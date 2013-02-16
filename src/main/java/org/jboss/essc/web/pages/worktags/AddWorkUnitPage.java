package org.jboss.essc.web.pages.worktags;

import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.dao.WorkTagDao;
import org.jboss.essc.web.model.WorkTag;
import org.jboss.essc.web.model.WorkUnit;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.wicketstuff.tagit.TagItTextField;


/**
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class AddWorkUnitPage extends BaseLayoutPage {

    @Inject private WorkTagDao daoWorkTag;
    
    // Data
    private WorkUnit newWorkUnit = new WorkUnit();

    // Components
    private FeedbackPanel feedbackPanel;

    
    public AddWorkUnitPage( PageParameters par ) {

        // Feedback
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId( true );
        feedbackPanel.setFilter( new ContainerFeedbackMessageFilter(this) );
        add(feedbackPanel);

        // Components
        Form<WorkUnit> form = new Form("form", new CompoundPropertyModel<WorkUnit>(new PropertyModel(this, "newWorkUnit")));
        
        form.add( new TextField("title"));
        
        form.add( new TagItTextField("tags", new TagsToStringModel(new PropertyModel(this, "tags"), this.daoWorkTag)) {
            @Override
            protected Iterable getChoices(String input) {
                return daoWorkTag.getTagStartingWith(input);
            }
        });
        
        form.add( new WorkUnitListingPanel("similarWorkUnits", 
                new SimilarWorkUnitsModel(new PropertyModel(this, "newWorkUnit"), daoWorkTag)) );

    }

    
    /**
     *  Model which takes String coming from the input and converts to List<WorkTag>.
     */
    private static class TagsToStringModel implements IModel<String> {
        
        private IModel<Iterable<WorkTag>> tagsModel;
        private WorkTagDao daoWorkTag;

        
        private TagsToStringModel(PropertyModel<Iterable<WorkTag>> tagsModel, WorkTagDao daoWorkTag) {
            this.tagsModel = tagsModel;
            this.daoWorkTag = daoWorkTag;
        }

        @Override public String getObject() {
            Iterable<WorkTag> tagObjects = tagsModel.getObject();
            return StringUtils.join(tagObjects.iterator(), " ");
        }

        @Override public void setObject(String inputValue) {
            List<WorkTag> tagsByNames = this.daoWorkTag.getTagsByNames(inputValue);
            tagsModel.setObject( tagsByNames );
        }

        @Override public void detach() {}
    }

    
    /**
     *  Model which provides a list of WorkUnits similar to the edited one.
     */
    private static class SimilarWorkUnitsModel implements IModel<List<WorkUnit>>{

        private final IModel<WorkUnit> compareToModel;
        private final WorkTagDao daoWorkTag;

        
        private SimilarWorkUnitsModel(PropertyModel<WorkUnit> compareToModel, WorkTagDao daoWorkTag) {
            this.daoWorkTag = daoWorkTag;
            this.compareToModel = compareToModel;
        }

        @Override public List<WorkUnit> getObject() {
            return this.daoWorkTag.getWorkUnitsSimilarTo( compareToModel.getObject() );
        }

        @Override public void setObject(List<WorkUnit> inputValue) {
        }

        @Override public void detach() {}

    }

}// class

package org.jboss.essc.web.pages.worktags;

import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.dao.WorkDao;
import org.jboss.essc.web.model.WorkUnit;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.wicketstuff.tagit.TagItTextField;


/**
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class AddWorkUnitPage extends BaseLayoutPage {

    @Inject private WorkDao daoWork;
    
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
        
        // Form
        Form<WorkUnit> form = new Form<WorkUnit>("form", new CompoundPropertyModel<WorkUnit>(new PropertyModel(this, "newWorkUnit"))){
            // Submit
            @Override protected void onSubmit() {
                WorkUnit wu = daoWork.createWorkUnit( this.getModelObject() );
                this.setModelObject(wu);
                info("Work unit created.");
            }
        };
        add(form);
        
        form.add( new RequiredTextField("title"));
        
        form.add( new TagItTextField("tags", new TagsToStringModel(new PropertyModel(this, "newWorkUnit.tags"), this.daoWork)) {
            @Override
            protected Iterable getChoices(String input) {
                return daoWork.getTagStartingWith(input);
            }
        });
        
        form.add( new TextArea<>("note") );
        
        add( new WorkUnitListingPanel("similarWorkUnits", 
                new SimilarWorkUnitsModel(new PropertyModel(this, "newWorkUnit"), daoWork)) );
    }// const

    
    
    
    /**
     *  Model which provides a list of WorkUnits similar to the edited one.
     */
    private static class SimilarWorkUnitsModel implements IModel<List<WorkUnit>>{

        private final IModel<WorkUnit> compareToModel;
        private final WorkDao daoWorkTag;
        private       int maxResults = 15;

        
        private SimilarWorkUnitsModel(PropertyModel<WorkUnit> compareToModel, WorkDao daoWorkTag) {
            this.daoWorkTag = daoWorkTag;
            this.compareToModel = compareToModel;
        }

        @Override public List<WorkUnit> getObject() {
            return this.daoWorkTag.getWorkUnitsSimilarTo( compareToModel.getObject(), maxResults );
        }

        @Override public void setObject(List<WorkUnit> inputValue) {
        }

        @Override public void detach() {}

    }

}// class

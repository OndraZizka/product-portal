package org.jboss.essc.web.pages.rel.co;

import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.essc.web.DAO.CommentDao;
import org.jboss.essc.web.DAO.DepChangeDao;
import org.jboss.essc.web.DAO.ReleaseDao;
import org.jboss.essc.web.model.MavenArtifact;
import org.jboss.essc.web.model.Release;


/**
 * Contains a list of products. Appears on the home page.
 * 
 * @author Ondrej Zizka
 */
public class DepsListBox extends Panel {
    
    @Inject private ReleaseDao daoRelease;
    @Inject private CommentDao     daoComment;
    @Inject private DepChangeDao   daoDepChange;

    // Components
    private FeedbackPanel feedbackPanel;
    
    
    public DepsListBox( String id, final IModel<Release> releaseModel ) {
        super( id );

        final Release release = releaseModel.getObject();

        this.setDefaultModel( releaseModel );

        
        // Dependencies
        add( new ListView<MavenArtifact>("deps", new PropertyModel(getModel(), "deps") ) {
            @Override
            protected void populateItem( ListItem<MavenArtifact> item ) {
                MavenArtifact ma = item.getModelObject();
                item.add( new Label("g", new PropertyModel(item.getModel(), "groupId")));
                item.add( new Label("a", new PropertyModel(item.getModel(), "artifactId")));
                item.add( new Label("c", "/" + ma.getClassifier()).setVisibilityAllowed( ! StringUtils.isBlank(ma.getClassifier()) ));
                item.add( new Label("v", new PropertyModel(item.getModel(), "version")));
                item.add( new Label("p", ma.getPackaging()).setVisibilityAllowed(! "jar".equals(ma.getPackaging())));
                //item.add( new Label("s", ma.getScope()).setVisibilityAllowed(! "compile".equals(ma.getScope())));
                item.add( new Label("s", ""));
            }
        });

    }// const()

    
    protected IModel<Release> getModel(){ return (IModel<Release>) this.getDefaultModel(); }
    
}// class

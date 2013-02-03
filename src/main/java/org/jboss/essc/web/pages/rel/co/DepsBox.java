package org.jboss.essc.web.pages.rel.co;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.essc.web.dao.CommentDao;
import org.jboss.essc.web.dao.DepChangeDao;
import org.jboss.essc.web.dao.ReleaseDaoBean;
import org.jboss.essc.web.model.DepChangeProposal;
import org.jboss.essc.web.model.MavenArtifact;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.pages.rel.DepsUploadForm;


/**
 * Contains a list of products. Appears on the home page.
 * 
 * @author Ondrej Zizka
 */
public class DepsBox extends Panel {
    
    @Inject private ReleaseDaoBean daoRelease;
    @Inject private CommentDao     daoComment;
    @Inject private DepChangeDao   daoDepChange;

    // Components
    private FeedbackPanel feedbackPanel;
    
    
    public DepsBox( String id, final IModel<Release> releaseModel ) {
        super( id );

        final Release release = releaseModel.getObject();

        this.setDefaultModel( releaseModel );

        
        // Heading
        add( new Label("productName", release.getProduct().getName()) );
        add( new Label("version",     release.getVersion()) );
        
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
                item.add( new Label("s", ma.getScope()).setVisibilityAllowed(! "compile".equals(ma.getScope())));
            }
        });

        // Feedback
        this.feedbackPanel = new FeedbackPanel("feedback");
        this.feedbackPanel.setOutputMarkupId( true );
        this.feedbackPanel.setFilter( new ContainerFeedbackMessageFilter(this) );
        add(feedbackPanel);

        // Propose version change.
        this.add( new Form("proposeChangeForm"){
            DepChangeProposal proposal = new DepChangeProposal(null, null, "Rationale");
                    
            final Form self = this;
            {
                // Select from existing deps
                List<MavenArtifact> deps = new LinkedList();
                deps.add( new MavenArtifact("New artifact", "", "") );
                deps.addAll( releaseModel.getObject().getDeps() );
                
                add( new DropDownChoice<MavenArtifact>("subjectGA",
                        new PropertyModel<MavenArtifact>(self, "proposal.subject"),
                        deps,
                        new IChoiceRenderer<MavenArtifact>() {
                    @Override public Object getDisplayValue( MavenArtifact object ) { return object.toStringGAV(); }
                    @Override public String getIdValue( MavenArtifact object, int index ) { return object.toStringGA(); }
                } ) );
                
                // New artifact?
                proposal.setNewGA("groupId:artifactId");
                add( new TextField("newGA", new PropertyModel<MavenArtifact>(self, "proposal.newGA")) );
                
                // New version
                add( new TextField("newVer", new PropertyModel<MavenArtifact>(self, "proposal.newVersion")) );

                // Rationale
                add( new TextArea("rationale", new PropertyModel<MavenArtifact>(self, "proposal.rationale")) );
            }

            @Override protected void onSubmit() {
                try {
                    daoDepChange.persist( proposal );
                    proposal = new DepChangeProposal();
                }
                catch( Exception ex ){
                    this.error( ex.toString() );
                }
            }
        });

        // Save as .properties
        this.add( new DepsDownloadLink("downloadProps", release, release.toStringIdentifier() + "-deps.csv") );
        
        // Upload & apply .properties
        this.add( new DepsUploadForm("uploadForm"){

            @Override protected void onSubmit() {
                List<MavenArtifact> deps;
                try {
                    deps = processDepsFromUploadedFile();
                }
                catch( IOException ex ) {
                    this.error( "Could not process CSV with dependencies: " + ex.toString() );
                    return;
                }
                
                try {
                    Release rel = (Release) DepsBox.this.getModel().getObject();
                    rel.setDeps(deps);
                    rel = daoRelease.update( rel );
                    DepsBox.this.setDefaultModelObject( rel );
                }
                catch( Exception ex ) {
                    this.error( "Could not save dependencies: " + ex.toString() );
                    return;
                }
            }
        });

    }

    
    protected IModel<Release> getModel(){ return (IModel<Release>) this.getDefaultModel(); }
    
}// class

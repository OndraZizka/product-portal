package org.jboss.essc.web.pages.rel.co;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import org.apache.wicket.Application;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.essc.web.WicketJavaEEApplication;
import org.jboss.essc.web.DAO.CommentDao;
import org.jboss.essc.web.DAO.DepChangeDao;
import org.jboss.essc.web.DAO.MavenArtifactDao;
import org.jboss.essc.web.DAO.ReleaseDao;
import org.jboss.essc.web.model.DepChangeProposal;
import org.jboss.essc.web.model.MavenArtifact;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.pages.rel.DepsUploadForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Contains a list of products. Appears on the home page.
 * 
 * @author Ondrej Zizka
 */
public class DepsActionsBox extends Panel {
    private static final Logger log = LoggerFactory.getLogger(DepsActionsBox.class);
    
    
    @Inject private ReleaseDao daoRelease;
    @Inject private CommentDao     daoComment;
    @Inject private DepChangeDao   daoDepChange;
    @Inject private MavenArtifactDao  daoMavenArtifact;

    
    // Components
    private FeedbackPanel feedbackPanel;
    
    
    public DepsActionsBox( String id, final IModel<Release> releaseModel ) {
        super( id );

        final Release release = releaseModel.getObject();

        this.setDefaultModel( releaseModel );

        
        // Heading
        add( new Label("productName", release.getProduct().getName()) );
        add( new Label("version",     release.getVersion()) );
        

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
                
                // Parse the uploaded file.
                List<MavenArtifact> deps;
                try {
                    deps = processDepsFromUploadedFile();
                    if( null == deps ){ this.error("No file was uploaded."); return; }
                    
                    // Replace the existing deps within the list.
                    Iterator<MavenArtifact> it = deps.iterator();
                    List<MavenArtifact> merged = new ArrayList<>(deps.size());
                    while( it.hasNext() ){
                        MavenArtifact ma = it.next();
                        MavenArtifact ma2 = daoMavenArtifact.findMavenArtifact(ma);
                        if( null != ma2 ){
                            it.remove();
                            merged.add(ma2);
                        } 
                        else merged.add(ma);
                    }
                    deps = merged;
                }
                catch( IOException | EJBTransactionRolledbackException ex ) {
                    this.error( "Could not process CSV with dependencies: " + ex.toString() );
                    return;
                }
                catch( Exception ex ){
                    String msg = WicketJavaEEApplication.getApp().isDevMode()
                            ? "Could not process CSV with dependencies: " + ex.toString()
                            : "Could not process CSV with dependencies. Error logged.";
                    this.error( msg );
                    log.error(msg, ex);
                    return;
                }
                
                
                // Persist the release with deps.
                try {
                    Release rel = (Release) DepsActionsBox.this.getModel().getObject();
                    rel.setDeps(deps);
                    rel = daoRelease.update( rel );
                    DepsActionsBox.this.setDefaultModelObject( rel );
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

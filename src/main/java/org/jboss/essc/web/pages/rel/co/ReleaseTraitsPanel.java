package org.jboss.essc.web.pages.rel.co;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.UrlValidator;
import org.jboss.essc.web.model.IHasTraits;
import org.jboss.essc.web.model.Product;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.model.ReleaseTraits;
import org.jboss.essc.wicket.UrlHttpRequestValidator;
import org.jboss.essc.wicket.UrlSimpleValidator;

import static org.jboss.essc.web.model.Release.Status;


/**
 *  A box with release traits OR product templates.
 * 
 * @author Ondrej Zizka
 */
public class ReleaseTraitsPanel extends Panel {
    
    // Components
    final FeedbackPanel feedbackPanel;
    
    // Validators
    UrlValidator urlFormatValidator = new UrlValidator();
    UrlSimpleValidator urlFormatSimpleValidator = new UrlSimpleValidator();
    UrlHttpRequestValidator urlHttpValidator = new UrlHttpRequestValidator();
    
    // Data
    private IHasTraits release;
    private boolean urlVerificationEnabled;


    
    // Wicket needs this :(
    public ReleaseTraitsPanel( String id, final Product prod ) {
        this( id, prod, 0 );
    }
    public ReleaseTraitsPanel( String id, final Release release ) {
        this( id, release, 0 );
    }
    public ReleaseTraitsPanel( String id, final IHasTraits release ) {
        this( id, release, 0 );
    }

    public ReleaseTraitsPanel( String id, final IHasTraits release, int foo ) {
        super(id);
        this.release = release;

        // Feedback
        this.feedbackPanel = new FeedbackPanel("feedback");
        this.feedbackPanel.setOutputMarkupId( true );
        this.feedbackPanel.setFilter( new ContainerFeedbackMessageFilter(this) );
        add(feedbackPanel);
        
        // Validators
        UrlValidator val = new UrlValidator();
        UrlHttpRequestValidator val2 = new UrlHttpRequestValidator();
        
        // Traits
        ReleaseTraits traits = this.release.getTraits();

        
        Model<IHasTraits> rm = new Model(release);
        
        this.add( new ReleaseTraitRowPanel("linkReleasedBinaries", ReleaseTraitRowPanel.Type.LINK, rm, "Released binaries",     Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkStagedBinaries",   ReleaseTraitRowPanel.Type.LINK, rm, "Staged binaries",       Status.STAGED, this));
        this.add( new ReleaseTraitRowPanel("linkReleasedDocs",     ReleaseTraitRowPanel.Type.LINK, rm, "Released docs",         Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkStagedDocs",       ReleaseTraitRowPanel.Type.LINK, rm, "Staged docs",           Status.STAGED, this));
        this.add( new ReleaseTraitRowPanel("linkJavadoc",          ReleaseTraitRowPanel.Type.LINK, rm, "Public API Javadoc",    Status.RELEASED, this));


        this.add( new ReleaseTraitRowPanel("linkIssuesFixed",      ReleaseTraitRowPanel.Type.LINK, rm, "Fixed issues",          Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkIssuesFound",      ReleaseTraitRowPanel.Type.LINK, rm, "Found issues",          Status.RELEASED, this));
        
        // Build
        this.add( new ReleaseTraitRowPanel("linkBuildHowto",       ReleaseTraitRowPanel.Type.LINK, rm, "Build HOWTO",           Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("gitHash",              ReleaseTraitRowPanel.Type.LABEL, rm, "Git hash",              Status.TAGGED, this));
        this.add( new ReleaseTraitRowPanel("linkGitRepo",          ReleaseTraitRowPanel.Type.LINK, rm, "Git repo",              Status.STAGED, this));
        this.add( new ReleaseTraitRowPanel("linkMead",             ReleaseTraitRowPanel.Type.LINK, rm, "Mead",                  Status.IN_PROGRESS, this));
        this.add( new ReleaseTraitRowPanel("linkBrew",             ReleaseTraitRowPanel.Type.LINK, rm, "Brew",                  Status.IN_PROGRESS, this));

        // Tests and reports
        this.add( new ReleaseTraitRowPanel("linkMeadJob",      ReleaseTraitRowPanel.Type.LINK, rm, "Jenkins MEAD job",          Status.IN_PROGRESS, this));
        this.add( new ReleaseTraitRowPanel("linkTattleTale",   ReleaseTraitRowPanel.Type.LINK, rm, "TattleTale",                Status.STAGED, this));
        this.add( new ReleaseTraitRowPanel("linkCodeCoverage", ReleaseTraitRowPanel.Type.LINK, rm, "Code coverage",             Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkTck",          ReleaseTraitRowPanel.Type.LINK, rm, "TCK tests",                 Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkCC",           ReleaseTraitRowPanel.Type.LINK, rm, "Common Criteria tests",     Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("link508",          ReleaseTraitRowPanel.Type.LINK, rm, "508 compliance tests",      Status.RELEASED, this));
        //this.add( new ReleaseTraitRowPanel("linkJavaEE",       ReleaseTraitRowPanel.Type.LINK, rm, "Java EE compliance tests",  Status.RELEASED, this));
        
        //this.add( new ReleaseTraitRowPanel("",      ReleaseTraitRowPanel.Type.LINK, rm, "",           Status.RELEASED, this));
        
        
        // Only show legend for Releases; not for Products.
        this.add( new WebMarkupContainer("legend").setVisible(release instanceof Release) );

    }// const


    /**
     *  Called when some of ReleaseTraitRowPanel's is ajax-updated.
     */
    protected void onTraitUpdate( ReleaseTraitRowPanel aThis, AjaxRequestTarget target ) {}


    //<editor-fold defaultstate="collapsed" desc="Get/Set">
    public boolean isUrlVerificationEnabled() { return urlVerificationEnabled; }
    public void setUrlVerificationEnabled( boolean urlVerificationEnabled ) { this.urlVerificationEnabled = urlVerificationEnabled; }
    //</editor-fold>

    
}// class

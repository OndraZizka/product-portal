package org.jboss.essc.web._cp.pageBoxes;

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
        
        this.add( new ReleaseTraitRowPanel("linkReleasedBinaries", rm, "Released binaries",     Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkStagedBinaries",   rm, "Staged binaries",       Status.STAGED, this));
        this.add( new ReleaseTraitRowPanel("linkReleasedDocs",     rm, "Released docs",         Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkStagedDocs",       rm, "Staged docs",           Status.STAGED, this));
        this.add( new ReleaseTraitRowPanel("linkJavadoc",          rm, "Public API Javadoc",    Status.RELEASED, this));


        this.add( new ReleaseTraitRowPanel("linkIssuesFixed",      rm, "Fixed issues",          Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkIssuesFound",      rm, "Found issues",          Status.RELEASED, this));
        
        // Build
        this.add( new ReleaseTraitRowPanel("linkBuildHowto",       rm, "Build HOWTO",           Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("gitHash",              rm, "Git hash",              Status.TAGGED, this));
        this.add( new ReleaseTraitRowPanel("linkGitRepo",          rm, "Git repo",              Status.STAGED, this));
        this.add( new ReleaseTraitRowPanel("linkMead",             rm, "Mead",                  Status.IN_PROGRESS, this));
        this.add( new ReleaseTraitRowPanel("linkBrew",             rm, "Brew",                  Status.IN_PROGRESS, this));

        // Tests and reports
        this.add( new ReleaseTraitRowPanel("linkMeadJob",      rm, "Jenkins MEAD job",          Status.IN_PROGRESS, this));
        this.add( new ReleaseTraitRowPanel("linkTattleTale",   rm, "TattleTale",                Status.STAGED, this));
        this.add( new ReleaseTraitRowPanel("linkCodeCoverage", rm, "Code coverage",             Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkTck",          rm, "TCK tests",                 Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkCC",           rm, "Common Criteria tests",     Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("link508",          rm, "508 compliance tests",      Status.RELEASED, this));
        this.add( new ReleaseTraitRowPanel("linkJavaEE",       rm, "Java EE compliance tests",  Status.RELEASED, this));
        
        //this.add( new ReleaseTraitRowPanel("",      rm, "",           Status.RELEASED, this));
        
        
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

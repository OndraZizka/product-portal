package org.jboss.essc.web._cp.pageBoxes;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
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
        
        this.add( new ReleaseTraitRowPanel("releasedBinaries", rm, "Released binaries",   "linkReleasedBinaries", Status.RELEASED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("stagedBinaries",   rm, "Staged binaries",     "linkStagedBinaries",   Status.STAGED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("releasedDocs",     rm, "Released docs",      "linkReleasedDocs",      Status.RELEASED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("stagedDocs",       rm, "Staged docs",        "linkStagedDocs",        Status.STAGED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("linkJavadoc",      rm, "Public API Javadoc", "linkJavadoc",           Status.RELEASED, this, feedbackPanel));


        this.add( new ReleaseTraitRowPanel("issuesFixed",      rm, "Fixed issues", "linkIssuesFixed",         Status.RELEASED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("issuesFound",      rm, "Found issues", "linkIssuesFound",         Status.RELEASED, this, feedbackPanel));
        
        // Build
        this.add( new ReleaseTraitRowPanel("linkBuildHowto",   rm, "Build HOWTO", "linkBuildHowto",           Status.RELEASED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("gitHash",          rm, "Git hash", "gitHash",                     Status.TAGGED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("gitRepo",          rm, "Git repo", "linkGitRepo",                 Status.STAGED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("mead",             rm, "Mead", "linkMead",                        Status.IN_PROGRESS, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("brew",             rm, "Brew", "linkBrew",                        Status.IN_PROGRESS, this, feedbackPanel));

        // Tests and reports
        this.add( new ReleaseTraitRowPanel("linkMeadJob",      rm, "Jenkins MEAD job", "linkMeadJob",         Status.IN_PROGRESS, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("linkTattleTale",   rm, "TattleTale", "linkTattleTale",            Status.STAGED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("linkCodeCoverage", rm, "Code coverage", "linkCodeCoverage",       Status.RELEASED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("linkTck",          rm, "TCK tests", "linkTck",                    Status.RELEASED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("linkCC",           rm, "Common Criteria tests", "linkCC",         Status.RELEASED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("link508",          rm, "508 compliance tests", "link508",         Status.RELEASED, this, feedbackPanel));
        this.add( new ReleaseTraitRowPanel("linkJavaEE",       rm, "Java EE compliance tests", "linkJavaEE",  Status.RELEASED, this, feedbackPanel));
        
        //this.add( new ReleaseTraitRowPanel("",      rm, "", "",           Status.RELEASED, this, feedbackPanel));
        
        
        // Only show legend for Releases; not for Products.
        this.add( new WebMarkupContainer("legend").setVisible(release instanceof Release) );

    }// const



    //<editor-fold defaultstate="collapsed" desc="Get/Set">
    public boolean isUrlVerificationEnabled() { return urlVerificationEnabled; }
    public void setUrlVerificationEnabled( boolean urlVerificationEnabled ) { this.urlVerificationEnabled = urlVerificationEnabled; }
    //</editor-fold>

    
}// class

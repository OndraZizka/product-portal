package org.jboss.essc.web.pages.rel;

import org.jboss.essc.web.pages.NotFoundPage;
import org.jboss.essc.web.pages.prod.ProductPage;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web._cp.pageBoxes.ReleaseBox;
import org.jboss.essc.web._cp.pageBoxes.ReleaseCustomFieldsPanel;
import org.jboss.essc.web.dao.ReleaseDaoBean;
import org.jboss.essc.web.model.ProductCustomField;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.pages.BaseLayoutPage;


/**
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class ReleasePage extends BaseLayoutPage {

    @Inject private ReleaseDaoBean releaseDao;

    // Data
    private Release release;

    // Components
    private FeedbackPanel feedbackPanel;

    
    public ReleasePage( PageParameters par ) {
        String prod =  par.get("product").toOptionalString();
        String ver = par.get("version").toOptionalString();
        
        try {
            this.release = releaseDao.getRelease( prod, ver );
        }
        catch( NoResultException ex ){
            // Redirect to NotFoundPage instead.
            String title = "Not found: " + prod + " " + ver;
            throw new RestartResponseAtInterceptPageException( new NotFoundPage(title) );
        }
        init();
    }

    public ReleasePage( Release release ) {
        if( release == null )
            throw new RestartResponseAtInterceptPageException( new NotFoundPage("Release not specified.") );
        this.release = release;
    }

    // Init components.
    private void init(){

        // Feedback
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId( true );
        feedbackPanel.setFilter( new ContainerFeedbackMessageFilter(this) );
        add(feedbackPanel);

        // Release box.
        add( new ReleaseBox("releaseBox", this.release) );

        // Custom fields.
        add( new ReleaseCustomFieldsPanel("customFields", new PropertyModel<Release>( this, "release") ){
            // Save release when changed.
            @Override
            protected void onAjaxChange( AjaxRequestTarget target, ListItem<ProductCustomField> item ) {
                onReleaseUpdate( target );
            }
        });
        
        
        // Danger Zone
        WebMarkupContainer dangerZone = new WebMarkupContainer("dangerZone");
        this.add( dangerZone );
        
        // Danger Zone Form
        dangerZone.add( new StatelessForm("form") {
            {
                // Really button
                final AjaxButton really = new AjaxButton("deleteReally") {};
                really.setVisible(false).setRenderBodyOnly(false);
                really.setOutputMarkupPlaceholderTag(true);
                add( really );
                
                // Delete button
                add( new AjaxLink("delete"){
                    @Override public void onClick( AjaxRequestTarget target ) {
                        target.add( really );
                        really.setVisible(true);
                    }
                });
            }
            @Override protected void onSubmit() {
                PageParameters params = ProductPage.createPageParameters(release.getProduct());
                releaseDao.remove( release );
                setResponsePage(ProductPage.class, params );
            }
        });
        
    }


    /**
     *  Called when some of sub-components were updated.
     *
     *  @param target  Ajax target, or null if not a result of AJAX.
     */
    private void onReleaseUpdate( AjaxRequestTarget target ) {
        if( target != null )  target.add( this.feedbackPanel );
        try {
            release = releaseDao.update( release );
            this.feedbackPanel.info("Release saved.");
            if( target != null )
                target.appendJavaScript("window.notifyFlash('Release saved.')");
        } catch( Exception ex ){
            this.feedbackPanel.info("Saving the release failed: " + ex.toString());
        }
    }


    /**  Helper - creates ReleasePage params for given release. */
    public static PageParameters createPageParameters( Release rel ){
        return new PageParameters()
            .add("product", rel.getProduct().getName())
            .add("version", rel.getVersion() );
    }

    
    public Release getRelease() { return release; }
    public void setRelease( Release release ) { this.release = release; }

}// class

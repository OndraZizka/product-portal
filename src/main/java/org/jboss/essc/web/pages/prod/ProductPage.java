package org.jboss.essc.web.pages.prod;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.ex.AuthException;
import org.jboss.essc.web._cp.PropertiesUploadForm;
import org.jboss.essc.web._cp.links.PropertiesDownloadLink;
import org.jboss.essc.web.pages.prod.co.CustomFieldsPanel;
import org.jboss.essc.web.pages.rel.co.ReleaseTraitRowPanel;
import org.jboss.essc.web.pages.rel.co.ReleaseTraitsPanel;
import org.jboss.essc.web.pages.prod.co.ReleasesBox;
import org.jboss.essc.web.dao.ProductDao;
import org.jboss.essc.web.model.Product;
import org.jboss.essc.web.model.ReleaseTraits;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.jboss.essc.web.pages.home.HomePage;
import org.jboss.essc.web.util.PropertiesUtils;
import org.jboss.essc.wicket.comp.editable.EditableLabel;


/**
 * ProductPage
 * 
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class ProductPage extends BaseLayoutPage {

    @Inject private ProductDao productDao;
    
    
    // Components
    private Form<Product> form;
    private FeedbackPanel feedbackPanel;

    // Data
    private Product product;

    
    public ProductPage( PageParameters par ) {
        String prodName = par.get("name").toString();
        try {
            this.product = productDao.getProductByName( prodName );
        }
        catch( NoResultException ex ){
            throw new RestartResponseException( HomePage.class, new PageParameters().add("error", "No such product: " + prodName) );
        }
        init();
    }

    public ProductPage( Product product ) {
        this.product = product;
        if( this.product == null )
            throw new RestartResponseException( HomePage.class, new PageParameters().add("error", "No product chosen.") );
        init();
    }
    
    private void init()
    {
        setDefaultModel( new PropertyModel( this, "product") );
        
        // Feedback
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId( true );
        feedbackPanel.setFilter( new ContainerFeedbackMessageFilter(this) );
        add(feedbackPanel);

        // Form
        this.form = new Form("form", new CompoundPropertyModel(getModel())) {
            @Override protected void onSubmit() {
                /*try {
                    doProductUpdate();
                } catch (Exception ex) {
                    error("Updating the product failed: " + ex.getMessage());
                }*/
                onProductUpdate(null);
            }
        };
        this.form.setVersioned(false);
        add( this.form );
        
        // Boxes
        add( new ReleasesBox("releasesBox", this.product, 100) );
        
        // Bugzilla ID
        this.form.add( new EditableLabel<String>("extIdBugzilla") );

        // Traits
        this.form.add( new ReleaseTraitsPanel("templates", this.product){
            @Override protected void onTraitUpdate( ReleaseTraitRowPanel row, AjaxRequestTarget target ) {
                ProductPage.this.onProductUpdate(target);
            }
        });

        // Custom fields
        this.form.add( new CustomFieldsPanel("customFields", new PropertyModel(this.product, "customFields"), feedbackPanel ){
            @Override protected void onChange( AjaxRequestTarget target ) {
                onProductUpdate( target );
            }
        });
        
        
        // Save as .properties - TODO
        this.add( new PropertiesDownloadLink("downloadProps", product.getTraits(), product.getName() + "-traits.properties") );

        // Upload & apply .properties
        this.add( new PropertiesUploadForm("uploadForm"){
            FileUploadField upload;

            @Override protected void onSubmit() {
                Properties props;
                try {
                    props = processPropertiesFromUploadedFile();
                }
                catch( IOException ex ) {
                    feedbackPanel.error( "Could not process properties: " + ex.toString() );
                    return;
                }
                
                ReleaseTraits traits = ((Product)getPage().getDefaultModelObject()).getTraits();
                PropertiesUtils.applyToObjectFlat( traits, props );
                onProductUpdate( null );
            }
        });

        
        
        final boolean isAdminLogged = getSession().isUserInGroup_Pattern("admin");
        
        // Admin Zone
        WebMarkupContainer adminZone = new WebMarkupContainer("adminZone");
        this.add( adminZone );
        adminZone.setVisibilityAllowed( isAdminLogged );
        adminZone.add( new Form("form")
            .add( new EditableLabel("editorsGroupPrefix", new PropertyModel<String>(this.product, "editorsGroupPrefix"))
                .add( new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override protected void onUpdate(AjaxRequestTarget target) {
                        if( ! isAdminLogged ){  error("Only members of the admin group can alter the privileges."); return; }
                        onProductUpdate(target);
                    }
                })
            )
        );
        
        
        
        // Danger Zone
        WebMarkupContainer dangerZone = new WebMarkupContainer("dangerZone");
        adminZone.setVisibilityAllowed( isAdminLogged );
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
                        //really.add(AttributeModifier.replace("style", "")); // Removes style="visibility: hidden".
                        //super.onSubmit( target, form );
                    }
                });
            }
            @Override protected void onSubmit() {
                if( ! productDao.canBeUpdatedBy( product, ProductPage.this.getSession().getUser() ) ){
                    error("You don't have permissions to delete this product.");
                    return; 
                }
                productDao.deleteIncludingReleases( product );
                setResponsePage(HomePage.class);
            }
        });

    }// init()


    /**
     *  Called when some of sub-components were updated - after a security check.
     *
     *  @param target  Ajax target, or null if not a result of AJAX.
     */
    private void onProductUpdate( AjaxRequestTarget target ) {
        if( target != null )  target.add( this.feedbackPanel );
        
        // Security check.
        if( ! productDao.canBeUpdatedBy( product, ProductPage.this.getSession().getUser() ) ){
            //throw new AuthException("You don't have permissions to modify this product's data.");
            error("You don't have permissions to modify this product's data.");
            return;
        }
        
        try {
            doProductUpdate();
            this.feedbackPanel.info("Product saved.");
            if( target != null )
                target.appendJavaScript("window.notifyFlash('Product saved.')");
        } catch( Exception ex ){
            this.feedbackPanel.info("Saving product failed: " + ex.toString());
        }
    }
    
    /**
     *  Actually saves the product.
     */
    private void doProductUpdate(){  // TODO: throws AuthException?
        product = productDao.update( product );
        modelChanged();
    }
    
    
    public static PageParameters createPageParameters( Product prod ){
        return new PageParameters().add("name", prod.getName());
    }

    
    
    public Product getProduct() { return product; }
    public void setProduct( Product product ) { this.product = product; }

    protected IModel<Product> getModel(){ return (IModel<Product>) this.getDefaultModel(); }

}// class
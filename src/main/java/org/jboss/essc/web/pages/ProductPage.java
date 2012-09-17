package org.jboss.essc.web.pages;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.jboss.essc.web._cp.pageBoxes.NoItemsFoundBox;
import org.jboss.essc.web._cp.pageBoxes.ReleaseTraitsBox;
import org.jboss.essc.web._cp.pageBoxes.ReleasesBox;
import org.jboss.essc.web.dao.ProductLineDaoBean;
import org.jboss.essc.web.model.ProductLine;


/**
 * Dynamic behavior for the ListContact page
 * 
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class ProductPage extends BaseLayoutPage {

    @Inject private ProductLineDaoBean productDao;
    
    private ProductLine product;

    
    public ProductPage( PageParameters par ) {
        try {
            this.product = productDao.getProductLineByName( par.get("name").toString() );
        }
        catch( NoResultException ex ){ /* remains null. */ }
        init();
    }

    public ProductPage( ProductLine product ) {
        this.product = product;
        init();
    }
    
    private void init(){
        if( this.product != null ){
            add( new ReleasesBox("releases", this.product, 100) );
            add( new ReleaseTraitsBox("templates", this.product) );
        }
        else {
            add( new NoItemsFoundBox("releases", "No product specified."));
            add( new WebMarkupContainer("templates"));
        }
    }
    
    
    
    /** Adds CSS reference. */
    public void renderHead(IHeaderResponse response) {
        //response.renderCSSReference(new PackageResourceReference(HomePage.class, "default/calendar.css"));
        response.renderCSSReference(new CssResourceReference( HomePage.class, "default.css" ));
    }

}
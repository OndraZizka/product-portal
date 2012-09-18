package org.jboss.essc.web.pages;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.jboss.essc.web._cp.ReleaseBox;
import org.jboss.essc.web._cp.pageBoxes.NoItemsFoundBox;
import org.jboss.essc.web.dao.ReleaseDaoBean;
import org.jboss.essc.web.model.ProductRelease;


/**
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class ReleasePage extends BaseLayoutPage {

    @Inject private ReleaseDaoBean releaseDao;
    
    private ProductRelease release;

    
    public ReleasePage( PageParameters par ) {
        String prod =  par.get("product").toOptionalString();
        String ver = par.get("version").toOptionalString();
        String titleIfNotFound = prod + " " + ver;
        try {
            this.release = releaseDao.getProductRelease( prod, ver );
        }
        catch( NoResultException ex ){ /* Release remains null. */ }
        init( titleIfNotFound );
    }

    public ReleasePage( ProductRelease release ) {
        this.release = release;
        init("Release not specified.");
    }
    
    private void init( String titleIfNotFound ){
        
        if( this.release != null ){
            add( new ReleaseBox("releaseBox", this.release) );
        }
        else {
            add( new NoItemsFoundBox("releaseBox", titleIfNotFound));
        }
    }
    

    public static PageParameters createPageParameters( ProductRelease rel ){
        return new PageParameters()
            .add("product", rel.getProduct().getName())
            .add("version", rel.getVersion() );
    }
    
}// class

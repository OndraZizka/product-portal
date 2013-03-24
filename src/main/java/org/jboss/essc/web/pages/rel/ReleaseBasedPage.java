package org.jboss.essc.web.pages.rel;

import org.jboss.essc.web.pages.NotFoundPage;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.DAO.ReleaseDao;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.pages.BaseLayoutPage;


/**
 * Base class for pages using Release as base object.
 * Use needs*() method to define what collections to fetch.
 *
 * TODO: Also move ReleasePage onto this.
 * 
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class ReleaseBasedPage extends BaseLayoutPage {

    @Inject protected ReleaseDao releaseDao;

    // Data
    protected Release release;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ReleaseBasedPage( PageParameters par ) {
        String prod =  par.get("product").toOptionalString();
        String ver = par.get("version").toOptionalString();
        
        try {
            this.release = releaseDao.getRelease( prod, ver, this.needsDeps() );
        }
        catch( NoResultException ex ){
            // Redirect to NotFoundPage instead.
            String title = "Not found: " + prod + " " + ver;
            throw new RestartResponseAtInterceptPageException( new NotFoundPage(title) );
        }
    }

    public ReleaseBasedPage( Release release ) {
        if( release == null )
            throw new RestartResponseAtInterceptPageException( new NotFoundPage("Release not specified.") );
        this.release = release;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.setDefaultModel( new PropertyModel(this, "release") );
    }





    /**  Helper - creates ReleasePage params for given release. */
    public static PageParameters createPageParameters( Release rel ){
        return new PageParameters()
            .add("product", rel.getProduct().getName())
            .add("version", rel.getVersion() );
    }

    
    public Release getRelease() { return release; }
    public void setRelease( Release release ) { this.release = release; }

    protected IModel<Release> getModel() {
        return new PropertyModel(this, "release");
    }


    // What collections of the entity this page needs?
    protected boolean needsDeps(){ return false; }

}// class

package org.jboss.essc.web;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import net.ftlines.wicket.cdi.CdiConfiguration;
import net.ftlines.wicket.cdi.ConversationPropagation;
import org.apache.wicket.*;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.PackageResourceGuard;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.jboss.essc.web.model.User;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.jboss.essc.web.pages.home.HomePage;
import org.jboss.essc.web.pages.prod.AddProductPage;
import org.jboss.essc.web.pages.prod.ProductPage;
import org.jboss.essc.web.pages.rel.AddReleasePage;
import org.jboss.essc.web.pages.rel.ReleaseDepsPage;
import org.jboss.essc.web.pages.rel.ReleasePage;
import org.jboss.essc.web.pages.statics.AboutPage;
import org.jboss.essc.web.pages.statics.Http404;
import org.jboss.essc.web.pages.test.AdminPage;
import org.jboss.essc.web.pages.test.JpaQueryPage;
import org.jboss.essc.web.pages.user.LoginPage;
import org.jboss.essc.web.pages.user.UserAccountPage;
import org.jboss.essc.web.pages.user.UserProfilePage;
import org.jboss.essc.web.pages.worktags.AddWorkUnitPage;
import org.jboss.essc.web.pages.worktags.WorkTagPage;
import org.jboss.essc.web.pages.worktags.WorkUnitPage;
import org.jboss.essc.web.qualifiers.CurrentSession;
import org.jboss.essc.web.qualifiers.LoggedIn;
import org.jboss.essc.web.qualifiers.ShowInternals;
import org.jboss.essc.web.security.EsscAuthSession;
import org.jboss.essc.web.security.SecuredPage;
import org.jboss.essc.web.test.EditableTestPage;

/**
 * Wicket application class.
 * 
 * @author Ondrej Zizka
 */
public class WicketJavaEEApplication extends WebApplication {

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    
    @Override
    protected void init() {
        super.init();

        // Enable CDI
        BeanManager bm;
        try {
            bm = (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
        } catch (NamingException e) {
            throw new IllegalStateException("Unable to obtain CDI BeanManager", e);
        }

        // Configure CDI, disabling Conversations as we aren't using them
        new CdiConfiguration(bm).setPropagation(ConversationPropagation.NONE).configure(this);

        // This would prevent Ajax components throwing an exception after session expiration.
        //this.getPageSettings().setRecreateMountedPagesAfterExpiry(false);
        
        //this.getApplicationSettings().setPageExpiredErrorPage(ErrorPage.class);
        this.getMarkupSettings().setStripWicketTags(true);
        //this.getResourceSettings().setThrowExceptionOnMissingResource( false ); // Fix: http://localhost:8080/essc-portal/release/EAP/HomePage.html?0
        //this.getPageSettings().setVersionPagesByDefault(false);
        
        
        // Mounts
        mountPage("/about", AboutPage.class);
        mountPage("/404",   Http404.class);
        mountPage("/login", LoginPage.class);
        mountPage("/user",  UserAccountPage.class);
        mountPage("/profile/#{name}",  UserProfilePage.class);
        mountPage("/test",  EditableTestPage.class);
        mountPage("/jpa",   JpaQueryPage.class);
        mountPage("/admin", AdminPage.class);
        
        mountPage("/addProduct",            AddProductPage.class);
        mountPage("/addRelease/#{product}", AddReleasePage.class);
        
        mountPage("/product/${name}",               ProductPage.class);
        mountPage("/release/${product}/${version}", ReleasePage.class);
        mountPage("/release/${product}/${version}/deps", ReleaseDepsPage.class);
        
        mountPage("/workTag/${name}", WorkTagPage.class);
        mountPage("/workUnit/${id}",  WorkUnitPage.class);
        mountPage("/addWorkUnit", AddWorkUnitPage.class);
     
        initResources();
        
        // Register the authorization strategy
        getSecuritySettings().setAuthorizationStrategy( new EsscAuthStrategy() );
        
    }// init()
    
    
    /**
     *  Initialize resources. Mostly registers images as shared resources.
     */
    void initResources(){
        //getSharedResources().add("favicon", new ContextRelativeResource("WEB-INF/classes/Leaf.ico"));
        //mountResource("/favicon.ico", new SharedResourceReference("favicon") );

        ResourceReference ref1 = new PackageResourceReference( BaseLayoutPage.class, "images/btn/AddProduct.png");
        ResourceReference ref3 = new PackageResourceReference( BaseLayoutPage.class, "images/btn/AddResource.png");
        getSharedResources().add("btn.AddProduct", ref1.getResource() );
        getSharedResources().add("btn.AddResource", ref3.getResource() );
        
        getSharedResources().add("ico.ExternalLink", new PackageResourceReference( BaseLayoutPage.class, "images/ico/ExternalLink.png").getResource() );
        
        IPackageResourceGuard guard = 
        new IPackageResourceGuard() {
            private final IPackageResourceGuard DEFAULT = new PackageResourceGuard();

            @Override
            public boolean accept(String path) {
                if( path.endsWith(".png") ) return true;
                if( path.endsWith(".jpg") ) return true;
                if( path.endsWith(".gif") ) return true;
                return DEFAULT.accept( path );
            }
        };
        getResourceSettings().setPackageResourceGuard( guard );
    }
    
    
    
    
    @Override
    public Session newSession( Request request, Response response ) {
        return new EsscAuthSession( request );
    }
    
    // CDI beans producers.
    @Produces @LoggedIn User getCurrentUser(){
        return ((EsscAuthSession) Session.get()).getUser();
    }

    @Produces @CurrentSession EsscAuthSession getCurrentSession(){
        return (EsscAuthSession) Session.get();
    }

    // Show internal releases?
    @Produces @ShowInternals Boolean isShowInternals(){
        EsscAuthSession sess = (EsscAuthSession) Session.get();
        User user = sess.getUser();
        if( user != null )   return user.isShowProd();
        
        return sess.getSettings().isShowInternalReleases();
    }
    
    public static WicketJavaEEApplication getApp(){
        return (WicketJavaEEApplication) Application.get();
    }
    
    
    public boolean isDevMode(){
        return RuntimeConfigurationType.DEVELOPMENT == Application.get().getConfigurationType();
    }

    
}// class WicketJavaEEApplication





/**
 *  Authorize instantiation of SecuredPage-marked only for logged users.
 *  @author ondra
 */
class EsscAuthStrategy implements IAuthorizationStrategy {

    public boolean isActionAuthorized( Component component, Action action ) {
        // authorize everything
        return true;
    }


    public <T extends IRequestableComponent> boolean isInstantiationAuthorized( Class<T> componentClass ) {
        // Check if the new Page requires authentication (implements the marker interface)
        if( SecuredPage.class.isAssignableFrom( componentClass ) ) {
            // Is user signed in?
            if( ((EsscAuthSession) Session.get()).isSignedIn() ) {
                return true;
            }

            // Intercept the request, but remember the target for later.
            // Invoke Component.continueToOriginalDestination() after successful logon to
            // continue with the target remembered.
            throw new RestartResponseAtInterceptPageException( LoginPage.class );
        }

        // okay to proceed
        return true;
    }

    @Override
    public boolean isResourceAuthorized(IResource arg0, PageParameters arg1) {
        return true;
    }
    
}// class EsscAuthStrategy

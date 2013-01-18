package org.jboss.essc.web.pages;

import org.apache.wicket.Session;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.jboss.essc.web._cp.pagePanes.HeaderPanel;
import org.jboss.essc.web._cp.pagePanes.SidebarPanel;
import org.jboss.essc.web.security.EsscAuthSession;
import org.jboss.essc.wicket.favicon.FavIconLink;


/**
 *  Base layout of all pages in this app.
 * 
 *  @author Ondrej Zizka
 */
public class BaseLayoutPage extends WebPage {


    // Set up the dynamic behavior for the page, widgets bound by id
    public BaseLayoutPage() {

        // Favicon
        add( new FavIconLink("favicon", "favicon.ico") );
        
        add( new DebugBar("debugBar") );
        
        add( new HeaderPanel("header") );
        
        add( new SidebarPanel("sidebar") );
        
    }
    
    
    /**
     *  Global helper to avoid casting everywhere.
     */
    @Override
    public EsscAuthSession getSession(){
        return (EsscAuthSession) Session.get();
    }
    
    
    /** Adds CSS reference. */
    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(new CssResourceReference( BaseLayoutPage.class, "default.css" ));
        response.renderJavaScriptReference(new JavaScriptResourceReference( BaseLayoutPage.class, "common.js" ));
    }

}// class

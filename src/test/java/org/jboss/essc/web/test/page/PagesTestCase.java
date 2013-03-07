
package org.jboss.essc.web.test.page;

import org.apache.wicket.util.tester.WicketTester;
import org.jboss.essc.web.pages.statics.AboutPage;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.jboss.essc.web.pages.home.HomePage;
import org.junit.Test;

/**
 *
 * @author Ondrej Zizka
 */
public class PagesTestCase {
    
    @Test
    public void testBaseLayoutPage(){
        WicketTester wt = new WicketTester(BaseLayoutPage.class);
        //WicketTester wt = new WicketTester( new WicketJavaEEApplication() );
        wt.startPage( new BaseLayoutPage() );
        wt.assertRenderedPage(BaseLayoutPage.class);
    }

    // To make CDI work in tests:
    // (12:03:03 PM) splatch: ozizka-ntb you need to set up a cdi container first to bind BeanManager and so on, without it wicket tester will fail
    // (12:03:35 PM) splatch: ozizka-ntb you can take a look to deltaspike project, it's relatively easy to start choosen CDI provider (weld or open web beans) without any additional logic
    // (12:04:44 PM) splatch: ozizka-ntb https://github.com/apache/camel/blob/trunk/components/camel-cdi/src/test/java/org/apache/camel/cdi/CdiTestSupport.java
    
    // (07:41:32 PM) ozizka-FN: How can I use WicketTester with CDI integration?
    // (07:41:48 PM) ozizka-FN: Or, in general - how can I test pages with CDI injection offline?
    // (07:52:31 PM) ivaynberg: we have two ways here
    // (07:52:43 PM) ivaynberg: once is an integration test where we start up weld and run the test
    // (07:52:49 PM) ivaynberg: the other is a mock test
    // (07:53:00 PM) ivaynberg: where we register beans in a map
    // (07:53:11 PM) ivaynberg: and then walk through a class, look for @Inject, and set values from that map
        
    // http://wicketopia.sourceforge.net/xref-test/org/wicketopia/cdi/TestCdiInjector.html
        
    @Test
    public void testHomePage(){
        WicketTester wt = new WicketTester(HomePage.class);
        wt.startPage( new HomePage() );
        wt.assertRenderedPage(HomePage.class);
    }

    @Test
    public void testAboutPage(){
        WicketTester wt = new WicketTester(AboutPage.class);
        wt.startPage( new AboutPage() );
        wt.assertRenderedPage(AboutPage.class);
    }

}// class

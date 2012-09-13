package org.jboss.essc.web.pages;

import java.util.List;
import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.dao.ProductLineDaoBean;
import org.jboss.essc.web.dao.ProductReleaseDaoBean;
import org.jboss.essc.web.model.Contact;
import org.jboss.essc.web.model.ProductLine;

/**
 *
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class AddReleasePage extends BaseLayoutPage {

    @Inject private ProductReleaseDaoBean prodRelDao;
    @Inject private ProductLineDaoBean prodDao;

    // Components
    private Form<Contact> insertForm;

    // Data
    private ProductLine line;
    private String version;
    

    
    public AddReleasePage(PageParameters params) {
        
        String proj = params.get("project").toOptionalString();
        add(new Label("titleReleaseOf", proj == null ? "" : " of " + proj ));
        
        add(new FeedbackPanel("feedback"));

        this.insertForm = new Form<Contact>("form") {

            @Override
            protected void onSubmit() {
                prodRelDao.addProductRelease( line, version );
                setResponsePage(HomePage.class);
            }
        };

        this.insertForm.add(new RequiredTextField<String>("version", new PropertyModel<String>(this, "version")));
        this.insertForm.add(new DropDownChoice("line", new ProjectsLDM(), new PropertyModel<String>(this, "line")));
        add(this.insertForm);
    }

    
    
    
    public ProductLine getLine() { return line; }
    public void setLine( ProductLine line ) { this.line = line; }
    public String getVersion() { return version; }
    public void setVersion( String version ) { this.version = version; }

    
    // ProjectsLDM 
    private class ProjectsLDM extends LoadableDetachableModel<List<ProductLine>> {

        public ProjectsLDM() {
        }

        @Override
        protected List<ProductLine> load() {
            return prodDao.getProductLines_orderName(0);
        }
    }// class
    

}
    

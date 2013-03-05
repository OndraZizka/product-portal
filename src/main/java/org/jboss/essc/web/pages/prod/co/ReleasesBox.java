package org.jboss.essc.web.pages.prod.co;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web._cp.links.ProductLink;
import org.jboss.essc.web._cp.links.ReleaseLink;
import org.jboss.essc.web.dao.ReleaseDao;
import org.jboss.essc.web.model.Product;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.model.User;
import org.jboss.essc.web.pages.rel.AddReleasePage;
import org.jboss.essc.web.security.EsscAuthSession;


/**
 * A list of releases.
 * 
 * @author Ondrej Zizka
 */
public class ReleasesBox extends Panel {

    @Inject protected ReleaseDao dao;
    

    protected int numReleases = 6;
    
    private Product forProduct;
    
    
    public ReleasesBox( String id, int numReleases ) {
        this( id, null, numReleases );
    }
    
    
    public ReleasesBox( String id, Product forProduct, int numReleases ) {
        super(id);
        this.setRenderBodyOnly( true );
        
        this.forProduct = forProduct;
        this.numReleases = numReleases;
        
        final boolean showProductCol = this.forProduct == null;
        final boolean showInternalReleases = this.isShowInternalStuff();

        //List<Release> releases = this.loadReleases( showInternalReleases );
        // TODO: Convert to LDM.
        
        // Model - Releases list LDM
        IModel<List<Release>> releasesModel = new LoadableDetachableModel<List<Release>>(){
            @Override
            protected List<Release> load() {
                return loadReleases(isShowInternalStuff());
            }
        };
        
        
        // Heading
        add( new Label("heading", "Releases" + (showProductCol ? "" : " of " + this.forProduct.getName() ) ) );
        add( new WebMarkupContainer("productTH").setVisible( showProductCol ) );
        
        // Releases table
        add( new ListView<Release>("rows", releasesModel)
        {
            // Populate the table of releases
            @Override
            protected void populateItem( final ListItem<Release> item) {
                final Release rel = item.getModelObject();
                
                // CSS for releases row - differentiate internal from public.
                if( showInternalReleases )  // Only if necessary.
                    item.add( new AttributeAppender("class", rel.isInternal() ? "internal" : "public") );
                
                //item.add( new Label("product", pr.getProduct().getName()).setVisible(ReleasesBox.this.forProduct == null) );
                item.add( new WebMarkupContainer("productTD")
                        .add( new ProductLink("productLink", rel.getProduct()) )
                        .setVisible(showProductCol)
                );
                item.add( new ReleaseLink("versionLink", rel));
                
                // Status
                String plannedFor = rel.getPlannedFor() == null ? "" : " "+ rel.formatPlannedFor() + " ("+ rel.formatPlannedForRelative()+")";
                String status = rel.getStatus() == null ? "" : rel.getStatus().getStatusString() + " " + plannedFor;
                item.add( new Label("status", status));
                
                // Links
                final String downloadUrl = rel.getTraits().getLinkReleasedBinaries();
                final String installUrl  = null;

                item.add( new WebMarkupContainer("links")
                    // Download
                    .add( new ExternalLink("download", downloadUrl ).add(new Image("img", "buttonDownload.png"))
                        .setVisibilityAllowed( null != downloadUrl ) )

                    // Install - TODO
                    .add( new ExternalLink("install", installUrl ).add(new Image("img", "buttonDownload.png"))
                        .setVisibilityAllowed( null != installUrl ) )

                    // Traits
                    .add( new ListView<LabelAndLink>("repeater", createLinksList(rel)){
                            @Override protected void populateItem( ListItem<LabelAndLink> item ) {
                                LabelAndLink ll = item.getModelObject();
                                item.add(new ExternalLink("link", ll.link, ll.label) );
                            }
                        }
                    )
                );
                
            }// populateItem() - Populate the table of releases

        });
        
        // "Add" link
        add( new WebMarkupContainer("add") .add( 
                forProduct == null 
                  ? new WebMarkupContainer("link")
                  : new BookmarkablePageLink("link", AddReleasePage.class, new PageParameters().add("product", this.forProduct.getName()) ) 
                )
                .setVisibilityAllowed(this.forProduct != null)
        );
        
    }// const
    
    
    /**
     *  Overridable - loads the releases to show.
     */
    protected List<Release> loadReleases( boolean showInternal ){
        if( this.forProduct == null )
            return dao.getReleases_orderDateDesc(this.numReleases, showInternal);
        else
            return dao.getReleasesOfProduct( this.forProduct, showInternal );
    }
    

    /**
     * Creates a list of link components for a set traits.
     */
    private static List<LabelAndLink> createLinksList( Release rel ) {
        List<LabelAndLink> links = new ArrayList<LabelAndLink>();
        //addLinkIfNotNull( links, "Release", rel.getTraits().getLinkReleasedBinaries()); // We have the Download button.
        addLinkIfNotNull( links, "Docs", rel.getTraits().getLinkReleasedDocs());
        addLinkIfNotNull( links, "Tests", rel.getTraits().getLinkMeadJob());
        addLinkIfNotNull( links, "Tattle", rel.getTraits().getLinkTattleTale());
        addLinkIfNotNull( links, "How to build", rel.getTraits().getLinkBuildHowto());
        //links.add( new LinkAndLabel( "", rel.getTraits().getLink()) );
        return links;
    }
    
    private static void addLinkIfNotNull( List<LabelAndLink> links, String label, String link ) {
        if( null != link)
            links.add( new LabelAndLink(label, link) );
    }

    /**
     *  Returns true if the internal releases etc should be shown.
     *  Determined by user's preference or session.settings if no user is signed in.
     */
    private boolean isShowInternalStuff() {
        EsscAuthSession sess = (EsscAuthSession)getSession();

        // User setting has precedence.
        User user = sess.getUser();
        if( user != null)  return user.isShowProd();
        
        // Session
        return sess.getSettings().isShowInternalReleases();
    }


}// class



class LabelAndLink implements Serializable {
    
    public final String label;
    public final String link;

    public LabelAndLink( String label, String link ) {
        this.label = label;
        this.link = link;
    }
}

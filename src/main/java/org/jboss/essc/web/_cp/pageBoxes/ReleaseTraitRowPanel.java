
package org.jboss.essc.web._cp.pageBoxes;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.UrlValidator;
import org.jboss.essc.web.model.IHasTraits;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.wicket.UrlHttpRequestValidator;
import org.jboss.essc.wicket.UrlSimpleValidator;
import org.jboss.essc.wicket.comp.editable.EditableLink4;
import org.jboss.essc.wicket.comp.editable.EditableLinkActivator;


/**
 *  Trait row colored according to the release status and stage when this trait is needed.
 * @author ozizka@redhat.com
 */
class ReleaseTraitRowPanel extends Panel {
    
    private IModel<IHasTraits> relModel;

    
    // Validators
    UrlValidator urlFormatValidator = new UrlValidator();
    UrlSimpleValidator urlFormatSimpleValidator = new UrlSimpleValidator();
    UrlHttpRequestValidator urlHttpValidator = new UrlHttpRequestValidator();


    /**
     *  Defaults 'shouldBe' to the value of 'mustBe'.
     */
    public ReleaseTraitRowPanel( String id, IModel<IHasTraits> relModel, String label,
            Release.Status mustBe,
            ReleaseTraitsPanel rp, FeedbackPanel feedback )
    {
        this( id, relModel, label, mustBe, mustBe, rp, feedback );
    }
    
    /**
     * 
     * @param id        Component ID, and also the property of the ReleaseTrait object.
     * @param relModel  Release model
     * @param label     Label of the trait
     * @param mustBe    Stage at which it must be filled.
     * @param shouldBe  Stage at which it should be filled.
     * @param rp        ReleaseTraitsPanel - backref. Needed for AJAX calls.
     * @param feedback  Feedback panel. TODO: Is it needed?
     */
    public ReleaseTraitRowPanel( String id, IModel<IHasTraits> relModel, String label, 
            Release.Status mustBe, Release.Status shouldBe,  
            ReleaseTraitsPanel rp, FeedbackPanel feedback )
    {
        super(id, relModel);
        this.relModel = relModel;

        String prop = id;

        // Label
        this.add( new Label("name", label) );

        // EditableLink.
        PropertyModel<String> traitModel = new PropertyModel( relModel.getObject().getTraits(), prop);
        EditableLink4 link = new EditableLink4("link", traitModel){
            // Pass the change notification to upper level. TODO: Does Wicket do this automatically?
            @Override protected void onModelChanged() {
                ReleaseTraitRowPanel.this.onModelChanged();
            }
        };
        // URL validators.
        if( false ){
            link.add( urlFormatValidator );
            link.add( urlHttpValidator );
        } else {
            link.add( urlFormatSimpleValidator );
        }

        add( link );

        // Activator icon.
        add( new Image("iconEdit", "icoEdit.png").add( new EditableLinkActivator(link) ) );


        // For releases, colorize trait rows, showing the urgency to be filled.
        if( relModel.getObject() instanceof Release ){
            String val = traitModel.getObject();
            // Not filled yet.
            if( val == null || val.isEmpty() || val.contains("${ver") )
                this.add( new RowColorByStatusClassModifier( new PropertyModel<Release.Status>( relModel, "status" ), mustBe, shouldBe ));
        }
    }

    @Override
    protected void onModelChanged() {
        //getParent().onModelChanged();
        //this.relModel.
    }

}// class ReleaseTraitRowPanel



/**
 *  Class attribute modifier which colorizes rows according to release state and property represented by that row.
 *  The purpose is to highlight which rows should be filled at what stage.
 * 
 *  Should be == the stage at which the link should better be in place.
 *  Must be   == the stage at which the link must be in place.
 */
class RowColorByStatusClassModifier extends AttributeAppender {
    
    private IModel classNameModel;
    private IModel<Release.Status> statusModel;
    
    private Release.Status mustBe;
    private Release.Status shouldBe;
    
    
    public RowColorByStatusClassModifier( IModel<Release.Status> statusModel, Release.Status mustBe, Release.Status shouldBe ) {
        super( "class", new Model() );
        this.classNameModel = super.getReplaceModel();
        this.statusModel = statusModel;
        this.mustBe = mustBe;
        this.shouldBe = shouldBe;
    }

    
    @Override public void beforeRender( Component component ) {
        this.statusModel.getObject();
        
        this.classNameModel.setObject( computeClass( this.statusModel.getObject(), this.mustBe, this.shouldBe ) );
        super.beforeRender( component );
    }
    
    
    private static String computeClass( Release.Status relStatus, Release.Status mustBe, Release.Status shouldBe ){
        if( relStatus.ordinal() < shouldBe.ordinal() )
            return "notNeeded";
        
        if( relStatus == Release.Status.RELEASED && mustBe == Release.Status.STAGED )
            return "shouldHaveBeen";

        if( relStatus.ordinal() < mustBe.ordinal() )
            return "shouldBe";
        
        return "mustBe";
    }

}// class RowColorByStatusClassModifier

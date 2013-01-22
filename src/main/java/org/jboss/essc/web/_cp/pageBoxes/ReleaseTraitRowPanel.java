
package org.jboss.essc.web._cp.pageBoxes;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
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
import org.jboss.essc.wicket.InvalidCssClassAppender;
import org.jboss.essc.wicket.UrlHttpRequestValidator;
import org.jboss.essc.wicket.UrlSimpleValidator;
import org.jboss.essc.wicket.comp.editable.EditableLink4;
import org.jboss.essc.wicket.comp.editable.EditableLinkActivator;


/**
 *  Trait row colored according to the release status and stage when this trait is needed.
 * @author ozizka@redhat.com
 */
public class ReleaseTraitRowPanel extends Panel {
    
    private IModel<IHasTraits> relModel;

    
    // Validators
    private static UrlValidator urlFormatValidator = new UrlValidator();
    private static UrlSimpleValidator urlFormatSimpleValidator = new UrlSimpleValidator();
    private static UrlHttpRequestValidator urlHttpRequestValidator = new UrlHttpRequestValidator();


    /**
     *  Defaults 'shouldBe' to the value of 'mustBe'.
     */
    public ReleaseTraitRowPanel( String id, IModel<IHasTraits> relModel, String label,
            Release.Status mustBe,
            ReleaseTraitsPanel rp )
    {
        this( id, relModel, label, mustBe, mustBe, rp );
    }
    
    /**
     * 
     * @param id        Component ID, and also the property of the ReleaseTrait object.
     * @param relModel  Release model
     * @param label     Label of the trait
     * @param mustBe    Stage at which it must be filled.
     * @param shouldBe  Stage at which it should be filled.
     * @param rp        ReleaseTraitsPanel - backref. Needed for AJAX calls.
     */
    public ReleaseTraitRowPanel( String id, IModel<IHasTraits> relModel, String label, 
            Release.Status mustBe, Release.Status shouldBe,  
            ReleaseTraitsPanel rp )
    {
        super(id, relModel);
        this.relModel = relModel;

        this.setOutputMarkupId( true );

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
        add( link );

        // OnChange - persist.
        link.add( new AjaxFormComponentUpdatingBehavior("onchange"){
            @Override protected void onUpdate( AjaxRequestTarget target ) {
                ReleaseTraitRowPanel.this.onUpdate( target );
            }
        } );

        // URL validators.
        if( false ){
            link.add( urlFormatValidator );
            link.add( urlHttpRequestValidator );
        } else {
            link.add( urlFormatSimpleValidator );
        }

        // Feedback.
        add( new FeedbackPanel("feedback", new ComponentFeedbackMessageFilter( link ) ).setOutputMarkupId(true) );

        link.add( InvalidCssClassAppender.INSTANCE );


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

    /**
     *  Hands over the onModelChanged() calls to contained traits TextFields.
     *  Called only when VALIDATION passes.
     */
    @Override protected void onModelChanged() {
        //getParent().onModelChanged();
        //this.relModel.
        //throw new RuntimeException("debug - ReleaseTraitRowPanel.onModelChanged()");
    }

    /**
     *  AJAX - Called when one of contained trait TextFields are updated.
     *  Basically, hands over the onUpdate() call.
     */
    protected void onUpdate( AjaxRequestTarget target ) {
        //if( ! ((EditableLink4)this.get("link")).isValid() )
            //this.get("link").error("AAAAAAaaaaa!");
            //throw new RuntimeException("Not valid in onUpdate.");

        target.add( get("feedback") ); // Trying to display validation error, but doesn't work.
        
        if( ! (getParent() instanceof ReleaseTraitsPanel) ) return;
        ReleaseTraitsPanel rtp = (ReleaseTraitsPanel)getParent();
        rtp.onTraitUpdate(this, target);
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

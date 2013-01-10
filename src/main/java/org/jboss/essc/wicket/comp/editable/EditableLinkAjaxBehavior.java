
package org.jboss.essc.wicket.comp.editable;

import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;

/**
 *  A behavior which adds EditableLink initialization to onDomReady event,
 *  and then responses to the AJAX requests from it.
 *  @author Ondrej Zizka
 */
public class EditableLinkAjaxBehavior extends AbstractAjaxBehavior {

    @Override
	public void renderHead( final Component component, final IHeaderResponse response ){
        super.renderHead(component, response);
        
        component.setOutputMarkupId(true);
        String id = component.getMarkupId();

        // Template with variables. See https://github.com/wicketstuff/core/blob/master/jdk-1.6-parent/autocomplete-tagit-parent/autocomplete-tagit/src/main/resources/org/wicketstuff/tagit/res/tag-it.tmpl.js
        TextTemplate jsSrc = new PackageTextTemplate( EditableLinkAjaxBehavior.class, "EditableLinkAjaxBehavior.tpl.js");
        Map<String, CharSequence> variables = new HashMap<String, CharSequence>();
        variables.put("componentId", id);
        variables.put("callbackUrl", getCallbackUrl());

        String script = jsSrc.asString(variables);

        //response.render(OnDomReadyHeaderItem.forScript(script)); // Wicket 6
        response.renderOnDomReadyJavaScript(script);               // Wicket 5
    }

    @Override
    public void onRequest() {

        // Get the value sent from component via AJAX...
        RequestCycle requestCycle = getComponent().getRequestCycle();
        Request request = requestCycle.getRequest();
        String input = request.getRequestParameters().getParameterValue("val").toString();

        // And set it as new model value.
        getComponent().setDefaultModelObject( input );

        // Call onChange().
        if( getComponent() instanceof EditableLink ){
            ((EditableLink) getComponent()).onChange();
        }

        // Model value after onChange();
        String ret = getComponent().getDefaultModelObjectAsString();

        requestCycle.scheduleRequestHandlerAfterCurrent(new TextRequestHandler("text/plain", "UTF-8", ret));
    }

}


package org.jboss.essc.wicket.favicon;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.ExternalLink;

/**
 *
 *  @author Ondrej Zizka
 */
public class FavIconLink extends ExternalLink
{
    public FavIconLink( String id, String path ){
        super( id, path );
        add(new AttributeModifier("type", "image/x-icon"));
        add(new AttributeModifier("rel", "shortcut icon"));
        this.setContextRelative( true );
    }
}

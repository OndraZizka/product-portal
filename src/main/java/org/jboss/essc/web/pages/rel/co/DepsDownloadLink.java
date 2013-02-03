
package org.jboss.essc.web.pages.rel.co;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.time.Duration;
import org.jboss.essc.web.model.Release;


/**
 *
 * @author ozizka@redhat.com
 */
public class DepsDownloadLink extends DownloadLink {

    public DepsDownloadLink( String id, final Release rel, final String fileName ) {

        super( id, new AbstractReadOnlyModel<File>() {
            @Override public File getObject() {
                
                String csvString = StringUtils.join( rel.getDeps(), ":");
                
                try {
                    File tempFile = File.createTempFile( "ProdPortal-deps-" + fileName + "-", ".properties" );
                    InputStream data = new ByteArrayInputStream( csvString.getBytes() );
                    Files.writeTo( tempFile, data );
                    return tempFile;
                }
                catch( IOException e ) {
                    throw new RuntimeException( e );
                }
            }
        } );
        this.setCacheDuration( Duration.NONE );
        this.setDeleteAfterDownload( true );
    }

}// class

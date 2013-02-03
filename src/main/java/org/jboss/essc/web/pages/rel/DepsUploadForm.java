
package org.jboss.essc.web.pages.rel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.util.lang.Bytes;
import org.jboss.essc.web.model.MavenArtifact;


/**
 *
 * @author ozizka@redhat.com
 */
public class DepsUploadForm extends Form {
    
    private FileUploadField upload;

    
    public DepsUploadForm( String id ) {
        super( id );
        add( this.upload = new FileUploadField("file") );
        setMultiPart(true);
        setMaxSize(Bytes.kilobytes(50));
    }

    
    protected List<MavenArtifact> processDepsFromUploadedFile() throws IOException {
        FileUpload fu = this.upload.getFileUpload();
        if( null == fu ) return null;
        return processDeps( fu.getInputStream() );
    }

    
    /**
     *  Parses the lines of uploaded file as G:A:P:V:S.
     */
    public static List<MavenArtifact> processDeps( InputStream is ) throws IOException {
        List<MavenArtifact> deps = new LinkedList();
        try {
            // For each line...
            BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
            do {
                String line = br.readLine();
                if( line == null ) break;
                line = line.trim();
                // Output of mvn dependency:list contains some formatting.
                if( line.isEmpty() ) continue;
                if( line.equals("The following files have been resolved:")) continue;
                if( line.equals("none")) continue;
                
                deps.add( MavenArtifact.fromDepsPluginString( line ) );
            } while( true );
        }
        catch( IOException ex ) {
            throw ex;
        }
        return deps;
    }

}// class

package org.jboss.essc.web.model;

import java.io.Serializable;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;


/**
 *  Maven artifact. Releases have a list of dependencies.
 * 
 *  @author Ondrej Zizka
 */
@SuppressWarnings("serial")
@Entity @Table(name="artifact", uniqueConstraints = {
    @UniqueConstraint(name = "gav", columnNames = {"groupId", "artifactId", "version", "packaging", "classifier"})
})
public class MavenArtifact implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    // Unique pentad
    @Column(length = 80, nullable = false)
    private String groupId;
    @Column(length = 60, nullable = false)
    private String artifactId;
    @Column(length = 40, nullable = false)
    private String version;
    @Column(length = 10, nullable = false)
    private String packaging;
    @Column(length = 20, nullable = true)
    private String classifier;

    
    
    public MavenArtifact() { }

    public MavenArtifact( String groupId, String artifactId, String version ) {
        this(groupId, artifactId, version, "jar");
    }
    public MavenArtifact( String groupId, String artifactId, String version, String packaging ) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.packaging = packaging;
        this.version = version;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupId() { return groupId; }
    public void setGroupId( String groupId ) { this.groupId = groupId; }
    public String getArtifactId() { return artifactId; }
    public void setArtifactId( String artifactId ) { this.artifactId = artifactId; }
    public String getVersion() { return version; }
    public void setVersion( String version ) { this.version = version; }
    public String getClassifier() { return classifier; }
    public void setClassifier( String classifier ) { this.classifier = classifier; }
    public String getPackaging() { return packaging; }
    public void setPackaging(String packaging) { this.packaging = packaging; }    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hash/eq">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.groupId != null ? this.groupId.hashCode() : 0);
        hash = 67 * hash + (this.artifactId != null ? this.artifactId.hashCode() : 0);
        hash = 67 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 67 * hash + (this.classifier != null ? this.classifier.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        final MavenArtifact other = (MavenArtifact) obj;
        return true;
    }
    //</editor-fold>



    public String toStringGA() {
        return this.groupId + ":" + this.artifactId;
    }

    public String toStringGAV() {
        return this.groupId + ":" + this.artifactId + ":" + this.version;
    }


    /**
     *  Parses "org.jboss.xnio:xnio-nio:jar[:classifier]:3.0.7.GA:compile"
     *  org.sonatype.sisu:sisu-guice:jar:no_aop:3.0.3:compile
     */
    public static MavenArtifact fromDepsPluginString( String str ) {
        String[] parts = StringUtils.split( str, ":");
        if( parts.length < 3 )
            throw new IllegalArgumentException("Expected format: 'G:A:P[:C]:V:scope'");

        int offset = (parts.length == 6) ?  1  :  0; // Includes classifier.
        
        MavenArtifact ma = new MavenArtifact( parts[0], parts[1], parts[3+offset], parts[2] );
        if( parts.length >= 5 ){
            //ma.setScope(parts[4+offset] );
        }

        return ma;
    }

    /**
     *  Parses "org.jboss.xnio:xnio-nio:3.0.7.GA[:classifier]"
     */
    public static MavenArtifact fromGavsString( String gavc ) {
        String[] parts = StringUtils.split( gavc, ":");
        if( parts.length < 3 )
            throw new IllegalArgumentException("Expected format: 'G:A:V[:classifier]'. Was: "
                    + StringUtils.abbreviate(gavc, 80));

        MavenArtifact ma = new MavenArtifact( parts[0], parts[1], parts[2] );
        if( parts.length >= 4 )
            ma.setClassifier( parts[3] );
        //if( parts.length >= 5 )
        //    ma.setScope(parts[4] );

        return ma;
    }

    @Override
    public String toString() {
        return String.format("MavenArtifact #%d { GAVPC: %s:%s:%s:%s:%s }", groupId, artifactId, version, packaging, classifier);
    }

}// class

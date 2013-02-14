package org.jboss.essc.web.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 *  Dependency.
 * 
 *  @author Ondrej Zizka
 *  @deprecated  Currently using just @ManyToMany.
 */
@SuppressWarnings("serial")
@Entity @Table(name="dep")
public class Dependency implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;
    
    private MavenArtifact artifact;

    private String scope;
    private String note;
    
    
    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MavenArtifact getArtifact() { return artifact; }
    public void setArtifact(MavenArtifact artifact) { this.artifact = artifact; }
    
    public String getScope() { return scope; }
    public void setScope( String scope ) { this.scope = scope; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    //</editor-fold>

    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.artifact != null ? this.artifact.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Dependency other = (Dependency) obj;
        if (this.artifact != other.artifact && (this.artifact == null || !this.artifact.equals(other.artifact))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Dependency #" + id + "{ " + artifact + '}';
    }
    
    
    
}// class
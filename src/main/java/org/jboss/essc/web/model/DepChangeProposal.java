package org.jboss.essc.web.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.*;


/**
 *  Dependency change proposal.
 * 
 *  @author Ondrej Zizka
 */
@SuppressWarnings("serial")
@Entity @Table(name="depChangeProposal")
public class DepChangeProposal implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    // Either an existing dependency,
    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id", updatable = false)
    private MavenArtifact subject;
    
    // or a new one.
    private String newGA;
    
    private String newVersion;

    private String rationale;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "depChangeProposal_id", updatable = false, nullable = false)
    private List<Comment> comments = new LinkedList();

    
    public DepChangeProposal() { }

    public DepChangeProposal( MavenArtifact subject, String newVersion, String rationale ) {
        this.subject = subject;
        this.newVersion = newVersion;
        this.rationale = rationale;
    }

    
    //<editor-fold defaultstate="collapsed" desc="Get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MavenArtifact getSubject() { return subject; }
    public void setSubject( MavenArtifact subject ) { this.subject = subject; }
    public String getNewGA() { return newGA; }
    public void setNewGA(String newGA) { this.newGA = newGA; }    
    public String getNewVersion() { return newVersion; }
    public void setNewVersion( String newVersion ) { this.newVersion = newVersion; }
    public String getRatio() { return rationale; }
    public void setRatio( String ratio ) { this.rationale = ratio; }
    //</editor-fold>


    public String toString() {
        return String.format("%s => %s", subject.toStringGAV(), newVersion);
    }


}// class

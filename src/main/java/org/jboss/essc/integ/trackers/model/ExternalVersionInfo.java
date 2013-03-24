package org.jboss.essc.integ.trackers.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.ForeignKey;


@Entity
@Table(name = "extVersion",uniqueConstraints = @UniqueConstraint(columnNames = {"project_id","name"}))
public class ExternalVersionInfo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    @JsonIgnore
    private Long id;

    @JsonProperty("id")
    @Column(columnDefinition = "INT UNSIGNED", unique = true)
    private long externalId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "project_id")
    @ForeignKey(name = "project_id")
    private ExternalProjectInfo project;

    @Column(nullable = false)
    private String name;
    
    private boolean released;

    
    
    public ExternalVersionInfo() { }

    public ExternalVersionInfo(long externalId) {
        this.externalId = externalId;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public Long getId() { return id; }    
    public void setId(Long id) { this.id = id; }    
    public ExternalProjectInfo getProject() { return project; }    
    public void setProject(ExternalProjectInfo project) { this.project = project; }    
    public String getName() { return name; }    
    public void setName(String name) { this.name = name; }    
    public boolean isReleased() { return released; }    
    public void setReleased(boolean released) { this.released = released; }    
    public long getExternalId() { return externalId; }    
    public void setExternalId(long jiraId) { this.externalId = jiraId; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hash/eq">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (this.externalId ^ (this.externalId >>> 32));
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
        final ExternalVersionInfo other = (ExternalVersionInfo) obj;
        if (this.externalId != other.externalId) {
            return false;
        }
        return true;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return ExternalVersionInfo.class.getSimpleName() + " #" + id
                + ", extId #" + externalId + " " + name
                + " { project=" + project
                + ", released=" + released
                + "}";
    }
    
}// class

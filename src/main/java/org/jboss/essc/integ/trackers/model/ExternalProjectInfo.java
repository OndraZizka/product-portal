package org.jboss.essc.integ.trackers.model;

import java.util.List;
import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


//@Entity
@Table(name = "extProject")
public class ExternalProjectInfo {

    // Need to have our ID as we load from multiple trackers (JBoss Jira, Red Hat Bugzilla...)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    @JsonIgnore
    private Long id;
    
    // ID from External tracker.
    @Column(columnDefinition = "INT UNSIGNED")
    @JsonProperty("id")
    private Long externalId;
    
    @Column(unique = true, nullable = false)
    @JsonProperty("key")
    private String prefix;
    
    @JsonProperty("name")
    private String name;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ExternalVersionInfo> versions;
    
    @JsonIgnore
    //@Temporal(TemporalType.TIMESTAMP)
    private long lastUpdated;

    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getExternalId() { return externalId; }
    public void setExternalId(Long externalId) { this.externalId = externalId; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public List<ExternalVersionInfo> getVersions() { return versions; }
    public void setVersions(List<ExternalVersionInfo> versions) { this.versions = versions; }
    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="hash/eq">
    @Override
    public int hashCode() {
        int hash = 7;
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
        final ExternalProjectInfo other = (ExternalProjectInfo) obj;
        if ((this.prefix == null) ? (other.prefix != null) : !this.prefix.equals(other.prefix)) {
            return false;
        }
        return true;
    }
    
    //</editor-fold>
    
    
    

    @Override
    public String toString() {
        return "JiraProject {id=" + id
                + ", key=" + prefix
                + ", name=" + name
                + ", lastUpdated=" + lastUpdated
                + "}";
    }
}

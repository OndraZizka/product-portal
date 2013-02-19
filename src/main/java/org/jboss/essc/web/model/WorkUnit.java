package org.jboss.essc.web.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;


/**
 *  Work tag. Belongs to a different app.
 * 
 *  @author Ondrej Zizka
 */
@SuppressWarnings("serial")
@Entity @Table(name="workunit")
public class WorkUnit implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;
    
    private String title;
    private String note;
    private String url;
    
    /**
     *  Who created this work unit.
     */
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    /**
     *  Who participated.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(joinColumns = {@JoinColumn(name = "worker_id")}, inverseJoinColumns = {@JoinColumn(name = "workUnit_id")})
    @OrderBy("name")
    private Set<User> workers;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            uniqueConstraints = @UniqueConstraint(columnNames = {"tag_id", "workUnit_id"}),
            joinColumns = {@JoinColumn(name = "tag_id")}, inverseJoinColumns = {@JoinColumn(name = "workUnit_id")}
    )
    private Set<WorkTag> tags = new HashSet<>();
    
    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }
    public Set<WorkTag> getTags() { return tags; }
    public void setTags(Set<WorkTag> tags) { this.tags = tags; }
    //</editor-fold>

    public Set<User> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<User> workers) {
        this.workers = workers;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="hash/eq">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final WorkUnit other = (WorkUnit) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    //</editor-fold>
    

    @Override
    public String toString() {
        return "WorkUnit #" + id + " from " + created + " by " + author + " { name: " + title + '}';
    }

    public String getTagsAsString() {
        return StringUtils.join(this.getTags(), " ");
    }
    
}// class
package org.jboss.essc.web.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;


/**
 *  Work tag. Belongs to a different app.
 * 
 *  @author Ondrej Zizka
 */
@SuppressWarnings("serial")
@Entity @Table(name="worktag")
public class WorkTag implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;
    
    private String name;

    
    public WorkTag() { }
    
    public WorkTag(String name) {
        this.name = name;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    //</editor-fold>
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name == null ? null : this.name.toLowerCase());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final WorkTag other = (WorkTag) obj;
        return safeToLowerEquals(this.name, other.name);
    }
    
    

    @Override
    public String toString() {
        return "WorkTag #" + id + " { " + name + '}';
    }
    
    static boolean safeToLowerEquals( String a, String b ){
        if( a == b ) return true;
        if( a == null ) return false;
        if( b == null ) return false;
        return a.toLowerCase().equals(b.toLowerCase());
    }
}// class

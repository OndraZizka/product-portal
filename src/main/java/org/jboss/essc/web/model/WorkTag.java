package org.jboss.essc.web.model;

import java.io.Serializable;
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
    
    
    //<editor-fold defaultstate="collapsed" desc="get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    //</editor-fold>
    
    

    @Override
    public String toString() {
        return "WorkTag " + id + "{ " + name + '}';
    }
    
}// class

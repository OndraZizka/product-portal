package org.jboss.essc.web.model;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *  User
 * 
 *  @author Ondrej Zizka
 */
@SuppressWarnings("serial")
@Entity @Table(name="userGroup")
@XmlRootElement(name="userGroup")
public class UserGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;
    
    @Column(unique=true, nullable=false)
    private String name;
    
    @Column(unique=true)
    private String mail;
    
    
    //@ManyToMany 
    //@JoinTable(name = "usergroup_members")
    //private Set<User> members;
    
    
    
    // Const
    
    public UserGroup() {
    }

    public UserGroup( String name ) {
        this.name = name;
    }
    
    
    
    //<editor-fold defaultstate="collapsed" desc="Get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName( String name ) { this.name = name; }
    public String getMail() { return mail; }
    public void setMail( String mail ) { this.mail = mail; }

    //public Set<User> getMembers() { return members; }
    //public void setMembers(Set<User> members) { this.members = members; }    
    //</editor-fold>


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)  return true;
        if (obj == null)  return false;
        if (getClass() != obj.getClass())  return false;
        UserGroup other = (UserGroup) obj;
        
        if (name == null) {
            if (other.name != null)  return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }

    
    @Override
    public String toString() {
        return "User #" + id + "{ " + name + ", " + mail + '}';
    }
    
}
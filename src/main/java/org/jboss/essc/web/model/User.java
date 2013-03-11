package org.jboss.essc.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;


/**
 *  User
 * 
 *  @author Ondrej Zizka
 */
@SuppressWarnings("serial")
@Entity @Table(name="user")
@XmlRootElement(name="user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;
    
    @Column(unique=true, nullable=false)
    private String name;
    
    @Column(nullable=false, columnDefinition = "CHAR(32)", length = 32)
    @XmlTransient @JsonIgnore
    private String pass;
    
    @Column(columnDefinition = "CHAR(32)", length = 32)
    @XmlTransient @JsonIgnore
    private String passTemp;
    
    @Column(unique=true)
    private String mail;
    
    @Column(nullable=false)
    private boolean showProd;
    
    @ManyToMany 
    @JoinTable(name = "user_groups", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "group_id")})
    private Set<UserGroup> groups;

    
    
    public User() {
    }

    public User( String name, String pass ) {
        this.name = name;
        this.pass = pass;
    }
    
    
    /**
     *  Converts getGroups() to a list of names of the groups. Convenience method.
     */
    //@JsonIgnore @Transient
    public List<String> getGroupsNames(){
        if( getGroups() == null ) return null;
        List<String> names = new ArrayList<>(getGroups().size());
        for( UserGroup g : getGroups() )
            names.add( g.getName() );
        return names;
    }
    
    /**
     * @returns true if this user is at least in one group with given prefix.
     *          E.g.  prefix = "prod" matches "prod", "prod.eap", but not "products".
     */
    public boolean isInGroups(String groupPattern) {
        for( UserGroup g : getGroups() ){
            if( g.getName().startsWith(groupPattern) )
                return true;
        }
        return false;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMail() { return mail; }
    public void setMail( String mail ) { this.mail = mail; }
    public String getName() { return name; }
    public void setName( String name ) { this.name = name; }
    public String getPass() { return pass; }
    public void setPass( String pass ) { this.pass = pass; }
    /** Temporary; for password reset. */
    public String getPassTemp() { return passTemp; }
    public void setPassTemp(String passTemp) { this.passTemp = passTemp; }    

    public boolean isShowProd() { return showProd; }
    public void setShowProd( boolean showProd ) { this.showProd = showProd; }

    public Set<UserGroup> getGroups() { return groups; }
    public void setGroups(Set<UserGroup> groups) { this.groups = groups; }    
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="hash/eq">
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
        User other = (User) obj;
        
        if (name == null) {
            if (other.name != null)  return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }
    //</editor-fold>

    
    @Override
    public String toString() {
        return "UserGroup #" + id + "{ " + name + " / " + pass + ", " + mail + " showProd=" + showProd + '}';
    }

}

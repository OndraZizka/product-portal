package org.jboss.essc.web.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.serial.util.StringUtil;
import org.slf4j.LoggerFactory;


/**
 *  User
 * 
 *  @author Ondrej Zizka
 */
@SuppressWarnings("serial")
@Entity @Table(name="user")
@XmlRootElement(name="user")
public class User implements Serializable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(User.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;
    
    @Column(unique=true, nullable=false)
    private String name;
    
    @Transient @XmlTransient @JsonIgnore
    private transient String pass;
    
    @Column(name = "pass", nullable=false, columnDefinition = "CHAR(32)", length = 32)
    @XmlTransient @JsonIgnore
    private String passHash;
    
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
        this.setName(name);
        this.setPass(pass);
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
     * @returns true if this user is in the given group.
     */
    public boolean isInGroup(String groupName) {
        return getGroupsNames().contains( groupName );
    }
    
    
    /**
     * @returns true if this user is at least in one group with given prefix.
     *          E.g.  prefix = "prod" matches "prod", "prod.eap", but not "products".
     */
    public boolean isInGroups_Prefix(String groupPrefix) {
        if( StringUtils.isBlank(groupPrefix) )  return false;
        for( UserGroup g : getGroups() ){
            if( g.getName().startsWith(groupPrefix) )
                return true;
        }
        return false;
    }

    /**
     * @returns true if this user is at least in one group with given prefix.
     *          E.g.  prefix = "prod" matches "prod", "prod.eap", but not "products".
     */
    public boolean isInGroups_Pattern(String groupPattern) {
        if( StringUtils.isBlank(groupPattern) )  return false;
        
        for( UserGroup g : getGroups() ){
            if( matchesGroupPattern(g.getName(), groupPattern) );
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
    
    // Passwords.
    public String getPass() { return pass; }
    public void setPass( String pass ) { 
        this.pass = pass;
        this.setPassHash( md5(pass) ); 
    }
    public String getPassHash() { return passHash; }
    protected void setPassHash( String passHash ) { this.passHash = passHash; }
    public String rehashPass() { 
        String hash = md5(pass); 
        this.setPassHash( hash ); 
        return hash; 
    }
    
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

    private static final String md5(String pass) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(pass.getBytes());
            return Hex.encodeHexString(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unexpected: MD5 algorithm not found.");
        }
    }
    
    
    /**
     *   "admin"  matches just "admin"
     *   "prod."  matches "prod", "prod.eap", but not "product" or "product.foo"
     *   ".eap."  matches "prod.eap", "eap", "eap.prod" but not "reapers".
     */
    public static final boolean matchesGroupPattern( String groupName, String pattern ){
        
        if( null == groupName || groupName.length() == 0 || null == pattern || pattern.length() == 0)
            return false;
        
        // "admin"  matches just "admin".
        if( groupName.equals(pattern) ) return true;

        boolean dot1 = pattern.startsWith(".");
        boolean dot2 = pattern.endsWith(".");

        // No dots at borders.
        if( ! (dot1 || dot2) ) return false;
        
        // ".eap." -> "eap"
        String patCore = StringUtils.removeEnd(pattern, ".");
        patCore = StringUtils.removeStart(patCore, ".");
        
        
        boolean core1 = groupName.startsWith(patCore + ".");
        boolean core2 = groupName.endsWith("." + patCore + ".");
        
        // Dot at the end.
        if( ! dot1 ) return core1;
        // Dot at the start.
        if( ! dot2 ) return core2;

        // Dots at both sides.
        return core1 || core2 || groupName.contains(pattern);
        
    }
    
    // Alternative - use regex.
    private static final boolean matchesGroupPattern2( String groupName, String pattern ){
        boolean dot1 = pattern.startsWith(".");
        boolean dot2 = pattern.endsWith(".");

        // ".eap." -> "eap"
        String patCore = StringUtils.removeEnd(pattern, ".");
        patCore = StringUtils.removeStart(patCore, ".");
        
        String regex = patCore.replace(".", "\\.");
        if( dot1 )
            regex = "(.*\\.)?" + regex;
        if( dot1 )
            regex = regex + "(\\..*)?";
        return groupName.matches(regex);
    }
    

}// class

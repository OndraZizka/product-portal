package org.jboss.essc.web.model;

import java.io.Serializable;
import java.util.Map;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;


/**
 *  Product line - groups releases of the same product.
 * 
 *  @author Ondrej Zizka
 */
@SuppressWarnings("serial")
@Entity @Table(name="product")
@XmlRootElement(name="product")
public class Product implements Serializable, IHasTraits {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(unique=true)
    private String name;
    
    private String note;

    // External ID of this product - Jira and Bugzilla.
    private String extIdJira;
    private String extIdBugzilla;

    
    // ---- Traits ----
    
    @Embedded
    @XmlTransient
    @JsonIgnore
    private ReleaseTraits traits;


    // ---- Custom fields ---- mappedBy = "product",
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true )
    @JoinColumn(name = "product_id")
    @MapKey(name = "name")
    @XmlTransient @JsonIgnore
    private Map<String, ProductCustomField> customFields;
    
    
    public Product() {
    }

    public Product(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    
    
    //<editor-fold defaultstate="collapsed" desc="Get/set">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Map<String, ProductCustomField> getCustomFields() { return customFields; }
    public void setCustomFields( Map<String, ProductCustomField> customFields ) { this.customFields = customFields; }

    @XmlTransient
    @JsonIgnore
    public ReleaseTraits getTraits() { 
        if( traits == null )  traits = new ReleaseTraits(); // HHH-7610
        return traits; 
    }
    public void setTraits( ReleaseTraits traits ) { this.traits = traits; }
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
        Product other = (Product) obj;
        
        if (name == null) {
            if (other.name != null)  return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Product #"+ id + " \"" + name + "\", " + customFields.size() + " customFields }";
    }

}

package org.jboss.essc.web.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *  Custom field of a product - not known beforehand.
 *
 *  @author Ondrej Zizka
 */
@Entity
@Table(name = "prod_custField")
public class ProductCustomField implements Serializable {


    private Long id;

    /*// Unidirectional for now.
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", updatable = false) // mappedBy = "customFields" ?
    private Product product; /**/

    private String name;

    private String label;

    @Transient // Not in DB yet.
    public String getDefaultValue() {
        return "";
    }

    //<editor-fold defaultstate="collapsed" desc="get/set">
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id; }
    public void setId( Long id ) { this.id = id; }
    @Basic(optional = false)
    public String getName() { return name; }
    public void setName( String name ) { this.name = name; }
    @Basic(optional = false)
    public String getLabel() { return label; }
    public void setLabel( String label ) { this.label = label; }
    //</editor-fold>
    
    @Override
    public String toString() {
        return "ProductCustomField #" + id + " { " + name + " '" + label + "'}";
    }

}

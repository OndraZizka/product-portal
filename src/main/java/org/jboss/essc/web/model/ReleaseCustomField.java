
package org.jboss.essc.web.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 *
 *  @author Ondrej Zizka
 */
@Entity
@Table(name = "rel_custField", uniqueConstraints = {
    @UniqueConstraint(name = "rel_prodcf", columnNames = {"release_id", "field_id"})
})
public class ReleaseCustomField implements Serializable {

    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "release_id", nullable = false, updatable = false)
    private Release release;

    @ManyToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false, updatable = false)
    private ProductCustomField field;

    private String value;




    //<editor-fold defaultstate="collapsed" desc="get/set/overrides">
    public Long getId() { return id; }
    public void setId( Long id ) { this.id = id; }

    public Release getRelease() { return release; }
    public void setRelease( Release release ) { this.release = release; }
    public ProductCustomField getField() { return field; }
    public void setField( ProductCustomField field ) { this.field = field; }
    public String getValue() { return value; }
    public void setValue( String value ) { this.value = value; }


    @Transient
    public String getName() { return field.getName(); }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        final ReleaseCustomField other = (ReleaseCustomField) obj;
        if( this.release != other.release && (this.release == null || !this.release.equals( other.release )) ) {
            return false;
        }
        if( this.field != other.field && (this.field == null || !this.field.equals( other.field )) ) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        String prodName = release.getProduct().getName();
        String relVer   = release.getVersion();
        return "ReleaseCustomFields #" + id + " { " + prodName + ":" + relVer + ":" + field.getName() + " = " + value + " }";
    }
    //</editor-fold>
    
}

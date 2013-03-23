
package org.jboss.essc.web.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 *  @author Ondrej Zizka
 */
@Entity
@Table(name = "rel_custField", uniqueConstraints = {
    @UniqueConstraint(name = "rel_prodcf", columnNames = {"release_id", "field_id"})
})
public class ReleaseCustomField implements Serializable {

    @Id @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    // TBC: Make uni-dir?
    @ManyToOne(optional = false)
    @JoinColumn(name = "release_id", nullable = false, updatable = false) // Only at one side.
    @XmlTransient @JsonIgnore
    private Release release;

    @ManyToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false, updatable = false)
    @XmlTransient @JsonIgnore
    private ProductCustomField field;

    private String value;


    public ReleaseCustomField() {}

    public ReleaseCustomField( Release release, ProductCustomField field ) {
        this.setRelease( release );
        this.setField( field );
    }


    @Transient
    public String getEffectiveValue(){
        try {
            return this.value != null ? this.value : this.getField().toString();
        } catch ( NullPointerException ex ){
            return "FIXME NPE";
        }
    }


    //<editor-fold defaultstate="collapsed" desc="get/set">
    public Long getId() { return id; }
    public void setId( Long id ) { this.id = id; }

    public Release getRelease() { return release; }
    public void setRelease( Release release ) { this.release = release; }
    public ProductCustomField getField() { return field; }
    public void setField( ProductCustomField field ) {
        this.field = field;
        // Stupid workaround, see http://stackoverflow.com/questions/14587148/jpa-hibernate-not-storing-mapkeycolumns-value
        // TODO: Maybe change to property-access (annotations at getters) and get rid of name member?
        this.name = field == null ? null : field.getName();
    }
    public String getValue() { return value; }
    public void setValue( String value ) { this.value = value; }
    //</editor-fold>


    //@Transient
    private String name;
    //public String getName() { return field == null ? "(DEBUG - no field set)" : field.getName(); }
    //public void setName( String name ) { }



    //<editor-fold defaultstate="collapsed" desc="overrides">
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
        String prodName = release == null ? "-" : (release.getProduct() == null ? "" : release.getProduct().getName());
        String relVer   = release == null ? "-" : release.getVersion();
        return "ReleaseCustomFields #" + id + " { " + prodName + ":" + relVer + ":" + field.getName() + " = " + value + " }";
    }
    //</editor-fold>
    
}

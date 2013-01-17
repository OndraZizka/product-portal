
package org.jboss.essc.web.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *  Custom field of a product - not known beforehand.
 *
 *  @author Ondrej Zizka
 */
@Entity
@Table(name = "prod_custField")
public class ProductCustomField implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*// Unidirectional for now.
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", updatable = false) // mappedBy = "customFields" ?
    private Product product; /**/

    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    private String label;
    
}

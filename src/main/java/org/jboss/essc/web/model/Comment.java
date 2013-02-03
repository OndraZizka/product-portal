
package org.jboss.essc.web.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang.StringUtils;

/**
 *
 *  @author Ondrej Zizka
 */
@Entity @Table(name = "comment")
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Temporal( TemporalType.TIMESTAMP )
    private Date posted;

    
    public Comment() {
    }

    public Comment( User author, String text ) {
        this.author = author;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comment #" + id + "by " + author + " at " + posted + ": " + StringUtils.abbreviate(text, 30) + '}';
    }

}// class

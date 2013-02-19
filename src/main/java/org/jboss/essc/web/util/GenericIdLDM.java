package org.jboss.essc.web.util;

import javax.persistence.EntityManager;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *  Loads given entity from a persistence context. Needs to be managed.
 *  @author Ondrej Zizka
 */
public class GenericIdLDM<IdT, T> extends LoadableDetachableModel<T> {
    //public static final Logger log = LoggerFactory.getLogger(GenericIdLDM.class);

    //@PersistenceContext 
    private EntityManager em;

    private IdT id;
    private Class<T> cls;

    public GenericIdLDM(IdT id, Class<T> cls, EntityManager em) {
        this.id = id;
        this.cls = cls;
        this.em = em;
    }

    @Override protected T load() {
        return this.em.find(cls, id);
    }
        
}// class GenericIdLDM

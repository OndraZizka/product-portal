package org.jboss.essc.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  
 *  @author Ondrej Zizka
 */
public final class RestUtils {
    //public static final Logger log = LoggerFactory.getLogger(RestUtils.class);

    /**
     *  Map-based generic wrapper - needed for JSON client.
     */
    public static final List rewrap(List items, String wrapName) {
        List p2 = new ArrayList(items.size());
        for( Object item : items){
            Map map = new HashMap();
            map.put(wrapName, item);
            p2.add( map );
        }
        return p2;
    }

}// class RestUtils

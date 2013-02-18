package org.jboss.essc.web.pages.worktags;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.pages.BaseLayoutPage;

/**
 *
 * @author Ondrej Zizka
 */
public class WorkUnitPage extends BaseLayoutPage {

    static PageParameters params(Long id) {
        return new PageParameters().add("id", id);
    }
    
}// class

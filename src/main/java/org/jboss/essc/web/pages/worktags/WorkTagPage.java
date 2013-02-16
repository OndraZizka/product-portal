package org.jboss.essc.web.pages.worktags;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.model.WorkTag;
import org.jboss.essc.web.pages.BaseLayoutPage;

/**
 *
 * @author Ondrej Zizka
 */
public class WorkTagPage extends BaseLayoutPage {

    static PageParameters params(WorkTag tag) {
        return new PageParameters().add("name", tag.getName());
    }
    
}// class

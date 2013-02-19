package org.jboss.essc.web.util;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author Ondrej Zizka
 */
public class DefaultTextModel extends AbstractReadOnlyModel<String> {

    private final IModel<String> delegate;
    private final String def;

    public DefaultTextModel(String def, IModel delegate) {
        this.def = def;
        this.delegate = delegate;
    }

    public String getObject() {
        String s = delegate.getObject();
        return (Strings.isEmpty(s)) ? def : s;
    }
    
    public void detach() {
        delegate.detach();
    }

}// class DefaultTextModel

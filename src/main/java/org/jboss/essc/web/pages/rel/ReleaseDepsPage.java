package org.jboss.essc.web.pages.rel;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.pages.rel.co.DepsActionsBox;
import org.jboss.essc.web.pages.rel.co.DepsListBox;


/**
 * Shows release dependencies.
 *
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class ReleaseDepsPage extends ReleaseBasedPage {

    public ReleaseDepsPage( PageParameters par ) {
        super( par );
    }

    public ReleaseDepsPage( Release release ) {
        super( release );
    }

    // Init components.
    @Override
    protected void onInitialize() {
        super.onInitialize();

        add( new DepsActionsBox("actionsBox", this.getModel() ) );
        
        add( new DepsListBox("depsBox", this.getModel() ) );
    }


    @Override protected boolean needsDeps() { return true; }

}// class

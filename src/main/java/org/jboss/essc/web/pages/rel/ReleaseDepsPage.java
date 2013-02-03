package org.jboss.essc.web.pages.rel;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.pages.rel.co.DepsBox;


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

        add( new DepsBox("depsBox", this.getModel() ) );

    }


    @Override protected boolean needsDeps() { return true; }

}// class

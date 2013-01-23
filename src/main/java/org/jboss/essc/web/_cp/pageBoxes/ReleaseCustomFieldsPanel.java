package org.jboss.essc.web._cp.pageBoxes;

import java.util.List;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jboss.essc.web.model.ReleaseCustomField;

/**
 *
 *  @author Ondrej Zizka
 */
public class ReleaseCustomFieldsPanel extends Panel {

    public ReleaseCustomFieldsPanel( String id, IModel<List<ReleaseCustomField>> listModel ) {
        super( id, listModel );

        add( new ListView<ReleaseCustomField>("fieldsRows", listModel){
            @Override
            protected void populateItem( final ListItem<ReleaseCustomField> item ) {
                item.add( new ReleaseCustomFieldRowPanel("fieldRow", item.getModel()));
            }
        });

    }

    protected IModel<List<ReleaseCustomField>> getModel(){
        return (IModel<List<ReleaseCustomField>>) this.getDefaultModel();
    }

}

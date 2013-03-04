package org.jboss.essc.web.pages.worktags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.essc.web.dao.WorkDao;
import org.jboss.essc.web.model.WorkTag;

/**
 *  Model which takes String coming from the input and converts to List<WorkTag>.
 */
public class TagsToStringModel implements IModel<String> {
    
    private IModel<Set<WorkTag>> tagsModel;
    private WorkDao daoWorkTag;

    
    public TagsToStringModel(PropertyModel<Set<WorkTag>> tagsModel, WorkDao daoWorkTag) {
        this.tagsModel = tagsModel;
        this.daoWorkTag = daoWorkTag;
    }

    @Override
    public String getObject() {
        Iterable<WorkTag> tagObjects = this.tagsModel.getObject();
        //return StringUtils.join(tagObjects.iterator(), " ");
        StringBuilder sb = new StringBuilder();
        for (WorkTag workTag : tagObjects) {
            sb.append(workTag.getName()).append(" ");
        }
        return sb.substring(0, Math.max(0, sb.length()-1));
    }

    @Override
    public void setObject(String inputValue) {
        List<WorkTag> tagsByNames = this.daoWorkTag.loadOrCreateTagsByNames(StringUtils.split(inputValue));
        HashSet tags = new HashSet(tagsByNames);
        this.tagsModel.setObject(tags);
    }

    @Override public void detach() { }

}// class TagsToStringModel

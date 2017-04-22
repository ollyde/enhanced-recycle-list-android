package com.polydelic.oliverdixon.ollysenhancedlist.src.ListModels;

import com.polydelic.oliverdixon.ollysenhancedlist.R;
import com.polydelic.oliverdixon.ollysenhancedlist.src.IListModel;
import com.polydelic.oliverdixon.ollysenhancedlist.src.BaseViewHolder;

public class ListItemPaginationSpinner extends ListModelTemporary implements IListModel {

    @Override
    public int getViewLayoutId() {
        if (getCustomLayout() != null) {
            return getCustomLayout();
        }
        return R.layout.list_item_pagination;
    }

    @Override
    public Class getViewClass() {
        return BaseView.class;
    }

    /**
     * BaseView for this list item.
     */
    public static class BaseView extends BaseViewHolder {
        public BaseView(android.view.View createdView) {
            super(createdView);
        }
    }
}

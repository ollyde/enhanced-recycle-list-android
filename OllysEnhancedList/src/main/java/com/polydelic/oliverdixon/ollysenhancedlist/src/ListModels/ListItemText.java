package com.polydelic.oliverdixon.ollysenhancedlist.src.ListModels;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.polydelic.oliverdixon.ollysenhancedlist.R;
import com.polydelic.oliverdixon.ollysenhancedlist.src.IListModel;
import com.polydelic.oliverdixon.ollysenhancedlist.src.BaseViewHolder;

public class ListItemText extends ListModelTemporary implements IListModel {

    private boolean isStart;
    private String text;

    public ListItemText(String text, boolean isStart) {
        this.isStart = isStart;
        this.text = text;
    }

    @Override
    public int getViewLayoutId() {
        if (getCustomLayout() != null) {
            return getCustomLayout();
        }
        return R.layout.list_item_text;
    }

    @Override
    public Class getViewClass() {
        return BaseView.class;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isStart() {
        return isStart;
    }

    /**
     * BaseView for this list item.
     */
    public static class BaseView extends BaseViewHolder {

        private @Nullable TextView text;

        public BaseView(android.view.View createdView) {
            super(createdView);

            if (createdView.findViewById(R.id.list_item_text) != null) {
                text = (TextView) createdView.findViewById(R.id.list_item_text);
            }
        }

        @Override
        public void loadModel(Object model) {
            super.loadModel(model);

            if (this.text != null) {
                ListItemText listItemText = (ListItemText) model;
                this.text.setText(listItemText.getText());
            }
        }
    }
}

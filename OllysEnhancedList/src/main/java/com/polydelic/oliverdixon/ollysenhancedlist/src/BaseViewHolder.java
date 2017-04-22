package com.polydelic.oliverdixon.ollysenhancedlist.src;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private IListModel IListModelModel;
    public static final String VIEW_TAG = "BaseRecycleViewHolder_tag";

    private final View view;

    public BaseViewHolder(View createdView) {
        super(createdView);
        createdView.setTag(VIEW_TAG);
        this.view = createdView;
    }

    public void loadModel(Object model) {
        IListModelModel = (IListModel) model;
    }

    public View getView() {
        return view;
    }

    public IListModel getIListModel() {
        return IListModelModel;
    }

    /**
     * Set the click listeners for the recycle view item.
     *
     * @param IRecycleViewOnClickListener the listener that is fired.
     * @param viewIds the view ids you want to listen too.
     */
    public void setClickListeners(final IRecycleViewOnClickListener IRecycleViewOnClickListener, int[] viewIds) {
        for (int eachViewId : viewIds) {
            final View view = itemView.findViewById(eachViewId);

            // Might not exist if we are using multiple recycle view types.
            if (view != null) {

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IRecycleViewOnClickListener.viewClicked(com.polydelic.oliverdixon.ollysenhancedlist.src.IRecycleViewOnClickListener.PressTime.SHORT_PRESS, view, BaseViewHolder.this);
                    }
                });

                view.setLongClickable(true);
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        IRecycleViewOnClickListener.viewClicked(com.polydelic.oliverdixon.ollysenhancedlist.src.IRecycleViewOnClickListener.PressTime.LONG_PRESS, view, BaseViewHolder.this);
                        return true;
                    }
                });
            }
        }
    }
}

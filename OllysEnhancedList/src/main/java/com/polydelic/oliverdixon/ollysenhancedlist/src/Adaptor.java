package com.polydelic.oliverdixon.ollysenhancedlist.src;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polydelic.oliverdixon.ollysenhancedlist.src.ListModels.ListItemPaginationSpinner;
import com.polydelic.oliverdixon.ollysenhancedlist.src.ListModels.ListItemText;
import com.polydelic.oliverdixon.ollysenhancedlist.src.ListModels.ListModelTemporary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

@SuppressWarnings("unused")
public class Adaptor extends RecyclerView.Adapter<BaseViewHolder> {

    private final ArrayList<IListModel> listModels = new ArrayList<>();
    private final SparseArray<Class> viewClassReference = new SparseArray<>();

    // State for inserting click listeners into the recycle view.
    private @Nullable IRecycleViewOnClickListener IRecycleViewOnClickListener;
    private @Nullable int[] viewIds;

    private @Nullable Integer customListItemTextLayout;
    private @Nullable Integer customPaginationLayout;

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View newViewGroup = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        try {
            BaseViewHolder baseViewHolder = (BaseViewHolder) viewClassReference.get(viewType).getDeclaredConstructor(new Class<?>[]{View.class}).newInstance(newViewGroup);
            if (IRecycleViewOnClickListener != null && viewIds != null) {
                baseViewHolder.setClickListeners(IRecycleViewOnClickListener, viewIds);
            }
            return baseViewHolder;

        } catch (Exception e) {
            Constants.LogError("Failed to create view in enhanced list. View type: " + viewClassReference.get(viewType).getCanonicalName());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.loadModel(this.listModels.get(position));
    }

    @Override
    public int getItemViewType(int position) {

        IListModel iListModel = this.listModels.get(position);

        if (iListModel.getViewLayoutId() == 0 || iListModel.getViewClass() == null) {
            Constants.LogError("iListModel had a null view class, you must define one. " + iListModel.toString());
        }

        viewClassReference.put(iListModel.getViewLayoutId(), iListModel.getViewClass());
        return this.listModels.get(position).getViewLayoutId();
    }

    @Override
    public int getItemCount() {
        return this.listModels.size();
    }

    public void setItems(IListModel[] newItems) {
        Collections.addAll(this.listModels, newItems);
        notifyDataSetChanged();
    }

    /**
     * call sortListInformationItems when finished adding items.
     */
    public void addItem(IListModel baseListItem) {
        this.listModels.add(baseListItem);
        notifyItemInserted(listModels.size() - 1);
    }

    /**
     * Add items to the recycle view adaptor.
     */
    public void addItems(IListModel[] itemsToAdd) {

        Collections.addAll(this.listModels, itemsToAdd);

        // Don't use 'this' we use the new items as the size.
        notifyItemRangeInserted(this.listModels.size(), this.listModels.size() + itemsToAdd.length);
        sortListInformationItems();
    }

    public void clearItems() {
        this.listModels.clear();
        notifyDataSetChanged();
    }

    /**
     * Gets the number of list items excluding the temporary ones like pagination spinner or text. Good for returning the real number of items put in.
     */
    public synchronized int getItemCountExcludingTemporaryItemsAndText() {
        int count = 0;
        Iterator<IListModel> baseListItemIterator = listModels.iterator();
        if (baseListItemIterator.hasNext()) {
            do {
                IListModel baseListItem = baseListItemIterator.next();
                if (baseListItem instanceof ListModelTemporary) {
                    continue;
                }
                count++;
            } while (baseListItemIterator.hasNext());
        }
        return count;
    }

    /**
     * Adds pagination spinner at the end.
     */
    public void addEndPagination() {
        ListItemPaginationSpinner listItemPaginationSpinner = new ListItemPaginationSpinner();
        listItemPaginationSpinner.setCustomLayout(customPaginationLayout);
        this.listModels.add(listItemPaginationSpinner);
        notifyItemInserted(this.listModels.size() - 1);
    }

    private void addListInformationItem(String text, boolean isStart) {
        ListItemText itemListInformation = new ListItemText(text, isStart);
        itemListInformation.setCustomLayout(customListItemTextLayout);
        int placeToBe = getPlaceListInformationItemShouldBe(isStart);
        this.listModels.add(placeToBe, itemListInformation);
        notifyItemInserted(placeToBe);
    }

    private int getPlaceListInformationItemShouldBe(boolean isStart) {
        return isStart ? 0 : this.listModels.isEmpty() ? 0 : this.listModels.size();
    }

    /**
     * Sets the list information at the start
     * @param text set to "" (Empty to hide this)
     * @param isStart true means the list item at the beginning.
     */
    public synchronized void setListInformationItem(String text, boolean isStart) {

        if (text == null || text.isEmpty()) {
            removeListInformationItem(isStart);
            return;
        }

        int itemPos = this.getListInformationPos(isStart);

        if (itemPos != -1) {
            ListItemText listItemListInformation = (ListItemText) this.listModels.get(itemPos);
            listItemListInformation.setText(text);
            notifyItemChanged(itemPos);

        } else {
            addListInformationItem(text, isStart);
        }

        sortListInformationItems();
    }

    /**
     * Remove pagination and information text.
     */
    public void removeTemporaryListItems() {
        Iterator<IListModel> baseListItemIterator = listModels.iterator();
        int index = 0;
        while (baseListItemIterator.hasNext()) {
            IListModel baseListItem = baseListItemIterator.next();
            if (baseListItem instanceof ListModelTemporary) {
                baseListItemIterator.remove();
                notifyItemRemoved(index);
            }
            index++;
        }
    }

    private void removeListInformationItem(boolean isStart) {
        for (int index = 0; index < this.listModels.size(); index++) {
            if (this.listModels.get(index) instanceof ListItemText && ((ListItemText) this.listModels.get(index)).isStart() == isStart) {
                this.listModels.remove(index);
                notifyItemRemoved(index);
                return;
            }
        }
    }

    /**
     * @return -1 if not found.
     */
    private int getListInformationPos(boolean isStart) {
        for (int index = 0; index < this.listModels.size(); index++) {
            IListModel baseListItem = this.listModels.get(index);
            if (baseListItem instanceof ListItemText && ((ListItemText)baseListItem).isStart() == isStart) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Move the items to the correct positions. Search both top and bottom text items.
     */
    private void sortListInformationItems() {
        moveListInformationItemIfIncorrectPlace(true);
        moveListInformationItemIfIncorrectPlace(false);
    }

    @SuppressWarnings("ConstantConditions")
    private void moveListInformationItemIfIncorrectPlace(boolean isStart) {

        int currentPos = getListInformationPos(isStart);
        // Couldn't find.
        if (currentPos == -1) {
            return;
        }

        // If they are not in the correct place.
        if ((isStart && currentPos != 0) || (!isStart && currentPos != this.listModels.size() - 1)) {

            ListItemText viewHolderListInformationItem = (ListItemText) this.listModels.get(currentPos);

            this.listModels.remove(currentPos);
            int placeToBe = getPlaceListInformationItemShouldBe(isStart);
            this.listModels.add(placeToBe, viewHolderListInformationItem);
            notifyItemMoved(currentPos, placeToBe);
        }

    }

    /**
     * You must set this before setting the items.
     *
     * @param IRecycleViewOnClickListener the listener for clicks.
     * @param viewIds the view ids that were clicked. If set to null it will return the container view.
     *
     * Setting this will reset viewIds and any other click listeners.
     */
    public void setOnClickListeners(@Nullable int[] viewIds, @Nullable IRecycleViewOnClickListener IRecycleViewOnClickListener) {
        this.IRecycleViewOnClickListener = IRecycleViewOnClickListener;
        this.viewIds = viewIds;
    }

    @Nullable  public Integer getCustomListItemTextLayout() {
        return customListItemTextLayout;
    }

    /**
     * Set this to a XML layout to replace the default.
     *
     * @param customListItemTextLayout set to null if you want to use the default one.
     */
    public void setCustomListItemTextLayout(@Nullable Integer customListItemTextLayout) {
        this.customListItemTextLayout = customListItemTextLayout;
    }

    @Nullable public Integer getCustomPaginationLayout() {
        return customPaginationLayout;
    }

    /**
     * Set this to a XML layout to replace the default.
     *
     * @param customPaginationLayout set to null if you want to use the default one.
     */
    public void setCustomPaginationLayout(@Nullable Integer customPaginationLayout) {
        this.customPaginationLayout = customPaginationLayout;
    }

}

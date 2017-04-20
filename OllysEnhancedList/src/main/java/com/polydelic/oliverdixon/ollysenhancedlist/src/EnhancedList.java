package com.polydelic.oliverdixon.ollysenhancedlist.src;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.polydelic.oliverdixon.ollysenhancedlist.R;

import butterknife.BindView;

public class EnhancedList extends RelativeLayout {

    /**
     * The number that each request can take for a paginated list.
     *
     * I.e, if we have 100 items in total and we want to paginate by 20 each time; this would be 20.
     */
    private int paginationLimit = Integer.MAX_VALUE;

    /**
     * Max request per seconds.
     */
    private static final long MAX_REQUESTS_PER_MS = 500;
    private long lastRequest = 0;
    private boolean isMakingRequest = false;
    private boolean isListInformationAtStart = false;

    @BindView(R.id.base_swipe_refresh_view) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.base_recycle_view) BaseRecycleView baseRecycleView;

    // The information texts.
    private @Nullable String noItemsText;
    private @Nullable String noMoreItems;
    private @Nullable String gettingMoreItemsText;
    private @Nullable String moreItemsToGetText;
    private @Nullable String listDisabledText;
    private @Nullable String failedToGetItemsSnackText;
    private @Nullable String failedToGetItemsListText;

    public EnhancedList(Context context, AttributeSet attrs) {
        super(context, attrs);
        selfInit();
    }

    public EnhancedList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selfInit();
    }

    /**
     * @param beginsFromTop if the list starts and scrolls from the top, this is default to true. If you want something that goes upside-down use false.
     * @param noItemsText when the list has no items display this text.
     * @param noMoreItems when the list has no more items you can display this text at the end, can be overrided by moreItemsToGetText text var.
     * @param gettingMoreItemsText when the list is in the process of getting more items this will be shown at the end.
     * @param moreItemsToGetText when the list has retrieved more items BUT has more items to get (Say we have pagination set to get 20 items at a time and we get 20, then we know there could be more).
     * @param listDisabledText when the list has been disabled, show this text. This text would be overriden by no items image.
     * @param failedToGetItemsSnackText shows a snack with "retry" button if the list fails to get more items.
     * @param failedToGetItemsListText if we fail to get the list, display this text at the end of the list.
     * @param dataInterface we use this to return data to the list and also return status like failed, or feature disabled.
     */
    public void init(
        boolean beginsFromTop,
        @Nullable String noItemsText,
        @Nullable String noMoreItems,
        @Nullable String gettingMoreItemsText,
        @Nullable String moreItemsToGetText,
        @Nullable String listDisabledText,
        @Nullable String failedToGetItemsSnackText,
        @Nullable String failedToGetItemsListText,
        DataInterface dataInterface
    ) {

        this.beginsFromTop = beginsFromTop;
        this.dataInterface = dataInterface;

        // Set the texts
        this.noItemsText = noItemsText;
        this.noMoreItems = noMoreItems;
        this.gettingMoreItemsText = gettingMoreItemsText;
        this.moreItemsToGetText = moreItemsToGetText;
        this.listDisabledText = listDisabledText;
        this.failedToGetItemsListText = failedToGetItemsListText;
        this.failedToGetItemsSnackText = failedToGetItemsSnackText;

        selfInit();
    }

    /**
     * In case the users decides to init without any values. This is the default continues after init.
     */
    private void selfInit() {
        inflate(getContext(), R.layout.list_view_pagination, this);
    }

    public int getPaginationLimit() {
        return paginationLimit;
    }

    public void setPaginationLimit(int paginationLimit) {
        this.paginationLimit = paginationLimit;
    }

    public boolean isBeginsFromTop() {
        return beginsFromTop;
    }

    public void setBeginsFromTop(boolean beginsFromTop) {
        this.beginsFromTop = beginsFromTop;
    }

    @Nullable
    public String getNoItemsText() {
        return noItemsText;
    }

    public void setNoItemsText(@Nullable String noItemsText) {
        this.noItemsText = noItemsText;
    }

    @Nullable
    public String getNoMoreItems() {
        return noMoreItems;
    }

    public void setNoMoreItems(@Nullable String noMoreItems) {
        this.noMoreItems = noMoreItems;
    }

    @Nullable
    public String getGettingMoreItemsText() {
        return gettingMoreItemsText;
    }

    public void setGettingMoreItemsText(@Nullable String gettingMoreItemsText) {
        this.gettingMoreItemsText = gettingMoreItemsText;
    }

    @Nullable
    public String getMoreItemsToGetText() {
        return moreItemsToGetText;
    }

    public void setMoreItemsToGetText(@Nullable String moreItemsToGetText) {
        this.moreItemsToGetText = moreItemsToGetText;
    }

    @Nullable
    public String getListDisabledText() {
        return listDisabledText;
    }

    public void setListDisabledText(@Nullable String listDisabledText) {
        this.listDisabledText = listDisabledText;
    }

    @Nullable
    public String getFailedToGetItemsSnackText() {
        return failedToGetItemsSnackText;
    }

    public void setFailedToGetItemsSnackText(@Nullable String failedToGetItemsSnackText) {
        this.failedToGetItemsSnackText = failedToGetItemsSnackText;
    }

    @Nullable
    public String getFailedToGetItemsListText() {
        return failedToGetItemsListText;
    }

    public void setFailedToGetItemsListText(@Nullable String failedToGetItemsListText) {
        this.failedToGetItemsListText = failedToGetItemsListText;
    }

    /**
     * This class requests items to the listener, the listener returns the results.
     */
    public static abstract class DataInterface {
        public void requestItems(int pageIndex, DataResult dataResult) {}
    }

    /**
     * What we get back from the {@link DataInterface} as results. You must call something before requesting more data.
     */
    public interface DataResult {
        void gotBaseListItems(BaseListItem[] baseListItems);
        void featureDisabled();
        void failedToGetList();
        ListViewWithPagination getListViewWithPagination();
    }
}

package com.polydelic.oliverdixon.ollysenhancedlist.src;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.polydelic.oliverdixon.ollysenhancedlist.R;

@SuppressWarnings("unused")
public class EnhancedList extends RelativeLayout {

    /*
     * The number that each request can take for a paginated list.
     *
     * I.e, if we have 100 items in total and we want to paginate by 20 each time; this would be 20.
     */
    private int paginationPageAmount = Integer.MAX_VALUE;
    // The current pagination page.
    private int currentPageIndex = 0;
    // If pagination is disabled
    private boolean paginationDisabled = false;

    /**
     * Max request per seconds.
     */
    private static final long MAX_REQUESTS_PER_MS = 500;

    private long lastRequest = 0;
    private boolean isMakingRequest = false;
    private boolean isListInformationAtStart = false;
    private boolean beginsFromTop = true;

    // It's used to return to this class with results (new items, failed, disabled).
    private DataInterface dataInterface;

    // We can listen for data changes and callback with the total amount of items.
    private @Nullable Action<Integer> dataSetChangedListener;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycleView;

    // The information texts.
    private @Nullable String noItemsText;
    private @Nullable String noMoreItems;
    private @Nullable String gettingMoreItemsText;
    private @Nullable String moreItemsToGetText;
    private @Nullable String listDisabledText;
    private @Nullable String failedToGetItemsListText;

    private Adaptor adaptor;
    private boolean isInitialized = false;

    public EnhancedList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFromLayout(attrs);
    }

    public EnhancedList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromLayout(attrs);
    }

    private void initFromLayout(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EnhancedList);

        if (typedArray.hasValue(R.styleable.EnhancedList_noItems)) {
            noItemsText = typedArray.getString(R.styleable.EnhancedList_noItems);
        }
        if (typedArray.hasValue(R.styleable.EnhancedList_noMoreItems)) {
            noMoreItems = typedArray.getString(R.styleable.EnhancedList_noMoreItems);
        }
        if (typedArray.hasValue(R.styleable.EnhancedList_gettingMoreItemsText)) {
            gettingMoreItemsText = typedArray.getString(R.styleable.EnhancedList_gettingMoreItemsText);
        }
        if (typedArray.hasValue(R.styleable.EnhancedList_moreItemsToGetText)) {
            moreItemsToGetText = typedArray.getString(R.styleable.EnhancedList_moreItemsToGetText);
        }
        if (typedArray.hasValue(R.styleable.EnhancedList_listDisabledText)) {
            listDisabledText = typedArray.getString(R.styleable.EnhancedList_listDisabledText);
        }
        if (typedArray.hasValue(R.styleable.EnhancedList_failedToGetItemsListText)) {
            failedToGetItemsListText = typedArray.getString(R.styleable.EnhancedList_failedToGetItemsListText);
        }

        typedArray.recycle();
    }

    /**
     * @param beginsFromTop if the list starts and scrolls from the top, this is default to true. If you want something that goes upside-down use false.
     * @param noItemsText when the list has no items display this text.
     * @param noMoreItems when the list has no more items you can display this text at the end, can be overrided by moreItemsToGetText text var.
     * @param gettingMoreItemsText when the list is in the process of getting more items this will be shown at the end.
     * @param moreItemsToGetText when the list has retrieved more items BUT has more items to get (Say we have pagination set to get 20 items at a time and we get 20, then we know there could be more).
     * @param listDisabledText when the list has been disabled, show this text. This text would be overriden by no items image.
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

        selfInit();
    }

    /**
     * @param dataInterface we use this to return data to the list and also return status like failed, or feature disabled.
     */
    public void init(DataInterface dataInterface) {
        this.dataInterface = dataInterface;

        selfInit();
    }

    /**
     * @param beginsFromTop if the list starts and scrolls from the top, this is default to true. If you want something that goes upside-down use false.
     * @param dataInterface we use this to return data to the list and also return status like failed, or feature disabled.
     */
    public void init(boolean beginsFromTop, DataInterface dataInterface) {
        this.dataInterface = dataInterface;
        this.beginsFromTop = beginsFromTop;

        selfInit();
    }

    /**
     * In case the users decides to init without any values. This is the default continues after init.
     */
    private void selfInit() {

        if (isInitialized) {
            Constants.LogError("isInitialized is already true, did you try to initialize the enhanced list more than once?");
            return;
        }
        isInitialized = true;

        inflate(getContext(), R.layout.enhanced_list_view, this);
        // Bind views.
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_view_for_enhanced_list);
        recycleView = (RecyclerView) findViewById(R.id.recycle_view_for_enhanced_list);

        adaptor = new Adaptor();
        recycleView.setAdapter(adaptor);

        // Listen for when we reach the end of the list.
        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!isMakingRequest && dy > 0 && !recyclerView.canScrollVertically(1)) {
                    requestItems(false);

                    // Scroll to the bottom.
                    getRecycleView().scrollToPosition(getAdaptor().getItemCount() - 1);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!paginationDisabled) {
                requestItems(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    /**
     * The list requests more items.
     *
     * @param fromTop if the list should be from the top, I.E start the pagination again.
     */
    public void requestItems(final boolean fromTop) {

        // Limit the request time to one request per Nms (Because hit bottom/top list is fired when the list refreshes)
        if (lastRequest + MAX_REQUESTS_PER_MS > System.currentTimeMillis()) {
            return;
        }
        lastRequest = System.currentTimeMillis();

        // Stop making duplicate requests.
        if (isMakingRequest) {
            return;
        }
        isMakingRequest = true;

        // Must have data interface.
        if (dataInterface == null) {
            return;
        }

        // Were requesting from the top so set the page index to 0.
        if (fromTop) {
            currentPageIndex = 0;
        }

        // Were requesting more items. This also goes to the bottom of the list.
        if (currentPageIndex > 0) {
            adaptor.addEndPagination();
        }

        // Because were just about to request more items.
        adaptor.setListInformationItem(gettingMoreItemsText, isListInformationAtStart);

        // The resource will tell us when we got items.
        dataInterface.requestItems(currentPageIndex, new DataResult() {
            @Override
            public void gotBaseListItems(IListModel[] IListModels) {

                adaptor.removeTemporaryListItems();

                // Look out for null items
                for (IListModel iListModel : IListModels) {
                    if (iListModel == null) {
                        Constants.LogError("A null list item was entered into the list.");
                        Constants.printStackTrace();
                    }
                }

                // Clear the items if it was from top.
                if (fromTop) {
                    adaptor.clearItems();
                }

                if (currentPageIndex > 0) {
                    adaptor.addItems(IListModels);
                } else {
                    adaptor.setItems(IListModels);
                }

                if (IListModels.length > paginationPageAmount) {
                    adaptor.setListInformationItem(moreItemsToGetText, isListInformationAtStart);

                } else if (IListModels.length == 0 && adaptor.getItemCountExcludingTemporaryItemsAndText() == 0) {
                    // No items at all.
                    adaptor.setListInformationItem(noItemsText, isListInformationAtStart);
                } else {
                    // No more items
                    adaptor.setListInformationItem(noMoreItems, isListInformationAtStart);
                }

                currentPageIndex++;

                // Wait for the next cycle before requesting more items.
                swipeRefreshLayout.setRefreshing(false);
                isMakingRequest = false;

                // Must be last because sometimes the source will ask for more.
                callDataSetChangedListeners();
            }

            @Override
            public void featureDisabled() {

                adaptor.setItems(new IListModel[]{});
                adaptor.setListInformationItem(listDisabledText, isListInformationAtStart);

                // Wait for the next cycle before requesting more items.
                swipeRefreshLayout.setRefreshing(false);
                isMakingRequest = false;

                // Must be last because sometimes the source will ask for more.
                callDataSetChangedListeners();
            }

            @Override
            public void failedToGetList() {

                adaptor.removeTemporaryListItems();
                adaptor.setListInformationItem(failedToGetItemsListText, isListInformationAtStart);

                // Wait for the next cycle before requesting more items.
                swipeRefreshLayout.setRefreshing(false);
                isMakingRequest = false;

                // Must be last because sometimes the source will ask for more.
                callDataSetChangedListeners();
            }

            @Override
            public EnhancedList getEnhancedList() {
                return EnhancedList.this;
            }
        });
    }

    public void clear() {
        adaptor.setItems(new IListModel[]{});
        currentPageIndex = 0;
        adaptor.setListInformationItem(moreItemsToGetText, isListInformationAtStart);
    }

    private void callDataSetChangedListeners() {
        if (dataSetChangedListener != null) {
            dataSetChangedListener.invoke(adaptor.getItemCountExcludingTemporaryItemsAndText());
        }
    }

    /**
     * Listen for data set changes. Returns the number excluding temporary list items.
     */
    public void setDataSetChangedListener(@Nullable Action<Integer> dataSetChangedListener) {
        this.dataSetChangedListener = dataSetChangedListener;
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
        void gotBaseListItems(IListModel[] IListModels);
        void featureDisabled();
        void failedToGetList();
        EnhancedList getEnhancedList();
    }

    public Adaptor getAdaptor() {
        return adaptor;
    }

    //************
    // Getters and setters ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //************

    public int getPaginationPageAmount() {
        return paginationPageAmount;
    }

    public void setPaginationPageAmount(int paginationPageAmount) {
        this.paginationPageAmount = paginationPageAmount;
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
    public String getFailedToGetItemsListText() {
        return failedToGetItemsListText;
    }

    public void setFailedToGetItemsListText(@Nullable String failedToGetItemsListText) {
        this.failedToGetItemsListText = failedToGetItemsListText;
    }

    public boolean isPaginationDisabled() {
        return paginationDisabled;
    }

    public void setPaginationDisabled(boolean paginationDisabled) {
        this.paginationDisabled = paginationDisabled;
    }

    public RecyclerView getRecycleView() {
        return this.recycleView;
    }
}

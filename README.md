# Olly's Enhanced List view for Android

## Why did I create this?

I was rather frustrated by how much code was needed for simple recycle view lists and how limited they actually were in Android.
Also the lack of pagination and features was annoying to say the least.
 
So I created this simple enhanced list that makes working with lists and view holders in Android very easy indeed. It also includes 
optional pagination out of the box that is super easy to implement too.

- Handle any model type.
- Multiple view layouts per list.
- Neat way to handle clicks on list items and clicks within list items for specific elements.
- Pagination that is handled automatically.
- Has optional information texts.
- And more

## Easy Setup

Include the gradle line as a dependency.

```
compile 'com.polydelic.oliverdixon:ollsenhancedlist:1.00'
```

Include the layout where ever you need it.  
The string fields beginning with "app" are optional.  
They set what text should be displayed a certain intervals.
```xml
<com.polydelic.oliverdixon.ollysenhancedlist.src.EnhancedList
    android:id="@+id/test_enhanced_list"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:noItems="No items"
    app:noMoreItems="No more items"
    app:gettingMoreItemsText="Getting more items"
    app:moreItemsToGetText="More items to get"
    app:listDisabledText="List is disabled"
    app:failedToGetItemsListText="Failed to get items."
    />

```

Initialize the enhanced list class.  
Here we are saying the list starts from the top and we are setting data interface.  
The data interface simply requests giving a page index.  
Items that are returned to the dataResult must implement IListModel.
```java
enhancedList.init(true, new EnhancedList.DataInterface() {
    @Override
    public void requestItems(int pageIndex, EnhancedList.DataResult dataResult) {
        super.requestItems(pageIndex, dataResult);
   
        // Get more cars because the list if requesting it. 
        // Could be because of swipe down to refresh, pagination or initial load. 
        // Enhanced list will handle pagination amount automatically.
        getMoreCars(pageIndex, dataResult);
    }
});
// For custom control of layouts
enhancedList.getRecycleView().setLayoutManager(new LinearLayoutManager(getApplication()));

// Optional pagination amount. If not set this is set to Integer.MAX_VALUE
enhancedList.setPaginationPageAmount(10);
```

Example of IListModel class of car type.
```java
public class ListItemCar implements IListModel {

    private final String carName;

    public ListItemCar(String carName) {
        this.carName = carName;
    }

    @Override
    public int getViewLayoutId() {
        // Return the layout resource for this cell.
        return R.layout.list_item_car;
    }

    @Override
    public Class getViewClass() {
        // Return the view class to instantiate. Must extend BaseViewHolder and have the default constructor
        return CarListBaseView.class;
    }

    public String getCarName() {
        return carName;
    }

    // The view for the list item.
    public static class CarListBaseView extends BaseViewHolder {

        @BindView(R.id.car_name) TextView carNameTextView;

        public CarListBaseView(android.view.View createdView) {
            super(createdView);
            ButterKnife.bind(this, createdView);
        }

        @Override
        public void loadModel(Object model) {
            super.loadModel(model);

            ListItemCar listItemCar = (ListItemCar) model;
            carNameTextView.setText(listItemCar.getCarName());
        }
    }
}

```

So when we want to fill the list we can return items via the data result.  
Example of the getMoreCars function
```java
private void getMoreCars(int pageIndex, EnhancedList.DataResult dataResult) {
    // This would probably get results from an API using GSON with objects extending IListModel
    
    Array<IListModel> carsGot = (Data that implments IListModel);
    
    // Return some cars to the list.
    dataResult.gotBaseListItems(carsGot);
    
    // An example of when we get no more items, return an empty list.
    dataResult.gotBaseListItems(new IListModel[]{});
    
    // If we fail to get items
    dataResult.failedToGetList();
    
    // If the feature is disabled (Show one item with the text)
    dataResult.featureDisabled();
}
```

And now we have a working list with pagination :-)
![alt text](http://i.imgur.com/qQJriA3.gif "Working enhanced list")

### Easy Click/Press listeners in Android

Example of super easy on click listeners that support sub views inside the items. Something that was very messy before.  
Also includes the ability to listen for long and short presses very easily.
```java
// Example of super easy on click listeners that support sub views inside the items. Something that was very messy before.
// The first param is the ids we want to listen on. 
// We return the press time (long or short, the view that was clicked so we can compare view.getId() and the view holder to compare the class list item.
enhancedList.getAdaptor().setOnClickListeners(new int[]{ R.id.button_click }, (pressTime, viewClicked, viewHolder) -> {

    if (!(viewHolder instanceof ListItemCar.CarListBaseView)) {
        return;
    }

    final ListItemCar listItemCar = (ListItemCar) viewHolder.getIListModel();

    if (pressTime == IRecycleViewOnClickListener.PressTime.SHORT_PRESS) {
        Toast.makeText(MainActivity.this, "Button was short pressed for car: " + listItemCar.getCarName(), Toast.LENGTH_SHORT).show();
    } else if (pressTime == IRecycleViewOnClickListener.PressTime.LONG_PRESS) {
        Toast.makeText(MainActivity.this, "Button was long pressed for car: " + listItemCar.getCarName(), Toast.LENGTH_SHORT).show();
    }

});
```

### Custom text views and/or custom pagination view
If you want to set a custom layout for the text views or the pagination views there's a simple option.

```java
// Note the text view will look for an id of "list_item_text" and set the text there.
enhancedList.getAdaptor().setCustomListItemTextLayout(R.layout.list_item_text_custom); // << Text views
enhancedList.getAdaptor().setCustomPaginationLayout(R.layout.list_item_pagination_custom); // << Pagination
```

## Todo
- Optional view for empty lists. Say an image with text or whatever view clients want.
- Unit tests

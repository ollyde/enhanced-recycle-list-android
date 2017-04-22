package polydelic.oliverdixon.testlibraryollyslist;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.polydelic.oliverdixon.ollysenhancedlist.src.*;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import testollylist.polydelic.oliverdixon.testlibraryollyslist.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.test_enhanced_list) EnhancedList enhancedList;

    // Data.
    private List<ListItemCar> listItemCarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Make a list of car names.
        ArrayList<String> carNames = new ArrayList<String>() {{
            add("Lamborghini");
            add("Jeep");
            add("Acura");
            add("Alpha Sports");
            add("Amuza");
            add("Australian Six");
            add("Möve 101");
            add("Steyr");
            add("Apal");
            add("Bernardini");
            add("Pirin-Fiat");
            add("Sofia");
            add("67X");
            add("1893 Shamrock");
            add("Fleetwood-Knight");
            add("Studebaker of Canada");
            add("Russell Motor");
            add("Chang’an");
            add("Hummer");
        }};

        // Create the list items from the car names.
        listItemCarList = Stream.of(carNames)
            .map(ListItemCar::new)
            .collect(Collectors.toList())
        ;

        // Must init first.
        // There are several options here.
        enhancedList.init(true, new EnhancedList.DataInterface() {
            @Override
            public void requestItems(int pageIndex, EnhancedList.DataResult dataResult) {
                super.requestItems(pageIndex, dataResult);

                // Add a delay to show the use of pagination spinner
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    // Get more cars because the list if requesting it. Could be because of a few reasons (swipe down to refresh, pagination, initial load. Enhanced list will handle pagination amount).
                    getMoreCars(pageIndex, dataResult);

                }, 1000);
            }
        });
        enhancedList.setPaginationPageAmount(10);

        // For custom control of layouts
        enhancedList.getRecycleView().setLayoutManager(new LinearLayoutManager(getApplication()));

        // Example of setting custom views for text and pagination.
        // enhancedList.getAdaptor().setCustomListItemTextLayout(R.layout.list_item_text_custom);
        // enhancedList.getAdaptor().setCustomPaginationLayout(R.layout.list_item_pagination_custom);

        // Example of super easy on click listeners that support sub views inside the items. Something that was very messy before.
        // The first param is the ids we want to listen on. We return the press time (long or short, the view that was clicked so we can compare view.getId() and the view holder to compare the class list item.
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

        // Request the data fires "requestItems"
        enhancedList.requestItems(true);
    }

    private void getMoreCars(int pageIndex, EnhancedList.DataResult dataResult) {

        // We request more data here. We get the current page were looking in too.
        // Usually this would be much simpler as an API request to some function that returns GSON items that implement IListModel

        int startingPoint = (pageIndex * enhancedList.getPaginationPageAmount());
        int endingPoint = startingPoint + enhancedList.getPaginationPageAmount();

        // No more items.
        if (startingPoint > listItemCarList.size()) {
            dataResult.gotBaseListItems(new IListModel[]{});
            return;
        }

        // Were out of items, return the rest if any.
        if (endingPoint >= listItemCarList.size()) {
            List<ListItemCar> cars = listItemCarList.subList(startingPoint, listItemCarList.size());
            dataResult.gotBaseListItems(cars.toArray(new IListModel[cars.size()]));
            return;
        }

        // We have items but they maybe more.
        List<ListItemCar> cars = listItemCarList.subList(startingPoint, endingPoint);
        dataResult.gotBaseListItems(cars.toArray(new IListModel[cars.size()]));

        // We can also call
        // dataResult.failedToGetList();
        // Or
        // dataResult.featureDisabled();
    }
}

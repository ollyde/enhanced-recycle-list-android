package polydelic.oliverdixon.testlibraryollyslist;

import android.widget.TextView;

import com.polydelic.oliverdixon.ollysenhancedlist.src.BaseViewHolder;
import com.polydelic.oliverdixon.ollysenhancedlist.src.IListModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import testollylist.polydelic.oliverdixon.testlibraryollyslist.R;

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

package steam.appjam.sopt.com.myapplication.Practice.View;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import steam.appjam.sopt.com.myapplication.afterFilter.ListItem;
import steam.appjam.sopt.com.myapplication.cardview.MyData;

/**
 * Created by kh on 2016. 9. 19..
 */
public interface MainView {
    void ChangeMainArea();
    Boolean LoginCheck();
    Location getLastKnownLocation();
    void showNullData();
    void setFirstDatas(ArrayList<MyData> myData);
    void setFilterDatas(List<ListItem> get_item);

}

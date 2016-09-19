package steam.appjam.sopt.com.myapplication.Practice.Presenter;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import steam.appjam.sopt.com.myapplication.Practice.Model.UserModel;
import steam.appjam.sopt.com.myapplication.Practice.View.MainView;
import steam.appjam.sopt.com.myapplication.afterFilter.ListItem;
import steam.appjam.sopt.com.myapplication.cardview.MyData;

/**
 * Created by kh on 2016. 9. 19..
 */
public class MainPresenterImpl implements MainPresenter, MonStudyPresenter {
    private MainView mainView;

    public MainPresenterImpl(MainView mainView) {
        this.mainView = mainView;
    }

    public MainView getMainView() {
        return mainView;
    }

    @Override
    public void getFirstData() {
        Log.i("MyTag", "2. 요청을 받은 프레젠터가 다시 모델에 데이터를 요청");
        UserModel model = new UserModel(this);
        model.getFirstDataFromServer();
    }

    @Override
    public void getFilterData(String si,String gu, String scate,String cap,String order) {
        Log.i("MyTag", "2. 요청을 받은 프레젠터가 다시 모델에 데이터를 요청");
        UserModel model = new UserModel(this);
        model.getFilterDataFromServer(si,gu,scate,cap,order);
    }

    @Override
    public void getFilterObjectFromModel(Object object) {
        Log.i("MyTag", "5. 모델에서 받은 데이터로 뷰를 갱신");

        List<ListItem> item = null;

        Gson gson = new Gson();
        String jsonString = gson.toJson(object);

        // JSONObject를 다룰 때 JSONException을 예외처리해주어야 합니다.
        try{
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray getFilterInfo = new JSONArray(jsonObject.getString("data"));

            Log.i("MyTag1", jsonObject.toString());
            JSONObject jObject;

            String Space_id;
            String Min_price;
            String Space_name;
            String Space_pic;
            String Space_openhour;
            String Space_closehour;
            String Review_rating_avg;
            String time;

            for(int i = 0; i<getFilterInfo.length();i++){
                jObject = getFilterInfo.getJSONObject(i);

                Space_id = jObject.getString("Space_id");
                Space_name = jObject.getString("Space_name");
                Min_price = jObject.getString("Min_price") +"원 ~ ";
                Review_rating_avg = jObject.getString("Review_rating_avg");
                Space_pic  =  jObject.getString("Space_pic") ;
                Space_openhour =  jObject.getString("Space_openhour") ;
                Space_closehour =  jObject.getString("Space_closehour") ;

                time = Space_openhour +":00~"+Space_closehour+":00";

                item.add(new ListItem(Space_id,Space_pic, Space_name, Min_price, time, Review_rating_avg));
            }

            if(getFilterInfo.length()==0)
                mainView.showNullData();
            else
                mainView.setFilterDatas(item);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.i("MyTag", e.getMessage());
        }


    }

    @Override
    public void getFirstObjectFromModel(Object object) {

        ArrayList<MyData> myDataset = null;

        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        // JSONObject를 다룰 때 JSONException을 예외처리해주어야 합니다.
        try{
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray getMainInfo = new JSONArray(jsonObject.getString("data"));
            JSONObject jObject;

            String sp_id;
            String name;
            String m_address;
            String location;
            String min_price;
            String rating;
            String image;

            for(int i = 0; i<getMainInfo.length();i++){
                jObject = getMainInfo.getJSONObject(i);

                sp_id = jObject.getString("Space_id");
                name = jObject.getString("Space_name");
                m_address = jObject.getString("Space_location_si");
                location  = jObject.getString("Space_location_si") +jObject.getString("Space_location_gu");
                min_price = jObject.getString("Min_price") +"원 ~ ";
                rating = jObject.getString("Review_rating_avg");
                image  =  jObject.getString("Space_pic") ;

                myDataset.add(new MyData(sp_id,name, m_address ,location, min_price, rating, image));

            }


            mainView.setFirstDatas(myDataset);

        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.i("MyTag", e.getMessage());
        }
    }

}

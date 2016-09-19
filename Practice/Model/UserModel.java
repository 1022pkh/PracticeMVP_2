package steam.appjam.sopt.com.myapplication.Practice.Model;

import android.util.Log;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import steam.appjam.sopt.com.myapplication.Practice.Presenter.MonStudyPresenter;
import steam.appjam.sopt.com.myapplication.afterFilter.type.FilterType;
import steam.appjam.sopt.com.myapplication.application.ApplicationController;
import steam.appjam.sopt.com.myapplication.network.NetworkService;

/**
 * Created by kh on 2016. 9. 19..
 */
public class UserModel {

    NetworkService networkService;
    MonStudyPresenter presenter;

    public UserModel(MonStudyPresenter presenter) {
        networkService = ApplicationController.getInstance().getNetworkService();
        this.presenter = presenter;
    }

    public void getFirstDataFromServer(){

        Log.i("MyTag", "3. 요청을 받은 모델이 서버에 데이터를 요청");
        Call<MainType> getDB = networkService.getMainData();

        getDB.enqueue(new Callback<MainType>() {
            @Override
            public void onResponse(Response<MainType> response, Retrofit retrofit) {

                if (response.isSuccess()) {

                    Log.i("MyTag", "4. 서버에서 받아온 데이터를 프레젠터에 전송");
                    presenter.getFirstObjectFromModel(response.body());

                } else {
                    Log.i("MyTag", "상태 코드 : " + response.code());
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "에러 내용 : " + t.getMessage());
            }
        });

    }
    public void getFilterDataFromServer(String si,String gu, String scate,String cap,String order) {

        Log.i("MyTag", "3. 요청을 받은 모델이 서버에 데이터를 요청");

        Call<FilterType> getFilter = networkService.getSearchRoom(si,gu,scate,cap,order);

        getFilter.enqueue(new Callback<FilterType>() {
            @Override
            public void onResponse(Response<FilterType> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    Log.i("MyTag", "4. 서버에서 받아온 데이터를 프레젠터에 전송");
                    presenter.getFilterObjectFromModel(response.body());

                } else {
                    Log.i("MyTag", "상태 코드 : " + response.code());
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "에러 내용 : " + t.getMessage());
            }
        });

    }

}
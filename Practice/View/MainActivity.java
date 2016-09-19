package steam.appjam.sopt.com.myapplication.Practice.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import steam.appjam.sopt.com.myapplication.ErrorController;
import steam.appjam.sopt.com.myapplication.LoginDialog;
import steam.appjam.sopt.com.myapplication.Practice.Model.MainType;
import steam.appjam.sopt.com.myapplication.Practice.Presenter.MainPresenter;
import steam.appjam.sopt.com.myapplication.Practice.Presenter.MainPresenterImpl;
import steam.appjam.sopt.com.myapplication.R;
import steam.appjam.sopt.com.myapplication.afterFilter.FilterAdapter;
import steam.appjam.sopt.com.myapplication.afterFilter.ListItem;
import steam.appjam.sopt.com.myapplication.application.ApplicationController;
import steam.appjam.sopt.com.myapplication.board.BoardView;
import steam.appjam.sopt.com.myapplication.cardview.MyAdapter;
import steam.appjam.sopt.com.myapplication.cardview.MyData;
import steam.appjam.sopt.com.myapplication.database.DbOpenHelper;
import steam.appjam.sopt.com.myapplication.dialog.CustomDialogKind;
import steam.appjam.sopt.com.myapplication.dialog.CustomDialogLocation;
import steam.appjam.sopt.com.myapplication.dialog.CustomDialogPeople;
import steam.appjam.sopt.com.myapplication.favorite.FavoriteSpace;
import steam.appjam.sopt.com.myapplication.gps.GpsInfo;
import steam.appjam.sopt.com.myapplication.login.view.LoginActivity;
import steam.appjam.sopt.com.myapplication.network.NetworkService;
import steam.appjam.sopt.com.myapplication.profile.view.ProfileActivity;
import steam.appjam.sopt.com.myapplication.sliding.SlidingDetail;


public class MainActivity extends AppCompatActivity
        implements MainView,OnClickListener,NavigationView.OnNavigationItemSelectedListener {


    private MainPresenter presenter;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<MyData> myDataset;

    private ListView listview;
    private LinearLayout locationBtn;
    private LinearLayout kindBtn;
    private LinearLayout peopleBtn;
    private Toolbar toolbar;
    private CheckBox scale;
    private CheckBox price;
    private CheckBox hour;

    private CircleImageView profile;
    private TextView userNameView;
    private CustomDialogLocation dialog_location;
    private CustomDialogKind dialog_kind;
    private CustomDialogPeople dialog_people;

    private String myLocation;
    LocationManager mLocationManager;
    private GpsInfo gps;


    private String chooseAddress = "null";
    private ArrayList<String> checkItem;
    private int checkPerson = 0;

    private int checkArray = 1;
    private Boolean fullHourChk = false;


    //Back 키 두번 클릭 여부 확인
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    private DbOpenHelper mDbOpenHelper;
    ApplicationController api;
    private LoginDialog dialog_login;
    private TextView kindText;

    private LinearLayout mypage;
    private LinearLayout bookmark;
    private LinearLayout board;
    private List<ListItem> item;
    private FilterAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * MainActivity -> MVP 패턴 적용
         */

        presenter = new MainPresenterImpl(this);


        if (Build.VERSION.SDK_INT >= 21) {   //상태바 색
            getWindow().setStatusBarColor(Color.parseColor("#F6D03F"));
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        TextView toolbar_title = ((TextView) findViewById(R.id.main_toolbar_title));
        toolbar_title.setTypeface(Typeface.createFromAsset(getAssets(),"Quicksand_Bold.otf"));
        toolbar_title.setText("MONSTUDY");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        mypage = (LinearLayout)findViewById(R.id.nav_mypage);
        bookmark = (LinearLayout)findViewById(R.id.nav_favorite);
        board = (LinearLayout)findViewById(R.id.nav_board);
        profile = (CircleImageView)findViewById(R.id.profile_image);
        userNameView = (TextView)findViewById(R.id.userName);
        locationBtn = (LinearLayout) findViewById(R.id.LocationArea);
        kindBtn = (LinearLayout) findViewById(R.id.KindArea);
        peopleBtn = (LinearLayout) findViewById(R.id.PeopleArea);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        listview = (ListView)findViewById(R.id.ListView1);
        scale = (CheckBox)findViewById(R.id.scaleChk);
        price = (CheckBox)findViewById(R.id.priceChk);
        hour = (CheckBox)findViewById(R.id.hourChk);


        mypage.setOnClickListener(this);
        bookmark.setOnClickListener(this);
        board.setOnClickListener(this);
        locationBtn.setOnClickListener(this);
        kindBtn.setOnClickListener(this);
        peopleBtn.setOnClickListener(this);


        // DB Create and Open
        mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();

        api = ApplicationController.getInstance();

        //로그인 중
        if(api.getCheck()){
            CircleImageView thumnail = (CircleImageView)findViewById(R.id.profile_image);
            thumnail.setVisibility(View.INVISIBLE);

            if(api.getLoginUser().img == 1)
                thumnail.setImageResource(R.drawable.ic_character_hobby_big);
            else  if(api.getLoginUser().img == 2){
                thumnail.setImageResource(R.drawable.ic_character_ready_big);
            }
            else{
                thumnail.setImageResource(R.drawable.ic_character_teach_big);
            }


            profile.setVisibility(View.VISIBLE);
//            profile.set(api.getLoginUser().img);
            userNameView.setText(api.getLoginUser().name);
        }


        NetworkService networkService = api.getNetworkService();


        Log.i("MyTag", "1. 프레젠터에 데이터 요청");
        presenter.getFirstData();



        Call<MainType> getDB = networkService.getMainData();

        getDB.enqueue(new Callback<MainType>() {
            @Override
            public void onResponse(Response<MainType> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(response.body());
                    // JSONObject를 다룰 때 JSONException을 예외처리해주어야 합니다.
                    try{
                        JSONObject jsonObject = new JSONObject(jsonString);
//                        jsonObject = jsonObject.getJSONObject("data");

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


                        mAdapter = new MyAdapter(myDataset);
                        mRecyclerView.setAdapter(mAdapter);


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("MyTag", e.getMessage());
                    }

                } else {
                    Log.i("MyTag", "상태 코드 : " + response.code());
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "에러 내용 : " + t.getMessage());
            }
        });


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0) {
                    // Recycle view scrolling up...

                } else if (dy > 0) {
                    // Recycle view scrolling down...

                    int scrollOffset = mRecyclerView.computeVerticalScrollOffset();
                    int scrollExtend = mRecyclerView.computeVerticalScrollExtent();
                    int scrollRange = mRecyclerView.computeVerticalScrollRange();

                    if (scrollOffset + scrollExtend == scrollRange || scrollOffset + scrollExtend - 1 == scrollRange) {

//                    if (lastVisibleItem >= totalItemCount - 10) {

//                        mAdapter.notifyDataSetChanged();

                    }
                }
            }
        });

    }

    @Override
    public void ChangeMainArea(){
        LinearLayout inflatedLayout = (LinearLayout)findViewById(R.id.mainArea);
        inflatedLayout.removeAllViews();

        LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflated_Layout.xml로 구성한 레이아웃을 inflatedLayout 영역으로 확장
        inflater.inflate(R.layout.activity_main_filter, inflatedLayout);

        item = new ArrayList<ListItem>();

        NetworkService networkService = api.getNetworkService();

        //@GET("/search/{si}/{gu}/{scate}/{cap}/{order}")
        String si,gu;

        //지역 선택되어 있는지
        if(chooseAddress == "null") {
            si = "*";
            gu = "*";
        }
        else{
            si = "서울시";
            gu = chooseAddress + "구";

        }

        //업종 선택되어 있는지
        String scate = "";

        if(checkItem != null){

            for (int i = 0; i<checkItem.size();i++) {

                if(checkItem.get(i).equals("all")){
                    scate = "*";
                    break;
                }
                else{
                    if(checkItem.get(i).equals("회의실"))
                        scate += "1" ;
                    else if(checkItem.get(i).equals("세미나실"))
                        scate += "2" ;
                    else if(checkItem.get(i).equals("다목적실"))
                        scate += "3" ;
                    else if(checkItem.get(i).equals("스터디"))
                        scate += "4" ;
                    else if(checkItem.get(i).equals("연습"))
                        scate += "5" ;
                    else if(checkItem.get(i).equals("카페"))
                        scate += "6" ;

                    if(i == checkItem.size()-1)
                        ;
                    else
                        scate += ",";
                }

            }

        }
        else
            scate = "*";

//        Log.i("Tag",scate);

        //인원 선택되어 있는지
        String cap;
        if(checkPerson == 0)
            cap = "*";
        else
            cap = String.valueOf(checkPerson);

        //검색조건
        String order = "1";
        if(checkArray == 1)
            order = "1";

        if(checkArray == 2)
            order = "2";

        if(checkArray == 1 && fullHourChk == true)
            order = "3";

        if(checkArray == 2 && fullHourChk == true)
            order = "4";



        Log.i("MyTag", "1. 프레젠터에 데이터 요청");
        presenter.getFilterData(si,gu,scate,cap,order);


        String image = "http://52.78.8.18:5000/image/6.png";
        item.add(new ListItem("1",image, "힐스터디", "1300원~", "09:00~20:00", "4.4"));
        item.add(new ListItem("2",image,"쉐어원 라운지","1000원~","09:00~20:00","2.3"));
        item.add(new ListItem("3",image,"히어로스터디","500원~","09:00~20:00","3.3"));

        if(checkArray == 1)
            scale.setChecked(true);
        else
            price.setChecked(true);

        if(fullHourChk)
            hour.setChecked(true);
        else
            hour.setChecked(false);


        scale.setOnClickListener(this);
        price.setOnClickListener(this);
        hour.setOnClickListener(this);

    }

    private OnClickListener SearchCurrent = new OnClickListener() {
        public void onClick(View v) {

            gps = new GpsInfo(MainActivity.this);
            String myPosition = null;

            // GPS 사용유무 가져오기
            if (gps.isGetLocation()) {
                Location myLocation = getLastKnownLocation();

                if(myLocation == null)
                    return ;
                double currentLatitude = myLocation.getLatitude();
                double currentLongitude = myLocation.getLongitude();


                Geocoder gc = new Geocoder(getApplicationContext(), Locale.KOREAN);

                try{
                    List<Address> addresses = gc.getFromLocation(currentLatitude,currentLongitude,1);
                    StringBuilder sb = new StringBuilder();

                    if(addresses.size() > 0){
                        Address address = addresses.get(0);

                        String addressTemp = address.getAddressLine(0);

                        String[] myLocationAddress = addressTemp.split(" ");
                        //현재 도로명주소로 주고 있음...
                        myPosition = myLocationAddress[1]+" "+myLocationAddress[2]+" "+myLocationAddress[3];
                        locationBtn.removeAllViews();

                        LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        // Inflated_Layout.xml로 구성한 레이아웃을 inflatedLayout 영역으로 확장
                        inflater.inflate(R.layout.main_search_location, locationBtn);


                        Toast.makeText(getApplicationContext(),"현재 위치 : "+myPosition,Toast.LENGTH_SHORT).show();

                        dialog_location.dismiss();
                        ChangeMainArea();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"GPS 신호가 약합니다. 직접 선택해주세요! ",Toast.LENGTH_SHORT).show();

                }

            } else {
                // GPS 를 사용할수 없으므로
                gps.showSettingsAlert();
            }

            myLocation = myPosition;


        }


    };

    private OnClickListener getLocationEvent = new OnClickListener() {
        public void onClick(View v) {
            chooseAddress = dialog_location.giveAddress();

            if(chooseAddress == "null") {
                Toast.makeText(getApplicationContext(),"지역을 선택해주세요.",Toast.LENGTH_SHORT).show();
            }
            else {

                locationBtn.removeAllViews();

                locationBtn.setGravity(Gravity.CENTER);
                LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // Inflated_Layout.xml로 구성한 레이아웃을 inflatedLayout 영역으로 확장
                inflater.inflate(R.layout.main_search_location, locationBtn);

                TextView locationText = (TextView) findViewById(R.id.ChooseLocation);
                String address = "서울시\n" + chooseAddress;

                locationText.setText(address);

                dialog_location.dismiss();
                ChangeMainArea();

            }
        }

    };

    private OnClickListener getKindEvent = new OnClickListener() {
        public void onClick(View v) {

            checkItem = dialog_kind.CheckKind();

            if(checkItem != null){
                kindBtn.removeAllViews();


                kindBtn.setGravity(Gravity.CENTER);
                LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // Inflated_Layout.xml로 구성한 레이아웃을 inflatedLayout 영역으로 확장
                inflater.inflate(R.layout.main_search_kind, kindBtn);

                kindText = (TextView)findViewById(R.id.ChooseKind);

                if(checkItem.size() > 3) {
                    Toast.makeText(getApplicationContext(), "최대 3개의 업종을 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    return ;
                }

                String temp = "";

                for (int i = 0; i<checkItem.size();i++) {
                    temp += checkItem.get(i) ;
                    if(i != checkItem.size()-1)
                    {
                        temp +="\n";
                    }
                }

                if(temp.equals("all"))
                    temp = "전체";

                if(temp == "" || temp == null)
                    return ;
                else
                    kindText.setText(temp);

                dialog_kind.dismiss();
                ChangeMainArea();

            }
            else{
                Toast.makeText(getApplicationContext(), "1개 이상의 업종을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }

        }

    };

    private OnClickListener getPeopleEvent = new OnClickListener() {
        public void onClick(View v) {

            checkPerson = dialog_people.getPerson();
            if(checkPerson == 0 ){
                Toast.makeText(getApplicationContext(),"최소 1명입니다."+checkPerson,Toast.LENGTH_SHORT).show();
                return ;
            }
            else{
                peopleBtn.removeAllViews();

                peopleBtn.setGravity(Gravity.CENTER);
                LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // Inflated_Layout.xml로 구성한 레이아웃을 inflatedLayout 영역으로 확장
                inflater.inflate(R.layout.main_search_people, peopleBtn);

                TextView peopleText = (TextView)findViewById(R.id.ChoosePeople);

                peopleText.setText(String.valueOf(checkPerson)+"명");

                dialog_people.dismiss();
                ChangeMainArea();
            }


        }

    };

    private OnClickListener loginEvent = new OnClickListener() {
        public void onClick(View v) {
            dialog_login.dismiss();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

    };

    private OnClickListener loginCancelEvent = new OnClickListener() {
        public void onClick(View v) {
            dialog_login.dismiss();
        }

    };

    @Override
    public Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void showNullData() {
        Toast.makeText(getApplicationContext(),"죄송합니다. 검색결과가 없습니다.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setFirstDatas(ArrayList<MyData> myData) {
        mAdapter = new MyAdapter(myData);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setFilterDatas(final List<ListItem> get_item) {

        adapter = new FilterAdapter(getApplicationContext(),get_item);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext() , SlidingDetail.class);
                intent.putExtra("space_id",String.valueOf(get_item.get(position).getId()));

                startActivity(intent);

            }
        });

        listview.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        long tempTime        = System.currentTimeMillis();
        long intervalTime    = tempTime - backPressedTime;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            /**
             * Back키 두번 연속 클릭 시 앱 종료
             */

            if ( 0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime ) {
                super.onBackPressed();
            }
            else {
                backPressedTime = tempTime;
                Toast.makeText(getApplicationContext(),"뒤로 가기 키을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void networkError() {
        ErrorController errorController = new ErrorController(this.getApplicationContext());
        errorController.notifyNetworkError();
    }

    @Override
    public Boolean LoginCheck(){
        ApplicationController api = new ApplicationController();
        if(!api.getCheck()) {
            WindowManager.LayoutParams loginParams;
            dialog_login = new LoginDialog(MainActivity.this, loginEvent,loginCancelEvent);

            loginParams = dialog_login.getWindow().getAttributes();

            // Dialog 사이즈 조절 하기
            loginParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            loginParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog_login.getWindow().setAttributes(loginParams);

            dialog_login.show();

            return false;
        }
        else
            return true;
    }

    @Override
    public void onClick(View v) {

        Intent intent;
        WindowManager.LayoutParams params;

        switch (v.getId()){
            case R.id.nav_mypage:
                if(LoginCheck()) {
                    intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.nav_favorite:
                if(LoginCheck()) {
                    intent = new Intent(getApplicationContext(), FavoriteSpace.class);
                    startActivity(intent);
                }
                break;

            case R.id.nav_board:
                if(LoginCheck()) {
                    intent = new Intent(getApplicationContext(), BoardView.class);
                    startActivity(intent);
                }
                break;

            case R.id.LocationArea:

                dialog_location = new CustomDialogLocation(MainActivity.this, SearchCurrent,getLocationEvent);

                params = dialog_location.getWindow().getAttributes();

                // Dialog 사이즈 조절 하기
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog_location.getWindow().setAttributes(params);

                dialog_location.show();

                break;

            case R.id.KindArea:
                dialog_kind = new CustomDialogKind(MainActivity.this,checkItem ,getKindEvent);

                params = dialog_kind.getWindow().getAttributes();

                // Dialog 사이즈 조절 하기
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog_kind.getWindow().setAttributes(params);
                dialog_kind.show();
                break;

            case R.id.PeopleArea:
                dialog_people = new CustomDialogPeople(MainActivity.this, checkPerson ,getPeopleEvent);

                params = dialog_people.getWindow().getAttributes();

                // Dialog 사이즈 조절 하기
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog_people.getWindow().setAttributes(params);
                dialog_people.show();

                break;


            case R.id.scaleChk:
                if(scale.isChecked()){
                    checkArray = 1;
                    price.setChecked(false);
                    ChangeMainArea();
                }
                else{
                    checkArray = 2;
                    price.setChecked(true);
                    ChangeMainArea();
                }
                break;

            case R.id.priceChk:
                if(price.isChecked()){
                    checkArray = 2;
                    scale.setChecked(false);
                    ChangeMainArea();
                }
                else{
                    checkArray = 1;
                    scale.setChecked(true);
                    ChangeMainArea();
                }
                break;

            case R.id.hourChk:
                if(hour.isChecked()){
                    fullHourChk = true;
                    ChangeMainArea();
                }
                else{
                    fullHourChk = false;
                    ChangeMainArea();
                }
                break;

        }

    }
}
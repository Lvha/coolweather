package com.example.lvhao.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lvhao.coolweather.db.City;
import com.example.lvhao.coolweather.db.County;
import com.example.lvhao.coolweather.db.Province;
import com.example.lvhao.coolweather.util.HttpUtil;
import com.example.lvhao.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     *市 列表
     */
    private List<City> cityList;


    /**
     *县列表
     */
    private List<County> countyList;

    /**
     *选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area,container,false);
        //获取id
        titleText = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        //适配器将数据传给ListView
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //如果当前点击的省，就将当前省的赋值，并且调下一个界面（市）
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    queryCities();



                } else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounties();


                } else if (currentLevel == LEVEL_COUNTY){
                    String weaterId = countyList.get(i).getCountyName();
                    //利用insranceof判读对象是否属于这个实例
                    if (getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weaterId);
                        startActivity(intent);
                        getActivity().finish();

                    } else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity)getActivity();
                        //关闭侧滑菜单
                        activity.drawerLayout.closeDrawers();
                        //显示下拉刷新进度条
                        activity.swipeRefresh.setRefreshing(true);
                        //从新获取天气信息
                        activity.requestWeather(weaterId);
                    }


                }


            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果是县
                if (currentLevel == LEVEL_COUNTY){
                    //返回到市
                    queryCities();


                    //如果是县
                } else if (currentLevel == LEVEL_CITY){
                    //返回到省
                    queryProvinces();
                }
            }
        });
        queryProvinces();



    }

    /**
     *查询省，先从数据库里查询，如果没有就到服务器上查询
     */
    private void queryProvinces(){
        titleText.setText("中国");
        //返回标签不可见，但是占布局的地方
        backButton.setVisibility(View.GONE);
        //通过findAll（）方法查询指定的表
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province :provinceList){
                dataList.add(province.getProvinceName());
            }
            //数据已经更改过，通知一下
            adapter.notifyDataSetChanged();
            //设置列表初始从那边开始
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");



        }


    }

    /**
     * 省
     */
    private void queryCities(){


        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");




        }



    }
    /**
     * 市
     */
    private void queryCounties(){

        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);

        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);

        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;

        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address,"county");


        }
    }

    /**
     * 根据传入的地址和类型，在服务器上查询数据
     */
    private void queryFromServer(String address,final String type){

        //显示进度对话框
        showProgressDialog();

        HttpUtil.sendOkHttpRequest(address, new Callback() {

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            String responseText = response .body().string();
            boolean result = false;
            if ("province".equals(type)){
                result = Utility.handleprovinceResponse(responseText);
            } else if ("city".equals(type)){
                result = Utility.handleCityResponse(responseText,selectedProvince.getId());
            }else if ("county".equals(type)){
                result = Utility.handleCountyResponse(responseText,selectedCity.getId());
            }





            if (result){

                //对UI操作只能在主线程上进行， runonUiThread就是回到主线程上
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭进度对话框
                        closeProgressDialog();



                        if ("province".equals(type)){
                            queryProvinces();

                        } else if ("city".equals(type)){
                            queryCities();

                        }else if ("county".equals(type)){
                            queryCounties();

                        }

                    }
                });
            }

        }


            @Override
            public void onFailure(Call call, IOException e) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //关闭进度对话框
                        closeProgressDialog();


                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();

                    }
                });

            }


        });




    }

    private void showProgressDialog(){

        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载…………");
            //设置取消在触摸外部
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();



    }

    private void closeProgressDialog(){
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }

    }

}

/*




 */
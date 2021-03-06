package com.example.myapplication.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.Adapter.DoctorsAdapter;
import com.example.myapplication.Bean.DoctorData;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Constant;
import com.example.myapplication.Utils.NetRequestService;
import com.example.myapplication.View.BottomLayout;
import com.example.myapplication.View.TitleLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TeleReservationActivity extends AppCompatActivity {
    private TitleLayout titleLayout;
    private List<DoctorData> list=new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private BottomLayout bottomLayout;
    private TextView textView;
    private int diseaseId;
    private int mYear;
    private int mMonth;
    private int mDay;
    public String[] text = {
            "\n姓名：李凯\n中华医学会骨科分会骨病组委员\n擅长针灸推拿和小针刀治疗\n科室：骨科\n职务-职称：副主任医师\n坐诊时间：周一至周五、周日",
            "\n姓名：季霞\n协和不孕不育研究所首席专家\n中华医学会生殖医学分会委员\n科室：不孕不育中心\n职务-职称：主任医师\n坐诊时间：周一至周六",
            "\n姓名：叶天琼\n重庆市不孕不育临床中心主任 \n妇女健康知识巡回宣讲团讲师\n科室：不孕不育中心\n职务-职称：副主任医师\n坐诊时间：周一至周六",
            "\n姓名：徐小蓉\n中华医学会妇产科分会委员\n抗癌协会妇科肿瘤专委会全国委员\n科室：妇产科\n职务-职称：主任医师 教授 \n坐诊时间：周二至周六",
            "\n姓名：赵本书\n妇女病康复专业委员会不孕症学组委员\n多次参加全国妇产科专业学术交流\n科室：不孕不育中心\n职务-职称：主治医师\n坐诊时间：全天坐诊" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tele_reservation);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        titleLayout = findViewById(R.id.title);
        titleLayout.setTitle("远程医疗");
        titleLayout.setNextGone();
        titleLayout.setOnBackClickListener(new TitleLayout.OnBackClickListener() {
            @Override
            public void onMenuBackClick() {
                finish();
            }
        });
        textView=findViewById(R.id.time_text);
        Intent intent=getIntent();
        diseaseId=intent.getIntExtra("diseaseId",0);
        linearLayout=findViewById(R.id.time);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                mYear=calendar.get(Calendar.YEAR);
                mMonth=calendar.get(Calendar.MONTH);
                mDay=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog=new DatePickerDialog(TeleReservationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear=year;
                        mMonth=month;
                        mDay=dayOfMonth;
                        textView.setText(String.valueOf(mYear)+"-"+String.valueOf(mMonth+1)+"-"+String.valueOf(mDay));
                        GetDocList(diseaseId,textView.getText().toString());
                    }
                },mYear,mMonth,mDay);
                dialog.show();
            }
        });
        bottomLayout=findViewById(R.id.bottom_bar);
        bottomLayout.setMode(2);

        //init();
        recyclerView=findViewById(R.id.recycler_view);


    }

    public void GetDocList(int diseaseId,String time) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.GETDOCTOR_URL)
                .build();
        NetRequestService netRequestService = retrofit.create(NetRequestService.class);
        Call<ResponseBody> call=netRequestService.getDocList(LoginActivity.sp.getString("token", ""),diseaseId,time,1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
               if (response.isSuccessful()){
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           try{
                               list.clear();
                               String body=response.body().string();
                               Log.d("dsadsafadsad", "run:success ");
                               JSONObject jsonObject=new JSONObject(body);
                               JSONArray array=jsonObject.getJSONArray("data");
                               for (int i=0;i<array.length();i++){
                                   JSONObject object=array.getJSONObject(i);
                                   int docId=object.getInt("docId");
                                   String name =object.getString("name");
                                   String title=object.getString("title");
                                   String attending=object.getString("attending");
                                   String introduction=object.getString("introduction");
                                   String language=object.getString("language");
                                   String text="\n姓名："+name+"\n"+
                                           "职务-职称："+title+"\n"+
                                           "擅长："+attending+"\n"+
                                           "简介："+introduction+"\n"+
                                           "语言："+language;
                                   Log.d("dsadsafadsad", "run: "+text);
                                   DoctorData doctorData=new DoctorData(docId,ContextCompat.
                                           getDrawable(TeleReservationActivity.this,R.drawable.doc11),text);
                                   list.add(doctorData);
                               }
                               LinearLayoutManager linearLayoutManager=new LinearLayoutManager(TeleReservationActivity.this);
                               DoctorsAdapter doctorsAdapter=new DoctorsAdapter(list,1);
                               recyclerView.setLayoutManager(linearLayoutManager);
                               recyclerView.setAdapter(doctorsAdapter);

                           }catch (Exception e){
                               e.printStackTrace();
                           }
                       }
                   });
               }else{

               }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

//    private void init(){
//        DoctorData doctorData1=new DoctorData(ContextCompat.getDrawable(this,R.drawable.doc11),text[0]);
//        DoctorData doctorData2=new DoctorData(ContextCompat.getDrawable(this,R.drawable.doc22),text[1]);
//        DoctorData doctorData3=new DoctorData(ContextCompat.getDrawable(this,R.drawable.doc33),text[2]);
//        DoctorData doctorData4=new DoctorData(ContextCompat.getDrawable(this,R.drawable.doc44),text[3]);
//        DoctorData doctorData5=new DoctorData(ContextCompat.getDrawable(this,R.drawable.doc55),text[4]);
//        list.add(doctorData1);
//        list.add(doctorData2);
//        list.add(doctorData3);
//        list.add(doctorData4);
//        list.add(doctorData5);
//    }

}

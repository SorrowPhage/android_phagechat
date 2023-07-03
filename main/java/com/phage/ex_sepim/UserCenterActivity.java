package com.phage.ex_sepim;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.phage.ex_sepim.entity.Result;
import com.phage.ex_sepim.entity.User;
import com.phage.ex_sepim.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserCenterActivity extends AppCompatActivity {

    private ImageView edit_close;
    private static final int EDIT_CODE = 1;

    private MHandler mHandler;

    private EditText edit_username, edit_des;

    private RadioGroup edit_group;

    private Button edit_edit,edit_gb1,edit_gb2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        mHandler = new MHandler();

        edit_username = findViewById(R.id.edit_username);
        edit_des = findViewById(R.id.edit_des);
        edit_edit = findViewById(R.id.edit_edit);

        edit_close = findViewById(R.id.edit_close);
        edit_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();

//        通过id获取信息
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl url = HttpUrl.parse(HttpUtil.PHAGE_SERVER_IP + "/user/get").newBuilder().addQueryParameter("id",extras.getString("id")).build();

        Request request = new Request.Builder().url(url).build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("=========");
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                Message msg = new Message();
                msg.what = EDIT_CODE;
                msg.obj = res;
                mHandler.sendMessage(msg);
            }
        });

        edit_group = findViewById(R.id.edit_group);
        edit_gb1 = findViewById(R.id.edit_gb1);
        edit_gb2 = findViewById(R.id.edit_gb2);
        edit_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edit_username.getText().toString();
                String des = edit_des.getText().toString();
                int sex;
//                获取性别
                RadioButton radioButton = (RadioButton) edit_group.getChildAt(0);
                if (radioButton.isChecked()) {
                    sex = 0;
                } else {
                    sex = 1;
                }
//                上传新的信息

                OkHttpClient okHttpClient = new OkHttpClient();

                HttpUrl url = HttpUrl.parse(HttpUtil.PHAGE_SERVER_IP + "/user/update").newBuilder().addQueryParameter("id", extras.getString("id")).addQueryParameter("username", username).addQueryParameter("des", des).addQueryParameter("sex", String.valueOf(sex)).build();

                Request request = new Request.Builder().url(url).build();

                Call call = okHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        System.out.println("=========");
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Toast.makeText(UserCenterActivity.this, "修改成功", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case EDIT_CODE:
                    if (msg.obj != null) {
                        String logResult = (String) msg.obj;
                        Result result = JSONObject.parseObject(logResult, Result.class);
                        if (200 == result.getCode()) {
                            User user = JSONObject.parseObject(String.valueOf(result.getData()), User.class);
                            System.out.println(user);
                            edit_username.setText(user.getUsername());
                            edit_des.setText(user.getDes());
                            if (user.getSex() == 0) {
                                RadioButton childAt = (RadioButton) edit_group.getChildAt(0);
                                childAt.setChecked(true);
                            } else {
                                RadioButton childAt = (RadioButton) edit_group.getChildAt(1);
                                childAt.setChecked(true);
                            }
                        }
                        break;
                    }
            }
        }
    }
}
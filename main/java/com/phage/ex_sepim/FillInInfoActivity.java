package com.phage.ex_sepim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
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

public class FillInInfoActivity extends AppCompatActivity {

    private EditText username, password;

    private Button button;

    private Bundle bundle;

    private final int FILL_IN_CODE = 1;

    private MHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in_info);

        mHandler = new MHandler();

        username = findViewById(R.id.fill_username);
        password = findViewById(R.id.fill_password);
        button = findViewById(R.id.fill_btn);
        bundle = getIntent().getExtras();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = username.getText().toString();
                String s1 = password.getText().toString();
                String s2 = bundle.getString("phone");
                OkHttpClient okHttpClient = new OkHttpClient();
                HttpUrl url = HttpUrl.parse(HttpUtil.PHAGE_SERVER_IP + "/user/add").newBuilder().addQueryParameter("username", s).addQueryParameter("phone", s2).addQueryParameter("password", s1).build();

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
                        msg.what = FILL_IN_CODE;
                        msg.obj = res;
                        mHandler.sendMessage(msg);
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
                case FILL_IN_CODE:
                    if (msg.obj != null) {
                        System.out.println("==========VerifyCode");
                        String logResult = (String) msg.obj;
                        Result result = JSONObject.parseObject(logResult, Result.class);
                        System.out.println(result);
                        if (200 == result.getCode()) {
                            User user = JSONObject.toJavaObject((JSON) result.getData(), User.class);
                            Intent intent = new Intent(FillInInfoActivity.this,ResultActivity.class);
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("id", String.valueOf(user.getId()));
                            intent.putExtras(bundle1);
                            startActivity(intent);
                        }
                        break;
                    }
            }
        }
    }
}
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

import com.alibaba.fastjson.JSONObject;
import com.phage.ex_sepim.entity.Result;
import com.phage.ex_sepim.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText phone;

    private Button send;

    private final int REGISTER_CODE = 1;

    private MHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phone = findViewById(R.id.edit_register);

        mHandler = new MHandler();

        send = findViewById(R.id.btn_register);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //得到输入框的手机号并发送给客户端，客户端返回一段验证码进行验证，若验证通过则输入用户名和密码，注册成功
                String s = phone.getText().toString();
//                Toast.makeText(RegisterActivity.this, "" + s, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(RegisterActivity.this, VerifyCodeActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("phone", s);
//                intent.putExtras(bundle);


                OkHttpClient okHttpClient = new OkHttpClient();
                HttpUrl url = HttpUrl.parse(HttpUtil.PHAGE_SERVER_IP + "/user/register/sendcode").newBuilder().addQueryParameter("phone", s).build();

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
                        msg.what = REGISTER_CODE;
                        msg.obj = res;
                        mHandler.sendMessage(msg);
                    }
                });



//                startActivity(intent);
            }
        });
    }


    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case REGISTER_CODE:
                    if (msg.obj != null) {
                        System.out.println("==========Register");
                        String logResult = (String) msg.obj;
                        Result result = JSONObject.parseObject(logResult, Result.class);
                        System.out.println(result);
                        if (200 == result.getCode()) {
                            Intent intent = new Intent(RegisterActivity.this,VerifyCodeActivity.class);
                            Bundle bundle = new Bundle();
                            phone = findViewById(R.id.edit_register);
                            bundle.putString("phone", phone.getText().toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        break;
                    }
            }
        }
    }
}
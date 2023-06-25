package com.phage.ex_sepim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class VerifyCodeActivity extends AppCompatActivity {
    private TextView verifyText;

    private EditText verify;

    private Button verifyBtn;

    private Bundle extras;

    private final int VERIFY_CODE_CODE = 1;

    private MHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        mHandler = new MHandler();

        verifyText = findViewById(R.id.tv_verify);
        verify = findViewById(R.id.edit_verify);
        verifyBtn = findViewById(R.id.btn_verify);

        extras = getIntent().getExtras();

        verifyText.setText("请输入" + extras.getString("phone") + "手机号得到的验证码");

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = verify.getText().toString();
//                验证成功
//                Intent intent = new Intent(VerifyCodeActivity.this, FillInInfoActivity.class);
//                intent.putExtras(extras);
//                startActivity(intent);

                OkHttpClient okHttpClient = new OkHttpClient();
                HttpUrl url = HttpUrl.parse(HttpUtil.PHAGE_SERVER_IP + "/user/register/verifycode").newBuilder().addQueryParameter("vcode", s).addQueryParameter("phone",extras.getString("phone")).build();

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
                        msg.what = VERIFY_CODE_CODE;
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
                case VERIFY_CODE_CODE:
                    if (msg.obj != null) {
                        System.out.println("==========VerifyCode");
                        String logResult = (String) msg.obj;
                        Result result = JSONObject.parseObject(logResult, Result.class);
                        System.out.println(result);
                        if (200 == result.getCode()) {
                            Intent intent = new Intent(VerifyCodeActivity.this,FillInInfoActivity.class);
                            extras = getIntent().getExtras();
                            intent.putExtras(extras);
                            startActivity(intent);
                        }
                        break;
                    }
            }
        }
    }
}
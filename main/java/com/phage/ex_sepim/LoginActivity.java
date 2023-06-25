package com.phage.ex_sepim;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.phage.ex_sepim.entity.Result;
import com.phage.ex_sepim.entity.User;
import com.phage.ex_sepim.utils.EditTextUtils;
import com.phage.ex_sepim.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText et_qqnum, et_qqpwd;
    private ImageView iv_login, et_delete_num, et_delete_pwd, et_pwd_see;
    private TextView tv_forgetpwd, tv_register;
    private String qq_numtext, qq_pwdtext;
    private boolean pwdCanSee;

    private MHandler mHandler;

    private static final int LOGIN_MSG_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mHandler = new MHandler();

        findId();
        //QQ账号输入状态监听
        et_qqnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                qq_pwdtext = et_qqpwd.getText().toString().trim();
                qq_numtext = et_qqnum.getText().toString().trim();
                if (!TextUtils.isEmpty(qq_numtext) && !TextUtils.isEmpty(qq_pwdtext)) {
                    //如果账号和密码都不为空，打开图片响应事件，并且更换图片
                    iv_login.setEnabled(true);
                    iv_login.setImageResource(R.mipmap.arrow_right_circle_blue);
                } else {
                    iv_login.setEnabled(false);
                    iv_login.setImageResource(R.mipmap.arrow_right_circle);
                }
            }
        });

        //QQ密码输入状态监听
        et_qqpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                qq_pwdtext = et_qqpwd.getText().toString().trim();
                qq_numtext = et_qqnum.getText().toString().trim();
                if (!TextUtils.isEmpty(qq_numtext) && !TextUtils.isEmpty(qq_pwdtext)) {
                    //如果账号和密码都不为空，打开图片响应事件，并且更换图片
                    iv_login.setEnabled(true);
                    iv_login.setImageResource(R.mipmap.arrow_right_circle_blue);
                } else {
                    iv_login.setEnabled(false);
                    iv_login.setImageResource(R.mipmap.arrow_right_circle);
                }
            }
        });
        //QQ密码输入焦点监听
        et_qqpwd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                et_pwd_see.setVisibility(View.VISIBLE);
            } else {
                et_pwd_see.setVisibility(View.INVISIBLE);
            }
        });

        //密码可见小图标
        pwdCanSee = false;//true密码可见，false密码不可见
        et_pwd_see.setOnClickListener(v -> {

            if (pwdCanSee) {
                //设置不可见
                et_qqpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_pwd_see.setImageResource(R.mipmap.nosee);
                et_qqpwd.setSelection(et_qqpwd.getText().length());
                pwdCanSee = false;
            } else {
                //设置可见
                et_qqpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_pwd_see.setImageResource(R.mipmap.see);
                et_qqpwd.setSelection(et_qqpwd.getText().length());
                pwdCanSee = true;
            }
        });

        //删除小图标
        EditTextUtils.clearButtonListener(et_qqnum, et_delete_num);
        EditTextUtils.clearButtonListener(et_qqpwd, et_delete_pwd);

        //登录
        //iv_login.setClickable(true);
        //setOnClickListener方法会默认把控件的setClickable设置为true。
        //设置login图片无事件响应
        iv_login.setEnabled(false);
        iv_login.setOnClickListener(v -> {
            qq_numtext = et_qqnum.getText().toString().trim();
            qq_pwdtext = et_qqpwd.getText().toString().trim();
//            Toast.makeText(this, "登录成功!账号:" + qq_numtext + ",密码:" + qq_pwdtext, Toast.LENGTH_SHORT).show();


            OkHttpClient okHttpClient = new OkHttpClient();


            HttpUrl url = HttpUrl.parse(HttpUtil.PHAGE_SERVER_IP + "/login").newBuilder().addQueryParameter("id", qq_numtext).addQueryParameter("password", qq_pwdtext).build();

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
                    msg.what = LOGIN_MSG_CODE;
                    msg.obj = res;
                    mHandler.sendMessage(msg);
                }
            });


//            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);

        });

        //忘记密码
        tv_forgetpwd.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("SmallQQ.error");
            startActivity(intent);
        });
        //用户注册
        tv_register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void findId() {
        et_qqnum = findViewById(R.id.qq_num);
        et_qqpwd = findViewById(R.id.qq_pwd);
        iv_login = findViewById(R.id.qq_login);
        tv_forgetpwd = findViewById(R.id.qq_forgetpwd);
        tv_register = findViewById(R.id.qq_register);
        et_delete_num = findViewById(R.id.iv_et_num_delete);
        et_delete_pwd = findViewById(R.id.iv_et_pwd_delete);
        et_pwd_see = findViewById(R.id.iv_et_pwd_see);
    }

    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case LOGIN_MSG_CODE:
                    if (msg.obj != null) {
                        String logResult = (String) msg.obj;
                        Result result = JSONObject.parseObject(logResult, Result.class);
                        System.out.println(result);
                        if (200 == result.getCode()) {
                            User user = JSONObject.toJavaObject((JSON) result.getData(), User.class);
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);

                            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                            editor.putString("id", user.getId().toString());
                            editor.putString("password", user.getPassword());
                            editor.putString("avatar", user.getAvatar());
                            editor.putString("phone", user.getPhone());
                            editor.putString("username", user.getUsername());
                            editor.apply();

//                            SQLiteOpenHelper helper = new DbContect(LoginActivity.this);
//                            SQLiteDatabase writableDatabase = helper.getWritableDatabase();
//                            ContentValues cv = new ContentValues();
//                            cv.put("id", user.getId());
//                            cv.put("username", user.getUsername());
//                            cv.put("password", user.getPassword());
//                            cv.put("avatar", user.getAvatar());
//                            cv.put("phone", user.getPhone());
//                            writableDatabase.insert("user_tb", null, cv);
//                            writableDatabase.close();

                            startActivity(intent);
                        }
                        break;
                    }
            }
        }
    }
}
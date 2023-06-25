package com.phage.ex_sepim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class NavigationActicity extends AppCompatActivity {
    private ImageView close,user_avatar;

    private TextView user_username;

    private RelativeLayout layout;

    private String id;

    private String password;
    private String avatar;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_acticity);
        close = findViewById(R.id.user_close);
        close.setOnClickListener(view -> finish());

        //        先判断是否登录，
        SharedPreferences sharedPreferences = getSharedPreferences("data", 0);
        id= sharedPreferences.getString("id","");
        password = sharedPreferences.getString("password", "");
        avatar = sharedPreferences.getString("avatar", "");
        username = sharedPreferences.getString("username", "");
        if (id.equals("") && password.equals("")) {
//            未登录，跳转到登录界面
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        user_avatar = findViewById(R.id.user_avatar);
        Glide.with(this).load(avatar).into(user_avatar);

        user_username = findViewById(R.id.user_username);
        user_username.setText(username);


        View view = findViewById(R.id.navigation);
        view.setOnTouchListener(new View.OnTouchListener() {
            private float mCurPosY;
            private float mCurPosX;
            private float mPosY;
            private float mPosX;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mCurPosX - mPosX > 0 && (Math.abs(mCurPosX - mPosX) > 150)) {
                            finish();

                        } else if (mCurPosX - mPosX < 0 && (Math.abs(mCurPosX - mPosX) > 150)) {
                            Log.e("TAG", "wang向左");
                        }
                        break;
                }
                return false;
            }
        });

        layout = findViewById(R.id.navi_exit);
        layout.setOnClickListener(view1 -> {
            Intent intent = new Intent(NavigationActicity.this, LoginActivity.class);
//            删除登录数据
            startActivity(intent);
        });
    }
}
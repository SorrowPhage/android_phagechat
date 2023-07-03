package com.phage.ex_sepim;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;

    private String id;

    private String password;
    private String avatar;
    private String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);



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

//        Intent intent1 = new Intent(this, NotificationService.class);
//        startService(intent1); //启动监听消息推送的服务


        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();


        Bundle bundle2 = new Bundle();
        bundle2.putString("id", id);
        bundle2.putString("username", username);
        bundle2.putString("avatar", avatar);
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle2);
        transaction.add(R.id.id_content, messageFragment);
        transaction.commit();
        initView();

    }

    private void initView() {
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);




        Drawable drawable = getResources().getDrawable(R.mipmap.icon_message);
        drawable.setBounds(0, 0
                , 100, 100);
        rb1.setCompoundDrawables(null,drawable,null,null);

        Drawable drawable2 = getResources().getDrawable(R.mipmap.icon_contacts);
        drawable2.setBounds(0, 0
                , 100, 100);
        rb2.setCompoundDrawables(null,drawable2,null,null);

        Drawable drawable3 = getResources().getDrawable(R.mipmap.icon_trends);
        drawable3.setBounds(0, 0
                , 100, 100);
        rb3.setCompoundDrawables(null,drawable3,null,null);

        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        rb3.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("username", username);
        bundle.putString("avatar", avatar);
        switch (view.getId()) {
            case R.id.rb1:
                MessageFragment messageFragment = new MessageFragment();
                messageFragment.setArguments(bundle);
                transaction.replace(R.id.id_content, messageFragment);
                break;
            case R.id.rb2:
                ContactsFragment contactsFragment = new ContactsFragment();
                contactsFragment.setArguments(bundle);
                transaction.replace(R.id.id_content, contactsFragment);
                break;
            case R.id.rb3:
                TrendsFragment trendsFragment = new TrendsFragment();
                trendsFragment.setArguments(bundle);
                transaction.replace(R.id.id_content, trendsFragment);
                break;
        }
        transaction.commit();
    }
}
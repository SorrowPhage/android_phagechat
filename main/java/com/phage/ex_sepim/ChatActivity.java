package com.phage.ex_sepim;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.phage.ex_sepim.entity.Result;
import com.phage.ex_sepim.entity.User;
import com.phage.ex_sepim.utils.ChatMessageAdapter;
import com.phage.ex_sepim.utils.DbContect;
import com.phage.ex_sepim.utils.HttpUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private TextView usernameView;

    private ImageView arrowLeft;

    private EditText editText;

    private final int CHAT_CODE = 1;

    private MHandler mHandler;

    private RecyclerView recyclerView;

    private ChatMessageAdapter chatMessageAdapter;

    private Button sendButton;

    private PhageChatWebSocketClient phageChatWebSocketClient;

    private static final String WEBSOCKET_URL = "ws://192.168.102.1:10926/websocket/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        mHandler = new MHandler();



        usernameView = findViewById(R.id.chat_username);
        usernameView.setText(extras.getString("username"));

        arrowLeft = findViewById(R.id.chat_arrow_left);
        arrowLeft.setOnClickListener(view -> finish());


        SharedPreferences sharedPreferences = getSharedPreferences("data", 0);
        String user_id = sharedPreferences.getString("id", "");
        String avatar = sharedPreferences.getString("avatar", "");
        String phone = sharedPreferences.getString("phone", "");
        String username = sharedPreferences.getString("username", "");

        String to_id = extras.getString("id");

        recyclerView = findViewById(R.id.chat_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chatMessageAdapter = new ChatMessageAdapter(this, user_id);
        recyclerView.setAdapter(chatMessageAdapter);




        //        获取聊天列表
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl url = HttpUrl.parse(HttpUtil.PHAGE_SERVER_IP + "/chat/msg/list").newBuilder().addQueryParameter("s1", to_id).addQueryParameter("s2", user_id).build();

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
                msg.what = CHAT_CODE;
                msg.obj = res;
                mHandler.sendMessage(msg);
            }
        });

        sendButton = findViewById(R.id.chat_send_btn);
        editText = findViewById(R.id.chat_edit);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                com.phage.ex_sepim.entity.Message message = new com.phage.ex_sepim.entity.Message();
                message.setFromId(Integer.valueOf(user_id));
                message.setToId(Integer.valueOf(to_id));
                message.setContent(String.valueOf(editText.getText()));



                User user = new User();
                user.setId(Integer.valueOf(user_id));
                user.setAvatar(avatar);
                user.setPhone(phone);
                user.setUsername(username);
                message.setUser(user);
                List<com.phage.ex_sepim.entity.Message> list = chatMessageAdapter.getList();
                list.add(message);
                chatMessageAdapter.setList(list);
                recyclerView.scrollToPosition(chatMessageAdapter.getItemCount() - 1);

                //发送消息
                phageChatWebSocketClient.send(JSONObject.toJSONString(message));

                //                修改SQLite中的数据
                DbContect dbContect = new DbContect(ChatActivity.this);

                SQLiteDatabase readableDatabase = dbContect.getReadableDatabase();
                Cursor cursor = readableDatabase.rawQuery("select * from chat_tb where l_account = " + user_id + " and u_id=" + to_id, null);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SQLiteDatabase writableDatabase = dbContect.getWritableDatabase();
                if (cursor.moveToNext()) {
                    ContentValues values = new ContentValues();
                    values.put("no_read", 0);
                    values.put("content", String.valueOf(editText.getText()));
                    values.put("date", simpleDateFormat.format(new Date()));
                    editText.setText("");
                    writableDatabase.update("chat_tb", values, "l_account = ? and u_id = ?", new String[]{user_id, to_id});
                    writableDatabase.close();
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put("l_account", user_id);
                    cv.put("u_id", to_id);
                    cv.put("username", extras.getString("username"));
                    cv.put("avatar", extras.getString("avatar"));
                    cv.put("no_read", 0);
                    cv.put("content", String.valueOf(editText.getText()));
                    cv.put("date", simpleDateFormat.format(new Date()));
                    editText.setText("");
                    writableDatabase.insert("chat_tb", null, cv);
                }
            }
        });



        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    phageChatWebSocketClient = new PhageChatWebSocketClient(WEBSOCKET_URL + user_id);
                    if (phageChatWebSocketClient.connectBlocking()) {
                        System.out.println("=======================连接cg");
                    } else {
                        System.out.println("======================连接失败");
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        runnable.run();


    }

    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case CHAT_CODE:
                    if (msg.obj != null) {
                        String logResult = (String) msg.obj;
                        Result result = JSONObject.parseObject(logResult, Result.class);
                        if (200 == result.getCode()) {
//                                展示信息
                            JSONArray jsonArray = (JSONArray) result.getData();
                            List<com.phage.ex_sepim.entity.Message> list = JSONArray.parseArray(jsonArray.toJSONString(), com.phage.ex_sepim.entity.Message.class);
                            chatMessageAdapter.setList(list);
                            recyclerView.scrollToPosition(chatMessageAdapter.getItemCount() - 1);
                        }
                        break;
                    }
            }
        }
    }



    class PhageChatWebSocketClient extends WebSocketClient{

        public PhageChatWebSocketClient(String serverUri) throws URISyntaxException {
            super(new URI(serverUri));
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("====================连接成功");
        }

        @Override
        public void onMessage(String message) {
            com.phage.ex_sepim.entity.Message message1 = JSONObject.parseObject(message, com.phage.ex_sepim.entity.Message.class);
            List<com.phage.ex_sepim.entity.Message> list = chatMessageAdapter.getList();
            list.add(message1);
            chatMessageAdapter.setList(list);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {

        }

        @Override
        public void onError(Exception ex) {
            System.out.println("========================连接失败");
        }
    }



}
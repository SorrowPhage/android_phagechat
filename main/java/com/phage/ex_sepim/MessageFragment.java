package com.phage.ex_sepim;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.phage.ex_sepim.entity.Result;
import com.phage.ex_sepim.entity.SqlMessage;
import com.phage.ex_sepim.listtener.OnClickLinstener;
import com.phage.ex_sepim.listtener.OnRecyclerItemClickListener;
import com.phage.ex_sepim.utils.DbContect;
import com.phage.ex_sepim.utils.GlideCircleTransform;
import com.phage.ex_sepim.utils.HttpUtil;
import com.phage.ex_sepim.utils.MessageAdapter;
import com.phage.ex_sepim.utils.ServerMessageAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView listViewMessage;

    SimpleAdapter simpleAdapter;

    private ImageView avatar;

    private TextView message_username;

    private static final int LOGIN_MSG_CODE = 1;

    private MHandler mHandler;

    private RecyclerView recyclerView;

    private ServerMessageAdapter messageAdapter;

    private MessageAdapter messageAdapter2;

    private String l_account;

//    private ImageView message_logout;



    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);


//        导航页
        avatar = view.findViewById(R.id.user_image);



        mHandler = new MHandler();

        recyclerView = view.findViewById(R.id.messgae_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        messageAdapter = new ServerMessageAdapter(getActivity());
        messageAdapter.setListener(new OnClickLinstener() {
            @Override
            public void onItemClick(int Position, List<com.phage.ex_sepim.entity.Message> list) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(list.get(Position).getUser().getId()));
                bundle.putString("username", String.valueOf(list.get(Position).getUser().getUsername()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//        message_logout = view.findViewById(R.id.message_logout);
//        message_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//
////                删掉登录数据
//
//                startActivity(intent);
//
//            }
//        });

        messageAdapter2 = new MessageAdapter(getActivity());
        messageAdapter2.setListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(int Position, List<SqlMessage> list) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(list.get(Position).getU_id()));
                bundle.putString("username", String.valueOf(list.get(Position).getUsername()));
                bundle.putString("avatar", String.valueOf(list.get(Position).getAvatar()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


//        recyclerView.setAdapter(messageAdapter);
        recyclerView.setAdapter(messageAdapter2);


        Bundle arguments = getArguments();
//        listViewMessage = view.findViewById(R.id.message_lv);

        if (arguments!=null) {
//            展示，用户名的信息
            message_username = view.findViewById(R.id.message_username);
            message_username.setText(arguments.getString("username"));
            GlideCircleTransform glideCircleTransform = new GlideCircleTransform(getActivity());
            Glide.with(getActivity()).load(arguments.getString("avatar")).transform(glideCircleTransform).into(avatar);


//           找到和其它用户的最后一条信息的列表
            OkHttpClient okHttpClient = new OkHttpClient();

            l_account = arguments.getString("id");

            HttpUrl url = HttpUrl.parse(HttpUtil.PHAGE_SERVER_IP + "/chat/list").newBuilder().addQueryParameter("id", l_account).build();

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
        }

//        去导航页面
//        avatar.setOnClickListener(view12 -> {
//            Intent intent = new Intent(getActivity(), NavigationActicity.class);
//            startActivity(intent);
//        });
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
                            Log.e("TAG", "wang向右");

                        } else if (mCurPosX - mPosX < 0 && (Math.abs(mCurPosX - mPosX) > 150)) {
//                            Log.e("TAG", "wang向左");
                            Intent intent = new Intent(getActivity(),NavigationActicity.class);
                            startActivity(intent);
                        }
                        break;

                       /* if (mCurPosY - mPosY > 0
                                && (Math.abs(mCurPosY - mPosY) > 25)) {
                            Log.e("TAG", "wang向下");
                        } else if (mCurPosY - mPosY < 0
                                && (Math.abs(mCurPosY - mPosY) > 25)) {
                            Log.e("TAG", "wang向上");
                        }
                        break;*/
                }
                return false;
            }
        });

        return view;
    }




    private Bitmap bitmap;

    public Bitmap returnBitMap(String url){

        new Thread(new Runnable() {
            @Override
            public void run() {

                URL imageurl = null;

                InputStream is;

                try {
                    imageurl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return bitmap;
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
                        if (200 == result.getCode()) {
                            JSONArray  jsonArray= (JSONArray) result.getData();
                            List<com.phage.ex_sepim.entity.Message> list = JSONArray.parseArray(jsonArray.toJSONString(), com.phage.ex_sepim.entity.Message.class);
                            SQLiteOpenHelper helper = new DbContect(getActivity());
                            if (list.size() > 0) {
                                //将消息插入SQLite
                                for (com.phage.ex_sepim.entity.Message message : list) {
                                    //查看该用户是否在SQLite中
                                    SQLiteDatabase db = helper.getReadableDatabase();
                                    Cursor cursor = db.rawQuery("select * from chat_tb where l_account = " + l_account + " and u_id=" + message.getUser().getId(), null);
                                    SQLiteDatabase writableDatabase = helper.getWritableDatabase();
                                    ContentValues cv = new ContentValues();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    //该用户在SQLite中
                                    if (cursor.moveToNext()) {
                                        cv.put("avatar",message.getUser().getAvatar());
                                        cv.put("username", message.getUser().getUsername());
                                        cv.put("no_read", message.getNoReadNum());
                                        cv.put("content", message.getContent());
                                        cv.put("date", simpleDateFormat.format(message.getDate()));
                                        writableDatabase.update("chat_tb", cv, "l_account = ? and u_id = ?", new String[]{l_account, String.valueOf(message.getUser().getId())});
                                    } else {
                                        //该用户不在SQLite中
                                        cv.put("l_account", l_account);
                                        cv.put("u_id", message.getUser().getId());
                                        cv.put("username", message.getUser().getUsername());
                                        cv.put("avatar", message.getUser().getAvatar());
                                        cv.put("content", message.getContent());
                                        cv.put("no_read", message.getNoReadNum());
                                        cv.put("date", simpleDateFormat.format(message.getDate()));
                                        writableDatabase.insert("chat_tb", null, cv);
                                    }
                                    writableDatabase.close();
                                }
                            }

                            SQLiteDatabase db = helper.getReadableDatabase();
                            Cursor sm = db.query("chat_tb", null, null, null, null, null, null);
                            List<SqlMessage> sqlMessageArrayList = new ArrayList<>();
                            while (sm.moveToNext()) {
                                SqlMessage sqlMessage = new SqlMessage();
                                sqlMessage.setId(sm.getInt(0));
                                sqlMessage.setL_account(sm.getInt(1));
                                sqlMessage.setU_id(sm.getInt(2));
                                sqlMessage.setUsername(sm.getString(3));
                                sqlMessage.setAvatar(sm.getString(4));
                                sqlMessage.setContent(sm.getString(5));
                                sqlMessage.setDate(sm.getString(6));
                                sqlMessage.setNoRead(sm.getInt(7));
                                sqlMessageArrayList.add(sqlMessage);
                            }
                            db.close();

//                            messageAdapter.setList(list);
                            messageAdapter2.setList(sqlMessageArrayList);
                        }
                        break;
                    }
            }
        }
    }
}
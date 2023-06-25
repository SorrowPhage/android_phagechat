package com.phage.ex_sepim;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.phage.ex_sepim.entity.Result;
import com.phage.ex_sepim.entity.User;
import com.phage.ex_sepim.listtener.OnClickUserListener;
import com.phage.ex_sepim.utils.ConstactsAdapter;
import com.phage.ex_sepim.utils.GlideCircleTransform;
import com.phage.ex_sepim.utils.HttpUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView avatar;

    private static final int CON_CODE = 1;

    private MHandler mHandler;

    private RecyclerView recyclerView;

    private ConstactsAdapter constactsAdapter;

    private EditText chat_search;


    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        avatar = view.findViewById(R.id.user_contacts_image);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NavigationActicity.class);
                startActivity(intent);
            }
        });

        Bundle arguments = getArguments();

        mHandler = new MHandler();
        if (arguments != null) {
            GlideCircleTransform glideCircleTransform = new GlideCircleTransform(getActivity());
            Glide.with(getActivity()).load(arguments.getString("avatar")).transform(glideCircleTransform).into(avatar);


            recyclerView = view.findViewById(R.id.constacts_rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            constactsAdapter = new ConstactsAdapter(getActivity());
            constactsAdapter.setOnClickUser(new OnClickUserListener() {
                @Override
                public void onItemClick(int Position, List<User> list) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", String.valueOf(list.get(Position).getId()));
                    bundle.putString("username", String.valueOf(list.get(Position).getUsername()));
                    bundle.putString("avatar", String.valueOf(list.get(Position).getAvatar()));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            recyclerView.setAdapter(constactsAdapter);




            OkHttpClient okHttpClient = new OkHttpClient();

            HttpUrl url = HttpUrl.parse(HttpUtil.PHAGE_SERVER_IP + "/user/ass/list").newBuilder().addQueryParameter("id", arguments.getString("id")).build();

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
                    msg.what = CON_CODE;
                    msg.obj = res;
                    mHandler.sendMessage(msg);
                }
            });
        }

        chat_search = view.findViewById(R.id.chat_search);
        chat_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }

    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case CON_CODE:
                    if (msg.obj != null) {
                        String logResult = (String) msg.obj;
                        Result result = JSONObject.parseObject(logResult, Result.class);
                        if (200 == result.getCode()) {
                            JSONArray jsonArray= (JSONArray) result.getData();
                            List<User> list = JSONArray.parseArray(jsonArray.toJSONString(), User.class);
                            constactsAdapter.setList(list);
                        }
                        break;
                    }
            }
        }
    }
}
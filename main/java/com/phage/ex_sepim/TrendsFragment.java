package com.phage.ex_sepim;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.phage.ex_sepim.utils.GlideCircleTransform;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView avatar;

    private TextView trends_username;

//    private ImageView trends_edit_username;

    private Button trends_logout,trends_edit;

    public TrendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendsFragment newInstance(String param1, String param2) {
        TrendsFragment fragment = new TrendsFragment();
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
        View view = inflater.inflate(R.layout.fragment_trends, container, false);
        avatar = view.findViewById(R.id.trends_avatar);
        trends_username = view.findViewById(R.id.trends_username);
        trends_logout = view.findViewById(R.id.trends_logout);
        Bundle arguments = getArguments();

        if (arguments != null) {
            trends_username.setText(arguments.getString("username"));
            GlideCircleTransform glideCircleTransform = new GlideCircleTransform(getActivity());
            Glide.with(getActivity()).load(arguments.getString("avatar")).transform(glideCircleTransform).into(avatar);

            trends_edit = view.findViewById(R.id.trends_edit);
            trends_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), UserCenterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", arguments.getString("id"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            trends_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });

        }

        return view;
    }
}
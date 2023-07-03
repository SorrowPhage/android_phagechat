package com.phage.ex_sepim.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.phage.ex_sepim.R;
import com.phage.ex_sepim.entity.User;
import com.phage.ex_sepim.listtener.OnClickUserListener;

import java.util.ArrayList;
import java.util.List;

public class ConstactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private EditText chat_search;

    private Context context;

    private List<User> list = new ArrayList<>();

    private OnClickUserListener onRecyclerItemClickListener;

    private String text;

    private ForegroundColorSpan span;

    public ConstactsAdapter(Context context) {
        this.context = context;
    }

    public void setText(String text,ForegroundColorSpan span) {
        this.text = text;
        this.span = span;
        notifyDataSetChanged();
    }


    public void setList(List<User> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<User> getList() {
        return list;
    }

    public void setOnClickUser(OnClickUserListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView constacts_image;

        private TextView constacts_name, constacts_des;

        private LinearLayout constacts_list;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            constacts_image = itemView.findViewById(R.id.constacts_image);
            constacts_name = itemView.findViewById(R.id.constacts_name);
            constacts_des = itemView.findViewById(R.id.constacts_des);
            constacts_list = itemView.findViewById(R.id.constacts_list);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRecyclerItemClickListener != null) {
                        onRecyclerItemClickListener.onItemClick(getAdapterPosition(), list);
                    }
                }
            });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.constacts_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = list.get(position);
        if (text != null) {
            int i = user.getUsername().indexOf(text);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(user.getUsername());
            if (i != -1) {
                ((MyViewHolder) holder).constacts_list.setVisibility(View.VISIBLE);
                spannableStringBuilder.setSpan(span, i, i + text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((MyViewHolder) holder).constacts_name.setText(spannableStringBuilder);
                ((MyViewHolder) holder).constacts_des.setText(user.getDes());
                Glide.with(context).load(user.getAvatar()).error(R.mipmap.ic_launcher_round).into(((MyViewHolder) holder).constacts_image);
            } else {
                ((MyViewHolder) holder).constacts_list.setVisibility(View.INVISIBLE);
            }
        } else {
            ((MyViewHolder) holder).constacts_list.setVisibility(View.VISIBLE);
            ((MyViewHolder) holder).constacts_name.setText(user.getUsername());
            ((MyViewHolder) holder).constacts_des.setText(user.getDes());
            Glide.with(context).load(user.getAvatar()).error(R.mipmap.ic_launcher_round).into(((MyViewHolder) holder).constacts_image);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

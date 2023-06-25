package com.phage.ex_sepim.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.phage.ex_sepim.R;
import com.phage.ex_sepim.entity.SqlMessage;
import com.phage.ex_sepim.listtener.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private List<SqlMessage> list = new ArrayList<>();

    private OnRecyclerItemClickListener listener;

    public void setListener(OnRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<SqlMessage> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        TextView name,date, des,message_noread;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.message_image);
            name = itemView.findViewById(R.id.message_name);
            date = itemView.findViewById(R.id.message_date);
            des = itemView.findViewById(R.id.message_des);
            message_noread = itemView.findViewById(R.id.message_noread);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition(), list);
                    }
                }
            });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_message, parent, false);
        RecyclerView.ViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SqlMessage message = list.get(position);
        ((MyViewHolder) holder).name.setText(message.getUsername());
        ((MyViewHolder) holder).date.setText(message.getDate());
        ((MyViewHolder) holder).des.setText(message.getContent());
        if (message.getNoRead() == 0) {
            ((MyViewHolder) holder).message_noread.setVisibility(View.INVISIBLE);
        } else {
            ((MyViewHolder) holder).message_noread.setText(message.getNoRead().toString());
        }

        Glide.with(context)
                .load(message.getAvatar())
                .error(R.mipmap.ic_launcher_round)
                .into(((MyViewHolder) holder).imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

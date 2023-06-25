package com.phage.ex_sepim.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.phage.ex_sepim.R;
import com.phage.ex_sepim.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private List<Message> list = new ArrayList<>();

    private String user_id;

    public void setList(List<Message> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Message> getList() {
        return this.list;
    }

    public ChatMessageAdapter(Context context, String user_id) {
        this.user_id = user_id;
        this.context = context;
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView chat_message_image,chat_host_message_image;
        private TextView chat_message_text,chat_host_message_text;

        private LinearLayout chat_left, chat_right;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chat_message_image = itemView.findViewById(R.id.chat_message_image);
            chat_message_text = itemView.findViewById(R.id.chat_message_text);
            chat_left = itemView.findViewById(R.id.chat_left);
            chat_host_message_image = itemView.findViewById(R.id.chat_host_message_image);
            chat_host_message_text = itemView.findViewById(R.id.chat_host_message_text);
            chat_right = itemView.findViewById(R.id.chat_right);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_chat_message, parent, false);

        RecyclerView.ViewHolder viewHolder = new ChatViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = list.get(position);
        GlideCircleTransform glideCircleTransform = new GlideCircleTransform(context);
        if (user_id.equals(message.getUser().getId().toString())) {
//            右侧消息

            ((ChatViewHolder) holder).chat_right.setVisibility(View.VISIBLE);
            ((ChatViewHolder) holder).chat_left.setVisibility(View.GONE);
            ((ChatViewHolder) holder).chat_host_message_text.setText(message.getContent());
            Glide.with(context).load(message.getUser().getAvatar()).transform(glideCircleTransform).error(R.mipmap.ic_launcher_round).into(((ChatViewHolder) holder).chat_host_message_image);
        } else {
//            左侧消息
            ((ChatViewHolder) holder).chat_right.setVisibility(View.GONE);
            ((ChatViewHolder) holder).chat_left.setVisibility(View.VISIBLE);
            ((ChatViewHolder) holder).chat_message_text.setText(message.getContent());
            Glide.with(context).load(message.getUser().getAvatar()).transform(glideCircleTransform).error(R.mipmap.ic_launcher_round).into(((ChatViewHolder) holder).chat_message_image);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

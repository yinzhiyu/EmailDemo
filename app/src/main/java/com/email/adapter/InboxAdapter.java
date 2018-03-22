package com.email.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.email.R;
import com.email.bean.Email;

import java.util.ArrayList;
import java.util.List;


public class InboxAdapter extends RecyclerView.Adapter {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private IndoxHolder IndoxHolder;
    private ArrayList<Email> list;

    public InboxAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }


    //    给接口变量赋值
    @Override
    public IndoxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        IndoxHolder holder;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.email_mailbox_item, parent, false);
        holder = new IndoxHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        // 给ViewHolder设置元素
        final Email email = list.get(position);
        if (holder instanceof IndoxHolder) {
            IndoxHolder = (IndoxHolder) holder;
            IndoxHolder.tv_from.setText("发件人：" + email.getFrom());
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class IndoxHolder extends RecyclerView.ViewHolder {
        TextView tv_from;
        TextView tv_new;
        TextView tv_subject;
        ImageView tv_sentdate;

        IndoxHolder(View view) {
            super(view);
            tv_from = (TextView) view.findViewById(R.id.tv_from);
            tv_new = (TextView) view.findViewById(R.id.tv_new);
            tv_subject = (TextView) view.findViewById(R.id.tv_subject);
            tv_sentdate = (ImageView) view.findViewById(R.id.tv_sentdate);

        }
    }

}
package com.email.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.framewok.base.ListBaseAdapter;
import com.email.R;
import com.email.bean.ItemModel;


public class DataAdapter extends ListBaseAdapter<ItemModel> {

    private LayoutInflater mLayoutInflater;

    public DataAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.email_mailbox_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemModel item = mDataList.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.textView.setText(".445");
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_from);
        }
    }
}

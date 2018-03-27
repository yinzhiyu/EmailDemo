package com.email.adapter;

import android.content.Context;
import android.widget.TextView;

import com.android.framewok.base.ListBaseAdapter;
import com.android.framewok.base.SuperViewHolder;
import com.email.R;
import com.email.bean.ItemModel;


public class DataAdapter extends ListBaseAdapter<ItemModel> {

    public DataAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.email_mailbox_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        ItemModel item = mDataList.get(position);

        TextView tv_from = holder.getView(R.id.tv_from);
        TextView tv_new = holder.getView(R.id.tv_new);
        TextView tv_subject = holder.getView(R.id.tv_subject);
        TextView tv_sentdate = holder.getView(R.id.tv_sentdate);
        tv_from.setText(item.getFrom());
        tv_new.setText(item.getContent());
        tv_subject.setText(item.getSubject());
        tv_sentdate.setText(item.getSentdata());
    }

    @Override
    public void onViewRecycled(SuperViewHolder holder) {
        super.onViewRecycled(holder);
    }

}
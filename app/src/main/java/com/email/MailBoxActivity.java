
package com.email;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.email.adapter.DataAdapter;
import com.email.app.MyApplication;
import com.email.bean.Email;
import com.email.bean.ItemModel;
import com.email.service.MailHelper;
import com.email.service.MailReceiver;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

public class MailBoxActivity extends Activity {

    private ArrayList<Email> mailslist = new ArrayList<Email>();
    private ArrayList<ItemModel> dataList = new ArrayList<ItemModel>();
    private ArrayList<ArrayList<InputStream>> attachmentsInputStreamsList = new ArrayList<ArrayList<InputStream>>();
    private String type;
    private int status;
    //    private MyAdapter myAdapter;
    private DataAdapter mDataAdapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private LRecyclerView lv_box;
    private LRecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
    private List<MailReceiver> mailReceivers;
    private ProgressDialog dialog;
    private Uri uri=Uri.parse("content://com.emailstatusprovider");
    private List<String> messageids;

    private int totalPage;//邮件总数


    private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
                mDataAdapter.notifyDataSetChanged();
                break;
                case 1:
                    dialog.dismiss();
                    Toast.makeText(MailBoxActivity.this, "网络连接超时", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MailBoxActivity.this, HomeActivity.class);
                    startActivity(intent);
			case 2:
				dialog.dismiss();
				break;
			}
			super.handleMessage(msg);
		}
    	
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        type = getIntent().getStringExtra("TYPE");
        status=getIntent().getIntExtra("status", -1);
        
        setContentView(R.layout.email_mailbox);
        initView();

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					mailReceivers = MailHelper
//							.getInstance(MailBoxActivity.this).getAllMail(type);
//				} catch (MessagingException e) {
//					e.printStackTrace();
//					handler.sendEmptyMessage(1);
//				}
//				// 去数据库查询
//				messageids = getAllMessageids();
//				switch (status) {
//				case 0://查询全部
//					getAllMails(mailReceivers);
//					break;
//				case 1://查询未读
//					getNotRead(mailReceivers);
//					break;
//				case 2://查询已读
//					getYesRead(mailReceivers);
//					break;
//				}
//
//				handler.sendEmptyMessage(2);
//			}
//		}).start();

    }

    //    private void initView() {
//        mRecyclerView = findViewById(R.id.mRecyclerView);
////        myAdapter = new MyAdapter();
////        lv_box.setAdapter(myAdapter);
//        mDataAdapter = new DataAdapter(this);
////        mDataAdapter.setData(dataList);
//
//        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
//        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
//
//        dialog=new ProgressDialog(this);
//        dialog.setMessage("正加载");
//        dialog.show();
//
//
//    }
    private void initView() {
        mRecyclerView = findViewById(R.id.mRecyclerView);
//        myAdapter = new MyAdapter();
//        lv_box.setAdapter(myAdapter);

        //关于添加分割线
//        DividerDecoration divider = new DividerDecoration.Builder(this,mLRecyclerViewAdapter)
//                .setHeight(R.dimen.default_divider_height)
//                .setPadding(R.dimen.default_divider_padding)
//                .setColorResource(R.color.colorAccent)
//                .build();
//        mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader); //设置下拉刷新Progress的样式
//        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);  //设置下拉刷新箭头
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.dark, android.R.color.white);
//设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.dark, android.R.color.white);
//设置底部加载文字提示
        mRecyclerView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");
        /**
         * onScrollUp()——RecyclerView向上滑动的监听事件；
         onScrollDown()——RecyclerView向下滑动的监听事件；
         onScrolled()——RecyclerView正在滚动的监听事件；
         onScrollStateChanged(int state)——RecyclerView正在滚动的监听事件；
         */
        mRecyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {

            }

            @Override
            public void onScrollStateChanged(int state) {

            }
        });
        //下拉刷新
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.refreshComplete(10);
                mLRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        //加载更多
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

//                if (mCurrentPage < totalPage) {
//                    // loading data
//                    requestData();
//                } else {
                mRecyclerView.setNoMore(true);
//                }
            }
        });
        // 网络异常出错代码处理如下：
        mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
            @Override
            public void reload() {
                requestData();
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setMessage("正加载");
        dialog.show();


    }

    private void requestData() {
        try {
            mailReceivers = MailHelper
                    .getInstance(MailBoxActivity.this).getAllMail(type);
        } catch (MessagingException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(1);
        }
        // 去数据库查询
        messageids = getAllMessageids();
        switch (status) {
            case 0://查询全部
                getAllMails(mailReceivers);
                break;
            case 1://查询未读
                getNotRead(mailReceivers);
                break;
            case 2://查询已读
                getYesRead(mailReceivers);
                break;
        }

    }

    /**
     * 查询出已读邮件
     * @return
     */
    private List<String> getAllMessageids(){
    	List<String> messageids=new ArrayList<String>();
    	Cursor c=getContentResolver().query(uri, null, "mailfrom=?", new String[]{MyApplication.info.getUserName()}, null);
    	while(c.moveToNext()){
    		messageids.add(c.getString(2));
    	}
    	return messageids;
    }

    
    
    /**
     * 适配器
     * @author Administrator
     *
     */
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mailslist.size();
        }

        @Override
        public Object getItem(int position) {
            return mailslist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(MailBoxActivity.this).inflate(R.layout.email_mailbox_item, null);
            TextView tv_from = (TextView) convertView.findViewById(R.id.tv_from);
            tv_from.setText(mailslist.get(position).getFrom());
            TextView tv_sentdate = (TextView) convertView.findViewById(R.id.tv_sentdate);
            tv_sentdate.setText(mailslist.get(position).getSentdata());
            if (mailslist.get(position).isNews()) {
                TextView tv_new = (TextView) convertView.findViewById(R.id.tv_new);
                tv_new.setVisibility(View.VISIBLE);
            }
            TextView tv_subject = (TextView) convertView.findViewById(R.id.tv_subject);
            tv_subject.setText(mailslist.get(position).getSubject());
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                	//点击表示已读把ID存入数据库
                	String mailID=mailslist.get(position).getMessageID();
                    if(!messageids.contains(mailID)){
                    	ContentValues values=new ContentValues();
                    	values.put("mailfrom", MyApplication.info.getUserName());
                    	values.put("messageid", mailID);
                    	getContentResolver().insert(uri, values);
                	 }
                	
                    ((MyApplication)getApplication()).setAttachmentsInputStreams(attachmentsInputStreamsList.get(position));
                    final Intent intent = new Intent(MailBoxActivity.this, MailContentActivity.class).putExtra("EMAIL", mailslist.get(position));
                    startActivity(intent);
                }
            });
            return convertView;
        }

    }
    
    /**
     * 获取所有邮件
     * @param mails
     */
    private void getAllMails(List<MailReceiver> mails) {
        totalPage = mails.size();
        for (MailReceiver mailReceiver : mails) {
//            Email email = new Email();
            ItemModel email = new ItemModel();
            try {
                email.setMessageID(mailReceiver.getMessageID());
                email.setFrom(mailReceiver.getFrom());
                email.setTo(mailReceiver.getMailAddress("TO"));
                email.setCc(mailReceiver.getMailAddress("CC"));
                email.setBcc(mailReceiver.getMailAddress("BCC"));
                email.setSubject(mailReceiver.getSubject());
                email.setSentdata(mailReceiver.getSentData());
                email.setContent(mailReceiver.getMailContent());
                email.setReplysign(mailReceiver.getReplySign());
                email.setHtml(mailReceiver.isHtml());
                email.setNews(mailReceiver.isNew());
                email.setAttachments(mailReceiver.getAttachments());
                email.setCharset(mailReceiver.getCharset());
                attachmentsInputStreamsList.add(0,mailReceiver.getAttachmentsInputStreams());
//                mailslist.add(0, email);
                dataList.add(0, email);
                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mDataAdapter = new DataAdapter(this);
        mDataAdapter.setDataList(dataList);

        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
    }
    
    /**
     * 查询未读
     */
    private void getNotRead(List<MailReceiver> mails){
        totalPage = mails.size();
        for (MailReceiver mailReceiver : mails) {
            Email email = new Email();
            try {
            	if(messageids.contains(mailReceiver.getMessageID())){
                	continue;
                }
                email.setMessageID(mailReceiver.getMessageID());
                email.setFrom(mailReceiver.getFrom());
                email.setTo(mailReceiver.getMailAddress("TO"));
                email.setCc(mailReceiver.getMailAddress("CC"));
                email.setBcc(mailReceiver.getMailAddress("BCC"));
                email.setSubject(mailReceiver.getSubject());
                email.setSentdata(mailReceiver.getSentData());
                email.setContent(mailReceiver.getMailContent());
                email.setReplysign(mailReceiver.getReplySign());
                email.setHtml(mailReceiver.isHtml());
                email.setNews(mailReceiver.isNew());
                email.setAttachments(mailReceiver.getAttachments());
                email.setCharset(mailReceiver.getCharset());
                attachmentsInputStreamsList.add(0,mailReceiver.getAttachmentsInputStreams());
                mailslist.add(0, email);
                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
    	}
    }
    
    /**
     * 查询已读
     */
    private void getYesRead(List<MailReceiver> mails){
        totalPage = mails.size();
        for (MailReceiver mailReceiver : mails) {
            Email email = new Email();
            try {
            	if(messageids.contains(mailReceiver.getMessageID())){
            		email.setMessageID(mailReceiver.getMessageID());
                    email.setFrom(mailReceiver.getFrom());
                    email.setTo(mailReceiver.getMailAddress("TO"));
                    email.setCc(mailReceiver.getMailAddress("CC"));
                    email.setBcc(mailReceiver.getMailAddress("BCC"));
                    email.setSubject(mailReceiver.getSubject());
                    email.setSentdata(mailReceiver.getSentData());
                    email.setContent(mailReceiver.getMailContent());
                    email.setReplysign(mailReceiver.getReplySign());
                    email.setHtml(mailReceiver.isHtml());
                    email.setNews(mailReceiver.isNew());
                    email.setAttachments(mailReceiver.getAttachments());
                    email.setCharset(mailReceiver.getCharset());
                    attachmentsInputStreamsList.add(0,mailReceiver.getAttachmentsInputStreams());
                    mailslist.add(0, email);
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    	}
    }
}

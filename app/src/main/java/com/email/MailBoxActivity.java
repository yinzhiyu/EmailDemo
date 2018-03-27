
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
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.email.adapter.DataAdapter;
import com.email.adapter.SampleFooter;
import com.email.app.MyApplication;
import com.email.bean.Email;
import com.email.bean.ItemModel;
import com.email.service.MailHelper;
import com.email.service.MailReceiver;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

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
    private LRecyclerView mRecyclerView;
    private List<MailReceiver> mailReceivers;
    private ProgressDialog dialog;
    private Uri uri=Uri.parse("content://com.emailstatusprovider");
    private List<String> messageids;
    private LinearLayout empty_view;
    private int totalPage;//邮件总数


    private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
                dialog.dismiss();
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
        empty_view = findViewById(R.id.empty_view);
        requestData();

    }

    private void requestData() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("正加载");
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        mailReceivers = MailHelper
                                .getInstance(MailBoxActivity.this).getAllMail(type);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MailBoxActivity.this, "发生不可预知的致命错误！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                        setLRecycView();
                    }
                });
            }
        }).start();
    }

    private void requestRData() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("正加载");
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        mailReceivers = MailHelper
                                .getInstance(MailBoxActivity.this).getAllMail(type);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MailBoxActivity.this, "发生不可预知的致命错误！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        totalPage = mailReceivers.size();
                        for (MailReceiver mailReceiver : mailReceivers) {
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
//                email.setContent(mailReceiver.getMailContent());
                                email.setReplysign(mailReceiver.getReplySign());
                                email.setHtml(mailReceiver.isHtml());
                                email.setNews(mailReceiver.isNew());
                                email.setAttachments(mailReceiver.getAttachments());
                                email.setCharset(mailReceiver.getCharset());
                                attachmentsInputStreamsList.add(0, mailReceiver.getAttachmentsInputStreams());
//                mailslist.add(0, email);
                                dataList.add(0, email);
                                handler.sendEmptyMessage(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        mDataAdapter.setDataList(dataList);
                        mRecyclerView.refreshComplete(totalPage);
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }

    private void setLRecycView() {
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.default_divider_height)
                .setColorResource(R.color.split)
                .build();
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        //add a HeaderView
//        View header = LayoutInflater.from(this).inflate(R.layout.sample_header, (ViewGroup) findViewById(android.R.id.content), false);
//
//        mLRecyclerViewAdapter.addHeaderView(header);
//        mLRecyclerViewAdapter.addHeaderView(new SampleHeader(this));


        //禁用下拉刷新功能
        mRecyclerView.setPullRefreshEnabled(true);

        //禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(false);

        SampleFooter sampleFooter = new SampleFooter(this);
        sampleFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 加载更多
//                ArrayList<ItemModel> dataList = new ArrayList<>();
//                for (int i = 0; i < 10; i++) {
//                    ItemModel itemModel = new ItemModel();
//                    itemModel.title = "item" + (i + mDataAdapter.getItemCount());
//                    dataList.add(itemModel);
//                }
//                mDataAdapter.addAll(dataList);
            }
        });
        //add a FooterView
        mLRecyclerViewAdapter.addFooterView(sampleFooter);


        //删除item
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                mDataAdapter.remove(position);
                Toast.makeText(MailBoxActivity.this, "不要點我啦...", Toast.LENGTH_SHORT).show();
            }

        });
        //下拉刷新
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                int currentSize = mDataAdapter.getItemCount();
                requestRData();
//                ArrayList<ItemModel> dataList = new ArrayList<>();
//                for (int i = 0; i < 10; i++) {
//                    ItemModel itemModel = new ItemModel();
//                    itemModel.title = "item" + i;
//                    dataList.add(itemModel);
//                }
                mDataAdapter.setDataList(dataList);
                mRecyclerView.refreshComplete(currentSize);
                mLRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
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
//                email.setContent(mailReceiver.getMailContent());
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
            ItemModel email = new ItemModel();
            try {
                if (messageids.contains(mailReceiver.getMessageID())) {

                    email.setMessageID(mailReceiver.getMessageID());
                    email.setFrom(mailReceiver.getFrom());
                    email.setTo(mailReceiver.getMailAddress("TO"));
                    email.setCc(mailReceiver.getMailAddress("CC"));
                    email.setBcc(mailReceiver.getMailAddress("BCC"));
                    email.setSubject(mailReceiver.getSubject());
                    email.setSentdata(mailReceiver.getSentData());
//                email.setContent(mailReceiver.getMailContent());
                    email.setReplysign(mailReceiver.getReplySign());
                    email.setHtml(mailReceiver.isHtml());
                    email.setNews(mailReceiver.isNew());
                    email.setAttachments(mailReceiver.getAttachments());
                    email.setCharset(mailReceiver.getCharset());
                    attachmentsInputStreamsList.add(0, mailReceiver.getAttachmentsInputStreams());
                    dataList.add(0, email);
                    handler.sendEmptyMessage(0);
                } else {
                    mRecyclerView.setEmptyView(empty_view);
                    dialog.dismiss();
                }
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
     * 查询已读
     */
    private void getYesRead(List<MailReceiver> mails){
        totalPage = mails.size();
        for (MailReceiver mailReceiver : mails) {
            ItemModel email = new ItemModel();
            try {
                if (messageids.contains(mailReceiver.getMessageID())) {
                    email.setMessageID(mailReceiver.getMessageID());
                    email.setFrom(mailReceiver.getFrom());
                    email.setTo(mailReceiver.getMailAddress("TO"));
                    email.setCc(mailReceiver.getMailAddress("CC"));
                    email.setBcc(mailReceiver.getMailAddress("BCC"));
                    email.setSubject(mailReceiver.getSubject());
                    email.setSentdata(mailReceiver.getSentData());
//                    email.setContent(mailReceiver.getMailContent());
                    email.setReplysign(mailReceiver.getReplySign());
                    email.setHtml(mailReceiver.isHtml());
                    email.setNews(mailReceiver.isNew());
                    email.setAttachments(mailReceiver.getAttachments());
                    email.setCharset(mailReceiver.getCharset());
                    attachmentsInputStreamsList.add(0,mailReceiver.getAttachmentsInputStreams());
                    dataList.add(0, email);
                    handler.sendEmptyMessage(0);
                } else {
                    mRecyclerView.setEmptyView(empty_view);
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mDataAdapter = new DataAdapter(this);
            mDataAdapter.setDataList(dataList);

            mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
            mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        }
    }
}

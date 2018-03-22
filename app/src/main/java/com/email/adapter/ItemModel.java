package com.email.adapter;


import com.android.framewok.bean.Entity;

import java.util.ArrayList;

public class ItemModel extends Entity {
    private static final long serialVersionUID = 1L;
    private String messageID;
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String sentdata;
    private String content;
    private boolean replysign;
    private boolean html;
    private boolean news;
    private ArrayList<String> attachments;
    private String charset;

}

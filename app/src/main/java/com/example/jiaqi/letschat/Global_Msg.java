package com.example.jiaqi.letschat;

/**
 * Created by LG on 2017/2/4.
 */
public class Global_Msg {
    public static final int TYPE_Log = 2;
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;
    private String sender = null;
    public Global_Msg(String content, int type, String sender) {
        this.content = content;
        this.type = type;
        this.sender = sender;
    }
    public String getContent() {
        return content;
    }
    public int getType() {
        return type;
    }

}

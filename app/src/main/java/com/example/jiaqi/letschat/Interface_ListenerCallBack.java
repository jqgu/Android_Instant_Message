package com.example.jiaqi.letschat;

import java.net.Socket;

/**
 * Created by LG on 2017/2/5.
 */
public interface Interface_ListenerCallBack extends Runnable{
    void getMessage(Socket socket);
}

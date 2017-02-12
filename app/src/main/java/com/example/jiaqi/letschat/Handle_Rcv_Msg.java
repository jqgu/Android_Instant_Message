package com.example.jiaqi.letschat;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by LG on 2017/2/5.
 */
public class Handle_Rcv_Msg implements Runnable {
    Handler handler = null;
    Socket socket = null;

    public Handle_Rcv_Msg(Handler handler, Socket socket) {
        this.handler = handler;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            return;
        }
        InputStream is = null;
        BufferedReader reader = null;
        Global_Msg msg = null;
        try {
            is = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            String name = reader.readLine();
            String line = null;
            StringBuilder tmp = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                tmp.append(line);
            }
            msg = new Global_Msg(tmp.toString(), Global_Msg.TYPE_RECEIVED, name);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (msg != null) {
            Message message = handler.obtainMessage();
            message.what = 1;
            message.obj = msg;
            handler.sendMessage(message);
        }

    }
}

package com.example.jiaqi.letschat;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * Created by LG on 2017/2/4.
 */
public class Global_Conn {
    private static String ServerIP = "192.168.0.115";
    private static int ServerPort = 8888;

    private static Socket socket;
    private static DataOutputStream writer;
    private static DataInputStream reader;
    private static MessageThread messageThread;

    public static void Login(final Interface_LoginCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ServerIP, ServerPort);
                    reader = new DataInputStream(socket.getInputStream());
                    sendMessage(new Global_Msg("", Global_Msg.TYPE_Log, Global_User.getInstance().getName()));
                } catch (Exception e) {
                    callBack.onError(e);
                    return;
                }
                callBack.onFinish(Global_User.getInstance().getName() + " join the chat!");
            }
        }).start();
    }

    private static void AddData(byte[] data, String content) {
        int length = content.length();
        Log.d("Loginfo","content length is "+length + ", "+content);
        data[0] = (byte) (length & 0xFF);

        byte[] tmp = content.getBytes();
        for (int i = 1; i < data.length; i++) {
            data[i] = tmp[i - 1];
        }
    }

    public static void sendMessage(Global_Msg message) {
        String content = Global_User.getInstance().getName() + "@" + message.getContent();
        byte[] data = new byte[1 + content.getBytes().length];
        AddData(data, content);
        try {
            writer = new DataOutputStream(socket.getOutputStream());
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Listen(Handler handler) {
        messageThread = new MessageThread(handler);
        messageThread.start();
    }

    private static int getDataLen(byte[] tmp) {
//        int value = (int) ((tmp[3] & 0xFF)
//                | ((tmp[2] & 0xFF) << 8)
//                | ((tmp[1] & 0xFF) << 16)
//                | ((tmp[0] & 0xFF) << 24));

        int value = (tmp[0] & 0xFF);

        return value;
    }

    private static String getData(byte[] tmp) {
        return new String(tmp);
    }

    static class MessageThread extends Thread {
        private Handler handler;

        public MessageThread(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            String message = null;
//            try {
//                reader = new DataInputStream(socket.getInputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            while (true) {
                try {
                    byte[] tmp = new byte[1];
                    reader.read(tmp, 0, 1);
                    int dataLen = getDataLen(tmp);
                    Log.d("message","content length is "+dataLen);
                    if(dataLen == 0){
                        continue;
                    }
                    byte[] data = new byte[dataLen];
                    reader.read(data, 0, dataLen);
                    message = getData(data);
                    Log.d("message","get message: "+message);
                    StringTokenizer st = new StringTokenizer(message, "@");
                    String name = st.nextToken();
                    String content = name + " says: " + st.nextToken();

                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    Global_Msg receiveMsg = new Global_Msg(content, Global_Msg.TYPE_RECEIVED, name);
                    msg.obj = receiveMsg;
                    handler.sendMessage(msg);
                    Log.d("message", "get message: " + message+", send!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


//        public static void sendMessage(Global_Msg m) {
//            final String content = m.getContent();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Socket socket = null;
//                    try {
//                        socket = new Socket(ServerIP, ServerPort);
//                        socket.setKeepAlive(true);
//                        socket.setSoTimeout(20 * 1000);
//                        OutputStream os = socket.getOutputStream();
//                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
//                        bw.write("1\n" + Global_User.getInstance().getName() + "\n" + content);
//                        bw.flush();
//                    } catch (UnknownHostException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        if (socket != null) {
//                            try {
//                                socket.close();
//                                Log.d("Send", "close socket");
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }).start();
//        }
//
//        private static Thread Thread_Listener = null;
//        private static ServerSocket Listener_Socket = null;
//
//        public static void startListener(final Handler handler) {
//            Thread_Listener = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    int flag = 1;
//                    try {
//                        Listener_Socket = new ServerSocket(LocalPort);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.d("Listening", "create socket fialed");
//                        return;
//                    }
//                    while (flag == 1) {
//                        try {
//                            Socket socket = Listener_Socket.accept();
//                            new Thread(new Handle_Rcv_Msg(handler, socket)).start();
//                        } catch (SocketException e2) {
//                            Log.d("Listening", "socket exp, exit thread");
//                            flag = 0;
//                        } catch (IOException e) {
//                            Log.d("Listening", "create listening socket fails");
//                            flag = 0;
//                            e.printStackTrace();
//                        }
//                    }
//                    if (Listener_Socket != null) {
//                        try {
//                            Listener_Socket.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            Thread_Listener.start();
//        }

//        public static void onExit() {
//            if (Thread_Listener != null && Listener_Socket != null) {
//                try {
//                    Listener_Socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
}

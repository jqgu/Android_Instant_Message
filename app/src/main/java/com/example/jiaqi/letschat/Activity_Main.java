package com.example.jiaqi.letschat;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Activity_Main extends AppCompatActivity {
    private ListView listView = null;
    private EditText editText = null;
    private ImageButton send = null;
    private MsgAdapter adapter = null;
    private List<Global_Msg> messageList = new ArrayList<>();
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.input);
        send = (ImageButton) findViewById(R.id.send);
        adapter = new MsgAdapter(Activity_Main.this, R.layout.message_item, messageList);
        listView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if (!content.equals("")) {
                    Global_Msg msg = new Global_Msg(content, Global_Msg.TYPE_SENT, "me");
                    messageList.add(msg);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(messageList.size());
                    editText.setText("");

                    Global_Conn.sendMessage(msg);
                }
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Log.d("message", "Handler receive the message!");
                    synchronized (messageList){
                        messageList.add((Global_Msg) msg.obj);
                        adapter.notifyDataSetChanged();
                        listView.setSelection(messageList.size());
                    }
                }
            }
        };

        Intent intent = getIntent();
        String welcome = intent.getStringExtra("welcome");
        Toast.makeText(this, welcome, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(){
        super.onStart();
        //Global_Conn.startListener(handler);
        Global_Conn.Listen(handler);
    }


}

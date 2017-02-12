package com.example.jiaqi.letschat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_Login extends AppCompatActivity {
    static final int SUCCESS = 0;
    static final int FAILURE = -1;
    EditText userName = null;
    Button logIn = null;
    String IP = null;
    Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login);
        userName = (EditText) findViewById(R.id.username);
        logIn = (Button) findViewById(R.id.logIn);

        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        IP = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));


        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString().trim();
                if (name.length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
                    builder.setTitle("Ooops");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setMessage("Username cannot be empty!");
                    builder.create().show();
                } else {
                    Global_User Me = Global_User.getInstance();
                    Me.set(name, IP);
                    final Message message = handler.obtainMessage();
                    Global_Conn.Login(new Interface_LoginCallBack() {
                        @Override
                        public void onError(Exception e) {
                            message.what = FAILURE;
                            handler.sendMessage(message);
                            Log.d("Connection", "Connection Failed");
                        }

                        @Override
                        public void onFinish(String welcome) {
                            message.what = SUCCESS;
                            message.obj = welcome;
                            handler.sendMessage(message);
                            Log.d("Connection", "Connection success");
                        }
                    });
                }
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == SUCCESS) {
                    Toast.makeText(Activity_Login.this, "Login Suuces!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("welcome", (String) msg.obj);
                    intent.setClass(getApplicationContext(), Activity_Main.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Activity_Login.this, "Login Fail!", Toast.LENGTH_SHORT).show();
                }
            }

        };

    }
}

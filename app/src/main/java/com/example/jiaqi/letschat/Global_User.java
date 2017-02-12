package com.example.jiaqi.letschat;

import java.util.HashSet;

/**
 * Created by LG on 2017/2/4.
 */
public class Global_User {
    private String IP   = null;
    private String name = null;
    private static Global_User instance    = new Global_User();

    private Global_User(){};

    public static Global_User getInstance(){
        return instance;
    }

    public String getIP(){
        return instance.IP;
    }

    public String getName(){
        return instance.name;
    }

    public void set(String name, String IP){
        instance.name = name;
        instance.IP   = IP;
    }
}

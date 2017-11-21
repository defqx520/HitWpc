package cn.edu.hit.ftcl.wearablepc.Communication;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 消息实体类
 * Created by hzf on 2017/5/13.
 */

public class Msg extends DataSupport implements Serializable{
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    public static final int CATAGORY_VOICE = 0;
    public static final int CATAGORY_TEXT = 1;
    public static final int CATAGORY_IMAGE = 2;
    public static final int CATAGORY_VIDEO = 3;

    private  int id;
    private int sender;
    private int receiver;
    private String path;//如果是文件，存文件路径；如果是文本，存文本内容
    private long time;//消息产生的时间
    private int type;//发送or接收
    private int catagory;//语音or文字or图片or视频

    //不指定消息属性时，默认为语音消息
    public Msg(String path, int type, long time){
        this.path = path;
        this.type = type;
        this.time = time;
        this.catagory = CATAGORY_VOICE;
    }

    public Msg(String path, int type, long time, int catagory){
        this.path = path;
        this.type = type;
        this.time = time;
        this.catagory = catagory;
    }

    public Msg(int sender, int receiver, String path, long time, int type, int catagory) {
        this.sender = sender;
        this.receiver = receiver;
        this.path = path;
        this.time = time;
        this.type = type;
        this.catagory = catagory;
    }

    public int getId(){
        return this.id;
    }
    public long getTime(){
        return this.time;
    }
    public int getType(){
        return this.type;
    }
    public String getPath(){
        return this.path;
    }
    public int getCatagory(){ return this.catagory;}

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCatagory(int catagory) {
        this.catagory = catagory;
    }

    public void setId(int id) {
        this.id = id;
    }
}

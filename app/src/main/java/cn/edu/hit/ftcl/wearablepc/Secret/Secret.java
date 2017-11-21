package cn.edu.hit.ftcl.wearablepc.Secret;

import org.litepal.crud.DataSupport;

/**
 * 消息列表项：记录与小组成员的最后一条消息
 * Created by hzf on 2017/11/10.
 */

public class Secret extends DataSupport{
    private int id;

    private int user_id;//小组成员id

    private String username;//小组成员用户名

    private String content;//消息内容

    private long time;//消息时间

    public Secret(int user_id, String username, String content, long time) {
        this.user_id = user_id;
        this.username = username;
        this.content = content;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

package cn.edu.hit.ftcl.wearablepc.Network;

import org.litepal.crud.DataSupport;

/**
 * 用户IP地址信息
 * Created by hzf on 2017/11/8.
 */

public class UserIPInfo extends DataSupport {
    public static final int TYPE_COMMON = 1;//普通用户

    private int id;

    private String username;

    private String password;

    private String ip;

    private int port;

    private int type;

    public UserIPInfo(){

    }

    public UserIPInfo(String username, String ip, int port) {
        this.username = username;
        this.password = "";
        this.ip = ip;
        this.port = port;
        this.type = TYPE_COMMON;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

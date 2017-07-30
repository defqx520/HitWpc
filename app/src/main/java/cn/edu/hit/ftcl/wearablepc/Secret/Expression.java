package cn.edu.hit.ftcl.wearablepc.Secret;

import org.litepal.crud.DataSupport;

/**
 * Created by hzf on 2017/7/29.
 */
public class Expression extends DataSupport{
    private int id;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

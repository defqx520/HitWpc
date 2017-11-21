package cn.edu.hit.ftcl.wearablepc.Image;

/**
 * Created by hzf on 2017/11/21.
 */

public class Image {
    private int id;

    private long time;

    private String filepath;

    public Image(long time, String filepath) {
        this.time = time;
        this.filepath = filepath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}

package cn.edu.hit.ftcl.wearablepc.Communication;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 参数
 * Created by hzf on 2017/5/22.
 */

public class Parameter extends DataSupport implements Serializable {

    private  int id;
    private String name;
    private String value;

    public Parameter(){

    }

    public Parameter(String name, String value){
        this.name = name;
        this.value = value;
    }

    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){ this.name = name;}
    public String getValue(){
        return this.value;
    }
    public void setValue(String value){ this.value = value;}
}

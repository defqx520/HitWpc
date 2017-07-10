package cn.edu.hit.ftcl.wearablepc.WifiCamera.FTPUtil;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by HFZ on 2017/6/6.
 */

/**
 * 用于获取手机的文件夹及文件的工具类，如果权限允许，可以获取手机上任意路径的文件列表
 * GetFilesUtils使用的是懒汉式单例模式，线程安全
 */
public class GetFilesUtils {
    private static final String TAG = "GetFilesUtils: ";

    public static final String FILE_TYPE_FOLDER="wFl2d";

    public static final String FILE_INFO_NAME="fName";
    public static final String FILE_INFO_ISFOLDER="fIsDir";
    public static final String FILE_INFO_TYPE="fFileType";
    public static final String FILE_INFO_NUM_SONDIRS="fSonDirs";
    public static final String FILE_INFO_NUM_SONFILES="fSonFiles";
    public static final String FILE_INFO_PATH="fPath";

    private static GetFilesUtils gfu;

    private GetFilesUtils(){

    }

    /**
     * 获取GetFilesUtils实例
     * @return GetFilesUtils
     **/
    public static synchronized GetFilesUtils getInstance(){
        if(gfu==null){
            gfu=new GetFilesUtils();
        }
        return gfu;
    }

    /**
     * 获取文件path文件夹下的文件列表
     * @see #getSonNode(String)
     * @param path 手机上的文件夹
     * @return path文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     *         FILE_INFO_NAME : String 文件名称 <br />
     *         FILE_INFO_ISFOLDER: boolean 是否为文件夹   <br />
     *         FILE_INFO_TYPE: string 文件的后缀 <br />
     *         FILE_INFO_NUM_SONDIRS : int 子文件夹个数   <br />
     *         FILE_INFO_NUM_SONFILES: int 子文件个数    <br />
     *         FILE_INFO_PATH : String 文件的绝对路径  <br />
     **/
    public List<Map<String, Object>> getSonNode(File path){
        if(path.isDirectory()){
            List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
            File[] files=path.listFiles();
            if(files!=null){
                for(int i=0;i<files.length;i++){
                    Map<String, Object> fileInfo=new HashMap<String, Object>();
                    fileInfo.put(FILE_INFO_NAME, files[i].getName());
                    if(files[i].isDirectory()){
                        fileInfo.put(FILE_INFO_ISFOLDER, true);
                        File[] bFiles=files[i].listFiles();
                        if(bFiles==null){
                            fileInfo.put(FILE_INFO_NUM_SONDIRS, 0);
                            fileInfo.put(FILE_INFO_NUM_SONFILES, 0);
                        }else{
                            int getNumOfDir=0;
                            for(int j=0;j<bFiles.length;j++){
                                if(bFiles[j].isDirectory()){
                                    getNumOfDir++;
                                }
                            }
                            fileInfo.put(FILE_INFO_NUM_SONDIRS, getNumOfDir);
                            fileInfo.put(FILE_INFO_NUM_SONFILES, bFiles.length-getNumOfDir);
                        }
                        fileInfo.put(FILE_INFO_TYPE, FILE_TYPE_FOLDER);
                    }else{
                        fileInfo.put(FILE_INFO_ISFOLDER, false);
                        fileInfo.put(FILE_INFO_NUM_SONDIRS, 0);
                        fileInfo.put(FILE_INFO_NUM_SONFILES, 0);
                        fileInfo.put(FILE_INFO_TYPE, getFileType(files[i].getName()));
                    }
                    fileInfo.put(FILE_INFO_PATH, files[i].getAbsoluteFile());
                    list.add(fileInfo);
                }
                return list;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    /**
     * 获取文件pathStr文件夹下的文件列表
     * @see #getSonNode(File)
     * @param pathStr 手机上的文件夹的绝对路径
     * @return pathStr文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     *         FILE_INFO_NAME : String 文件名称 <br />
     *         FILE_INFO_ISFOLDER: boolean 是否为文件夹   <br />
     *         FILE_INFO_TYPE: string 文件的后缀 <br />
     *         FILE_INFO_NUM_SONDIRS : int 子文件夹个数   <br />
     *         FILE_INFO_NUM_SONFILES: int 子文件个数    <br />
     *         FILE_INFO_PATH : String 文件的绝对路径  <br />
     **/
    public List<Map<String, Object>> getSonNode(String pathStr){
        File path=new File(pathStr);
        return getSonNode(path);
    }

    /**
     * 获取文件path文件或文件夹的兄弟节点文件列表
     * @see #getBrotherNode(String)
     * @param path 手机上的文件夹
     * @return path文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     *         FILE_INFO_NAME : String 文件名称 <br />
     *         FILE_INFO_ISFOLDER: boolean 是否为文件夹   <br />
     *         FILE_INFO_TYPE: string 文件的后缀 <br />
     *         FILE_INFO_NUM_SONDIRS : int 子文件夹个数   <br />
     *         FILE_INFO_NUM_SONFILES: int 子文件个数    <br />
     *         FILE_INFO_PATH : String 文件的绝对路径  <br />
     **/
    public List<Map<String, Object>> getBrotherNode(File path){
        if(path.getParentFile()!=null){
            return getSonNode(path.getParentFile());
        }else{
            return null;
        }
    }
    /**
     * 获取文件path文件或文件夹的兄弟节点文件列表
     * @see #getBrotherNode(File)
     * path 手机上的文件夹
     * @return path文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     *         FILE_INFO_NAME : String 文件名称 <br />
     *         FILE_INFO_ISFOLDER: boolean 是否为文件夹   <br />
     *         FILE_INFO_TYPE: string 文件的后缀 <br />
     *         FILE_INFO_NUM_SONDIRS : int 子文件夹个数   <br />
     *         FILE_INFO_NUM_SONFILES: int 子文件个数    <br />
     *         FILE_INFO_PATH : String 文件的绝对路径  <br />
     **/
    public List<Map<String, Object>> getBrotherNode(String pathStr){
        File path=new File(pathStr);
        return getBrotherNode(path);
    }

    /**
     * 获取文件或文件夹的父路径
     * File path文件或者文件夹
     * @return String path的父路径
     **/
    public String getParentPath(File path){
        if(path.getParentFile()==null){
            return null;
        }else{
            return path.getParent();
        }
    }
    /**
     * 获取文件或文件的父路径
     *String pathStr文件或者文件夹路径
     * @return String pathStr的父路径
     **/
    public String getParentPath(String pathStr){
        File path=new File(pathStr);
        if(path.getParentFile()==null){
            return null;
        }else{
            return path.getParent();
        }
    }

    /**
     * 获取sd卡的绝对路径
     * @return String 如果sd卡存在，返回sd卡的绝对路径，否则返回null
     **/
    public String getSDPath(){
        String sdcard= Environment.getExternalStorageState();
        if(sdcard.equals(Environment.MEDIA_MOUNTED)){
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }else{
            return null;
        }
    }

    /**
     * 获取一个基本的路径，一般应用创建存放应用数据可以用到
     * @return String 如果SD卡存在，返回SD卡的绝对路径，如果SD卡不存在，返回Android数据目录的绝对路径
     **/
    public String getBasePath(){
        String basePath=getSDPath();
        if(basePath==null){
            return Environment.getDataDirectory().getAbsolutePath();
        }else{
            return basePath;
        }
    }

    /**
     * 获取文件path的大小
     * @return String path的大小
     **/
    public String getFileSize(File path) throws IOException {
        if(path.exists()){
            DecimalFormat df = new DecimalFormat("#.00");
            String sizeStr="";
            FileInputStream fis=new FileInputStream(path);
            long size=fis.available();
            fis.close();
            if(size<1024){
                sizeStr=size+"B";
            }else if(size<1048576){
                sizeStr=df.format(size/(double)1024)+"KB";
            }else if(size<1073741824){
                sizeStr=df.format(size/(double)1048576)+"MB";
            }else{
                sizeStr=df.format(size/(double)1073741824)+"GB";
            }
            return sizeStr;
        }else{
            return null;
        }
    }

    /**
     * 获取文件fpath的大小
     * @return String path的大小
     **/
    public String getFileSize(String fpath){
        File path=new File(fpath);
        if(path.exists()){
            DecimalFormat df = new DecimalFormat("#.00");
            String sizeStr="";
            long size=0;
            try {
                FileInputStream fis = new FileInputStream(path);
                size=fis.available();
                fis.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "未知大小";
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "未知大小";
            }
            if(size<1024){
                sizeStr=size+"B";
            }else if(size<1048576){
                sizeStr=df.format(size/(double)1024)+"KB";
            }else if(size<1073741824){
                sizeStr=df.format(size/(double)1048576)+"MB";
            }else{
                sizeStr=df.format(size/(double)1073741824)+"GB";
            }
            return sizeStr;
        }else{
            return "未知大小";
        }
    }

    /**
     * 根据后缀获取文件fileName的类型
     * @return String 文件的类型
     **/
    public String getFileType(String fileName){
        if(fileName!=""&&fileName.length()>3){
            int dot=fileName.lastIndexOf(".");
            if(dot>0){
                return fileName.substring(dot+1);
            }else{
                return "";
            }
        }
        return "";
    }

    public Comparator<Map<String, Object>> defaultOrder() {

        final String orderBy0=FILE_INFO_ISFOLDER;
        final String orderBy1=FILE_INFO_TYPE;
        final String orderBy2=FILE_INFO_NAME;

        Comparator<Map<String, Object>> order=new Comparator<Map<String,Object>>() {

            @Override
            public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
                // TODO Auto-generated method stub
                int left0=lhs.get(orderBy0).equals(true)?0:1;
                int right0=rhs.get(orderBy0).equals(true)?0:1;
                if(left0==right0){
                    String left1=lhs.get(orderBy1).toString();
                    String right1=rhs.get(orderBy1).toString();
                    if(left1.compareTo(right1)==0){
                        String left2=lhs.get(orderBy2).toString();
                        String right2=rhs.get(orderBy2).toString();
                        return left2.compareTo(right2);
                    }else{
                        return left1.compareTo(right1);
                    }
                }else{
                    return left0-right0;
                }
            }
        };

        return order;
    }

}
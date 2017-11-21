package cn.edu.hit.ftcl.wearablepc.Security;

import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.edu.hit.ftcl.wearablepc.Communication.LogUtil;
import cn.edu.hit.ftcl.wearablepc.Network.UserIPInfo;

/**
 * 加密工具类
 * Created by hzf on 2017/7/29.
 */

public class EncryptionUtil {
    private static final String TAG = EncryptionUtil.class.getSimpleName();

    /**
     * 验证身份
     * @param username
     * @param password
     */
    public static boolean validateIdentity(String username, String password){
        if(username.isEmpty() || password.isEmpty()){
            return false;
        }
        String passwordMd5 = md5(password);
        UserIPInfo userIPInfo = DataSupport.where("username = ?", username).findFirst(UserIPInfo.class);
        if(userIPInfo == null){
            if(DataSupport.where("type = ?", String.valueOf(UserIPInfo.TYPE_SELF)).findFirst(UserIPInfo.class) == null) {
                //首次登录，创建账号
                UserIPInfo addUser = new UserIPInfo(username, passwordMd5);
                addUser.save();
            }else{
                return false;
            }
        }else{
            LogUtil.d(TAG, "pwd in db:" + userIPInfo.getPassword());
            LogUtil.d(TAG, "pwd:" + passwordMd5);
            if(userIPInfo.getPassword().equals(passwordMd5)){
                return true;
            }else{
                return false;
            }
        }
        return true;
    }

    //md5加密
    public static String md5(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException",e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}

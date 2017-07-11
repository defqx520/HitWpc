package cn.edu.hit.ftcl.wearablepc.Communication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.MyApplication;
import cn.edu.hit.ftcl.wearablepc.R;

/**
 * 适配器类
 * Created by hzf on 2017/5/13.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    private List<Msg> mMsgList;

    private Context mContext;

    public MsgAdapter(List<Msg> msgList){
        mMsgList = msgList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.communication_item_voice, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.leftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("MagAdapter", "left onClick() has executed...");
                int position = holder.getAdapterPosition();
                switch (mMsgList.get(position).getCatagory()) {
                    case Msg.CATAGORY_VOICE:
                        Toast.makeText(MyApplication.getContext(), "开始播放录音", Toast.LENGTH_SHORT).show();
                        MediaPlayerManager.playSound(mMsgList.get(position).getPath(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {

                            }
                        });
                        break;
                    default:
                }
            }
        });
        holder.rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MagAdapter", "right onClick() has executed...");
                final int position = holder.getAdapterPosition();
                switch (mMsgList.get(position).getCatagory()) {
                    case Msg.CATAGORY_VOICE:
                        Toast.makeText(MyApplication.getContext(), "开始播放录音", Toast.LENGTH_SHORT).show();
                        MediaPlayerManager.playSound(mMsgList.get(position).getPath(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {

                            }
                        });
                        break;
                    case Msg.CATAGORY_IMAGE:
                        //调用系统自带的播放器
                        File outputImage = new File(mMsgList.get(position).getPath());
                        Uri imageUri;
                        if(Build.VERSION.SDK_INT >= 24){
                            imageUri = FileProvider.getUriForFile(MyApplication.getContext(), "com.hitwearable.fileprovider", outputImage);
                        }else {
                            imageUri = Uri.fromFile(outputImage);
                        }
                        Intent imageIntent = new Intent(Intent.ACTION_VIEW);
                        imageIntent.setDataAndType(imageUri, "image/jpeg");
                        mContext.startActivity(imageIntent);
                        break;
                    case Msg.CATAGORY_VIDEO:
                        //调用系统自带的播放器
                        LogUtil.d("MsgAdapter", mMsgList.get(position).getPath());
                        File outputVideo = new File(mMsgList.get(position).getPath());
                        Uri videoUri;
                        if(Build.VERSION.SDK_INT >= 24){
                            videoUri = FileProvider.getUriForFile(MyApplication.getContext(), "com.hitwearable.fileprovider", outputVideo);
                        }else {
                            videoUri = Uri.fromFile(outputVideo);
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(videoUri, "video/mp4");
                        mContext.startActivity(intent);
                    default:
                }
            }
        });
        //长按删除
        holder.leftLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                int msgId = mMsgList.get(position).getId();
                DataSupport.delete(Msg.class, msgId);
                Toast.makeText(MyApplication.getContext(), "消息已删除", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        holder.rightLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                int msgId = mMsgList.get(position).getId();
                DataSupport.delete(Msg.class, msgId);
                Toast.makeText(MyApplication.getContext(), "消息已删除", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if(msg.getType() == Msg.TYPE_RECEIVED){
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftRecoderTime.setText(new SimpleDateFormat("MM-dd HH:mm").format(msg.getTime()));
            holder.leftLayout.setTag(position);
            //不同属性的消息显示不同
            switch (msg.getCatagory()) {
                case Msg.CATAGORY_VOICE:
                    holder.voiceLeft.setVisibility(View.VISIBLE);
                    holder.textLeft.setVisibility(View.GONE);
                    break;
                case Msg.CATAGORY_TEXT:
                    holder.voiceLeft.setVisibility(View.GONE);
                    holder.textLeft.setVisibility(View.VISIBLE);
                    holder.textLeft.setText(msg.getPath());
                    break;
                default:
            }
        }
        else if(msg.getType() == Msg.TYPE_SENT){
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightRecoderTime.setText(new SimpleDateFormat("MM-dd HH:mm").format(msg.getTime()));
            holder.rightLayout.setTag(position);
            //不同属性的消息显示不同
            switch (msg.getCatagory()) {
                case Msg.CATAGORY_VOICE:
                    holder.voiceRight.setVisibility(View.VISIBLE);
                    holder.textRight.setVisibility(View.GONE);
                    holder.imageRight.setVisibility(View.GONE);
                    break;
                case Msg.CATAGORY_TEXT:
                    holder.voiceRight.setVisibility(View.GONE);
                    holder.textRight.setVisibility(View.VISIBLE);
                    holder.imageRight.setVisibility(View.GONE);
                    holder.textRight.setText(msg.getPath());
                    break;
                case Msg.CATAGORY_IMAGE:
                    holder.voiceRight.setVisibility(View.GONE);
                    holder.textRight.setVisibility(View.GONE);
                    holder.imageRight.setVisibility(View.VISIBLE);
                    //图片处理
                    File outputImage = new File(msg.getPath());
                    Uri imageUri;
                    if(Build.VERSION.SDK_INT >= 24){
                        imageUri = FileProvider.getUriForFile(MyApplication.getContext(), "com.hitwearable.fileprovider", outputImage);
                    }else {
                        imageUri = Uri.fromFile(outputImage);
                    }
                    Glide.with(mContext).load(imageUri).into(holder.imageRight);
                    //设置图片
//                    Bitmap bitmap = null;
//                    try {
//                        bitmap = BitmapFactory.decodeStream(MyApplication.getContext().getContentResolver().openInputStream(imageUri));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    if(bitmap != null)
//                        holder.imageRight.setImageBitmap(comp(bitmap));
                case Msg.CATAGORY_VIDEO:
                    holder.voiceRight.setVisibility(View.GONE);
                    holder.textRight.setVisibility(View.GONE);
                    holder.imageRight.setVisibility(View.VISIBLE);
                    //图片处理
                    File outputVideo = new File(msg.getPath());
                    Uri videoUri;
                    if(Build.VERSION.SDK_INT >= 24){
                        videoUri = FileProvider.getUriForFile(MyApplication.getContext(), "com.hitwearable.fileprovider", outputVideo);
                    }else {
                        videoUri = Uri.fromFile(outputVideo);
                    }
                    Glide.with(mContext).load(videoUri).into(holder.imageRight);//依然加载成图片，点击后播放视频
                    break;
                default:
            }
        }
    }


    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftRecoderTime;
        TextView rightRecoderTime;
        View voiceLeft;
        View voiceRight;
        TextView textLeft;
        TextView textRight;
        ImageView imageRight;
        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout = (LinearLayout) itemView.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout) itemView.findViewById(R.id.right_layout);
            leftRecoderTime = (TextView)itemView.findViewById(R.id.id_recoder_time_left);
            rightRecoderTime = (TextView)itemView.findViewById(R.id.id_recoder_time_right);
            voiceLeft = (View)itemView.findViewById(R.id.id_voice_left);
            voiceRight = (View)itemView.findViewById(R.id.id_voice_right);
            textLeft = (TextView)itemView.findViewById(R.id.id_text_left);
            textRight = (TextView)itemView.findViewById(R.id.id_text_right);
            imageRight = (ImageView)itemView.findViewById(R.id.id_image_right);
        }
    }

    //按比例压缩
    private Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 75, baos);//这里压缩75%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    //质量压缩
    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}

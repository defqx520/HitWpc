package cn.edu.hit.ftcl.wearablepc.Image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.MyApplication;
import cn.edu.hit.ftcl.wearablepc.R;

/**
 * Created by hzf on 2017/11/21.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    List<Image> imageList;

    Context mContext;

    public ImageAdapter(List<Image> imageList){
        this.imageList = imageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item_image, parent, false);
        final ViewHolder holder =  new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Image image = imageList.get(position);
                //打开大图
                File outputImage = new File(image.getFilepath());
                Uri imageUri;
                if(Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(MyApplication.getContext(), "com.hitwearable.fileprovider", outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent imageIntent = new Intent(Intent.ACTION_VIEW);
                imageIntent.setDataAndType(imageUri, "image/jpeg");
                mContext.startActivity(imageIntent);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                Image image = imageList.get(position);
                if(!holder.time.getText().toString().equals("选中删除")) {
                    //delete
                    holder.time.setText("选中删除");
                    ImageManageActivity.deleteImageList.add(image.getFilepath());
                }else{
                    //undelete
                    holder.time.setText(new SimpleDateFormat("yyyy-MM-dd").format(image.getTime()));
                    ImageManageActivity.deleteImageList.remove(image.getFilepath());
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Image image = imageList.get(position);
        holder.time.setText(new SimpleDateFormat("yyyy-MM-dd").format(image.getTime()));
        Glide.with(mContext).load(image.getFilepath()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView image;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            image = (ImageView)itemView.findViewById(R.id.id_image);
            time = (TextView)itemView.findViewById(R.id.id_image_time);
        }
    }
}

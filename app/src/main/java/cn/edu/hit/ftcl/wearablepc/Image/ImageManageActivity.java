package cn.edu.hit.ftcl.wearablepc.Image;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.R;

public class ImageManageActivity extends AppCompatActivity {

    private List<Image> imageList = new ArrayList<>();

    private ImageAdapter adapter;

    public static List<String> deleteImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity_image_manage);

        //设置ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_image_manage);
        setSupportActionBar(toolbar);
        //卡片式布局
        initImageList();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImageAdapter(imageList);
        recyclerView.setAdapter(adapter);
        //悬浮按钮
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认框
                AlertDialog.Builder dialog = new AlertDialog.Builder(ImageManageActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("确定删除吗？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(String filepath : deleteImageList){
                            new File(filepath).delete();
                        }
                        deleteImageList.clear();

                        initImageList();
                        adapter.notifyDataSetChanged();

                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImageList.clear();

                        initImageList();
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.show();
            }
        });
    }

    //初始化imageList
    private void initImageList(){
        imageList.clear();

        File dirImage = new File(Environment.getExternalStorageDirectory() + "/HitWearable/image");
        if (!dirImage.exists()) {
            dirImage.mkdirs();//文件夹不存在，则创建文件夹
        }
        File dirVideo = new File(Environment.getExternalStorageDirectory() + "/HitWearable/video");
        if(!dirVideo.exists()){
            dirVideo.mkdirs();
        }

        File[] filesImage = dirImage.listFiles();
        File[] filesVideo = dirVideo.listFiles();
        if(filesImage != null){
            for(int i = 0; i < filesImage.length; i++){
                imageList.add(new Image(filesImage[i].lastModified(), filesImage[i].toString()));
            }
        }
        if(filesVideo != null){
            for(int i = 0; i < filesVideo.length; i++){
                imageList.add(new Image(filesVideo[i].lastModified(), filesVideo[i].toString()));
            }
        }
    }
}

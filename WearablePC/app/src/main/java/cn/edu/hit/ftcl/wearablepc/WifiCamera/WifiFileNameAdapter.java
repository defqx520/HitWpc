package cn.edu.hit.ftcl.wearablepc.WifiCamera;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.R;

/**
 * Created by HFZ on 2017/6/5.
 */

public class WifiFileNameAdapter extends BaseAdapter {
    private List<String> fileNameList;
    private List<Calendar> fileDateList;
    private LayoutInflater inflater;

    public WifiFileNameAdapter(Activity parent){
        fileNameList = new ArrayList<String>();
        fileDateList = new ArrayList<Calendar>();
        inflater = parent.getLayoutInflater();
    }

    public void addFile(String fileName, Calendar calendar){
        if (!fileNameList.contains(fileName)){
            fileNameList.add(fileName);
            fileDateList.add(calendar);
        }
    }

    public void clearList(){
        fileNameList.clear();
        fileDateList.clear();
    }

    @Override
    public Object getItem(int position) {
        return fileNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FieldReferences fieldReferences;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.wifi_file_name_item,null);
            fieldReferences = new FieldReferences();
            fieldReferences.wifiFileName = (TextView)convertView.findViewById(R.id.wifi_file_name);
            fieldReferences.wifiFileDate = (TextView)convertView.findViewById(R.id.wifi_file_date);
            convertView.setTag(fieldReferences);
        }else {
            fieldReferences = (FieldReferences)convertView.getTag();
        }

        String fileName = fileNameList.get(position);
        Calendar calendar = fileDateList.get(position);
        Date date = calendar.getTime();
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String dateString = formatter.format(date);

        fieldReferences.wifiFileName.setText(fileName);
        fieldReferences.wifiFileDate.setText(dateString);
        return convertView;
    }

    @Override
    public int getCount() {
        return fileNameList.size();
    }

    private class FieldReferences{
        TextView wifiFileName;
        TextView wifiFileDate;
    }
}


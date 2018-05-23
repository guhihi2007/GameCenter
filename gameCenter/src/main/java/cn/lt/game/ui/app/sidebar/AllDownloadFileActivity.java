package cn.lt.game.ui.app.sidebar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.model.GameBaseDetail;

/**
 * Created by 林俊生 on 2016/1/6.
 */
public class AllDownloadFileActivity extends BaseActivity {

    private ListView lv_allDownFileListView;
    private List<GameBaseDetail> fileList;

    @Override
    public void setPageAlias() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_down_file);
        initVIew();
        setData();
    }

    private void initVIew() {
        lv_allDownFileListView = (ListView) findViewById(R.id.lv_allDownFileListView);

    }

    private void setData() {
        fileList = FileDownloaders.getAllDownloadFileInfo();

        AllDownFileAdapter adapter = new AllDownFileAdapter();
        lv_allDownFileListView.setAdapter(adapter);
    }


    private class AllDownFileAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return fileList.size() == 0 ? 1 : fileList.size();
        }

        @Override
        public GameBaseDetail getItem(int position) {
            return fileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if(convertView == null) {
                convertView = LayoutInflater.from(AllDownloadFileActivity.this).inflate(R.layout.item_all_down_file, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            if(fileList.size() == 0) {
                vh.tv_downFileName.setText("No File~~");

            } else {
                GameBaseDetail file = fileList.get(position);
                vh.tv_downFileName.setText("FileName = " + file.getName());
                vh.tv_downFileInfo.setText("pkgName = " + file.getPkgName() + "\n" +
                        "md5 = " + file.getMd5() + "\n" +
                        "pkgSize = " + file.getPkgSize() + "\n" +
                        "verName = " + file.getVersion() + "\n" +
                        "verCode = " + file.getVersionCode() + "\n" +
                        "downUrl = " + file.getDownUrl() + "\n" +
                        "State = " + file.getState() + "\n" +
                        "PrevState = " + file.getPrevState() + "\n" +
                        "DownPath = " + file.getDownPath() + "\n" +
                        "DownLength = " + file.getDownLength() + "\n" +
                        "FileTotalLength = " + file.getFileTotalLength()
                );


            }




            return convertView;
        }

        class ViewHolder {
            TextView tv_downFileName;
            TextView tv_downFileInfo;

            public ViewHolder(View v) {
                tv_downFileName = (TextView) v.findViewById(R.id.tv_downFileName);
                tv_downFileInfo = (TextView) v.findViewById(R.id.tv_downFileInfo);

            }
        }
    }
}

package com.qugengting.flowlayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qugengting.view.flowlayout.FlowLayout;
import com.qugengting.view.flowlayout.TagAdapter;
import com.qugengting.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private String[] mVals = new String[]{"a@qq.com", "b@qq.com", "c@126.com", "d@hotmail.com", "e@foxmail.com"};
    private static final String KEY_DATA_STORE = "key_data";
    private LayoutInflater mInflater;
    private ImageButton mImageButtonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mInflater = LayoutInflater.from(this);
        TagFlowLayout flowLayout = (TagFlowLayout) findViewById(R.id.id_flowlayout);
        flowLayout.setAttachLabel(true);//设置是否需要添加标签,默认添加
        flowLayout.setAdapter(adapter);
        mImageButtonAdd = (ImageButton) findViewById(R.id.ib_add);
        mImageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add("addI@sina.com");
                adapter.add("addII@sina.com");
                adapter.add("addIII@sina.com");
                adapter.notifyDataChanged();
            }
        });
    }

    private TagAdapter adapter = new TagAdapter(mVals) {
        @Override
        public View getView(FlowLayout parent, int position, String s) {
            TextView tv = (TextView) mInflater.inflate(R.layout.tv_item, parent, false);
            tv.setText(s);
            return tv;
        }

        @Override
        public View getLabelView(FlowLayout parent) {
            //如果设置flowLayout.setAttachLabel(false);该标签将不显示
            TextView tv = (TextView) mInflater.inflate(R.layout.tv_label, parent, false);
            tv.setText("收件人：");
            return tv;
        }

        @Override
        public View getInputView(FlowLayout parent) {
            return mInflater.inflate(R.layout.edt, parent, false);
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(KEY_DATA_STORE, (ArrayList<String>) adapter.getDatas());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<String> data = savedInstanceState.getStringArrayList(KEY_DATA_STORE);
        adapter.resetData(data);
    }
}

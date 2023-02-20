package com.example.dliangwang.snapshotdetection;

import android.support.v7.app.AppCompatActivity;

import com.example.dliangwang.snapshotdetection.CallBack.SnapShotTakeCallBack;
import com.example.dliangwang.snapshotdetection.Utils.FileObserverUtils;

/**
 * Created by dliang.wang on 2017/4/12.
 * /storage/emulated/0/DCIM/Screenshots/Screenshot_2023-02-20-18-52-12-953_com.example.dliangwang.snapshotdetection.jpg
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();

        FileObserverUtils.setSnapShotCallBack(new SnapShotTakeCallBack(this));
        FileObserverUtils.startSnapshotWatching();
    }

    @Override
    protected void onPause() {
        super.onPause();

        FileObserverUtils.stopSnapshotWatching();
    }
}

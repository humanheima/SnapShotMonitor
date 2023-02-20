package com.example.dliangwang.snapshotdetection;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.dliangwang.snapshotdetection.CustomView.LineInfo;
import com.example.dliangwang.snapshotdetection.CustomView.PaintableImageView;
import com.example.dliangwang.snapshotdetection.Utils.BitmapUtils;

import static com.example.dliangwang.snapshotdetection.Utils.FileObserverUtils.SNAP_SHOT_FOLDER_PATH;
import static com.example.dliangwang.snapshotdetection.CallBack.SnapShotTakeCallBack.SNAP_SHOT_PATH_KEY;

/**
 * Created by dliang.wang on 2017/4/12.
 */

public class SnapShotEditActivity extends AppCompatActivity {

    private static final String TAG = "SnapShotEditActivity";

    private String snapShotPath;
    private PaintableImageView imageView;

    private Button withDrawBtn;
    private Button normalLineBtn;
    private Button mosaicLineBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snap_shot_layout);
        withDrawBtn = (Button) findViewById(R.id.withdraw_btn);
        normalLineBtn = (Button) findViewById(R.id.normal_line_btn);
        mosaicLineBtn = (Button) findViewById(R.id.mosai_btn);

        withDrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.withDrawLastLine();
            }
        });

        normalLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setLineType(LineInfo.LineType.NormalLine);
            }
        });

        mosaicLineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setLineType(LineInfo.LineType.MosaicLine);
            }
        });

        imageView = (PaintableImageView) findViewById(R.id.image_view);
        snapShotPath = getIntent().getStringExtra(SNAP_SHOT_PATH_KEY);

        final ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                autoFitImageView();

                ViewTreeObserver vto = imageView.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    vto.removeOnGlobalLayoutListener(this);
                } else {
                    vto.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    /**
     * 根据ImageView实际高度对图片进行等比例压缩，并调整IamgeView尺寸和Bitmap尺寸一致
     */
    private void autoFitImageView() {
        //int imageViewHeight = imageView.getHeight();

        int imageViewHeight = ScreenUtil.getScreenHeight(this);
        Bitmap compressedBitmap = BitmapUtils
                .getCompressedBitmap(SNAP_SHOT_FOLDER_PATH + snapShotPath, imageViewHeight);

        if (null != compressedBitmap) {
            int width = compressedBitmap.getWidth();
            int height = compressedBitmap.getHeight();
            Bitmap qrcode = generateQRCode();
            if (qrcode != null) {
                int qrcodeHeight = qrcode.getHeight();
                Log.i(TAG, "autoFitImageView: qrcodeHeight = " + qrcodeHeight);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height + qrcodeHeight);

                Bitmap bitmap = Bitmap.createBitmap(width, height + qrcodeHeight, Config.RGB_565);
                Canvas canvas = new Canvas();
                canvas.setBitmap(bitmap);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(compressedBitmap, 0, 0, null);
                canvas.drawBitmap(qrcode, 0, height, null);

                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                canvas.drawBitmap(logo, 144, height, null);

                layoutParams.gravity = Gravity.CENTER;
                imageView.setLayoutParams(layoutParams);
                imageView.requestLayout();
                imageView.setImageBitmap(bitmap);
                //bitmap.recycle();
            }
        }
    }

    private Bitmap generateQRCode() {
        int width = 144;
        String filePath = getFilesDir().toString() + "qrcode" + System.currentTimeMillis();
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        QRCodeUtil.createQRImage("http://www.baidu.com", width, width, logo, filePath,
                Color.parseColor("#4D4D4D"),
                Color.parseColor("#FFFFFF"), 16);

        Bitmap qrcode = BitmapFactory.decodeFile(filePath);

        return qrcode;
    }
}

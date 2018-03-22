package com.lambda.canvasassignment.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lambda.canvasassignment.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Dell on 3/21/2018.
 */

public class HomeActivity extends Activity implements View.OnClickListener{

    LinearLayout linearLayout2;
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private ArrayList<Path> paths = new ArrayList<Path>();
    DrawingPanel dp;

    Button btn_undo, btn_redo, btn_save, btn_load, btn_clear, btn_circle, btn_rectangle;
    ImageView img;
    String path;
    String datenTime, dte, tme, hh, mm, ss;
    private String outputFile = null;

    Bitmap bitmap = null;


    /*private int mPivotX = 0;
    private int mPivotY = 0;*/
    private int radius = 60;

    boolean drwCircle = false, drwRect = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        dp = new DrawingPanel(this);
        linearLayout2.addView(dp);



        img = (ImageView) findViewById(R.id.img1);

        btn_undo = (Button) findViewById(R.id.btn_undo);
        btn_undo.setOnClickListener(this);

        btn_redo = (Button) findViewById(R.id.btn_redo);
        btn_redo.setOnClickListener(this);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);


        btn_load = (Button) findViewById(R.id.btn_load);
        btn_load.setOnClickListener(this);

        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);


        btn_circle = (Button) findViewById(R.id.btn_drwCircle);
        btn_circle.setOnClickListener(this);

        btn_rectangle = (Button) findViewById(R.id.btn_drwRectangle);
        btn_rectangle.setOnClickListener(this);



        String TAG = "Permsission : ";
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                //return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                //return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("##Lambda Permission","Permission is granted");
            //return true;
        }





    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        String TAG = "Permsission : ";
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            //Define the path you want
            File mFolder = new File(Environment.getExternalStorageDirectory(), "Folder_Name");
            if (!mFolder.exists()) {
                boolean b =  mFolder.mkdirs();

            }
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.btn_undo:
                if (paths.size() > 0) {
                    undonePaths.add(paths
                            .remove(paths.size() - 1));
                    dp.invalidate();
                }

                break;


            case R.id.btn_redo:

                if (undonePaths.size()>0) {
                    paths.add(undonePaths.remove(undonePaths.size()-1));
                    dp.invalidate();
                }

                break;

            case R.id.btn_save:


                    btn_load.setVisibility(View.VISIBLE);
                    btn_save.setVisibility(View.GONE);

                    getDate();
                    saveImage();

                break;


            case R.id.btn_load:


                btn_load.setVisibility(View.GONE);
                btn_save.setVisibility(View.VISIBLE);


                img.setVisibility(View.VISIBLE);

                Bitmap getBitmap = BitmapFactory.decodeFile(outputFile);
                img.setImageBitmap(getBitmap);


                Log.v("##Lambda Load : ",""+outputFile);

                break;

            case R.id.btn_clear:


                if (paths != null)
                    paths.clear();
                if (dp != null)
                    dp.invalidate();



                btn_load.setVisibility(View.GONE);
                btn_save.setVisibility(View.VISIBLE);

                img.setVisibility(View.GONE);

                finish();
                startActivity(getIntent());


                break;


            case R.id.btn_drwCircle:

                drwCircle = true;

                Log.v("##Lambda DrawCircle","drwCircle "+drwCircle);

                if (dp != null)
                    dp.invalidate();

                break;

            case R.id.btn_drwRectangle:

                Log.v("##Lambda DrawRecangle","drwRec ");

                drwRect = true;

                if (dp != null)
                    dp.invalidate();

                break;

        }

}






    private void saveImage() {


        try{


        String sep = File.separator; // Use this instead of hardcoding the "/"
        String newFolder = "Lambda";
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File myNewFolder = new File(extStorageDirectory + sep + newFolder);
        myNewFolder.mkdir();
        outputFile = Environment.getExternalStorageDirectory().toString()
                + sep + newFolder + sep + "_"+dte+"_"+hh+"_"+mm+"_"+ss +".png";



        dp.setDrawingCacheEnabled(true);
        bitmap = dp.getDrawingCache();

        FileOutputStream ostream = new FileOutputStream(outputFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);

            ostream.close();
            img.setDrawingCacheEnabled(false);
            Log.v("##Lambda SF : ",""+outputFile);

            Toast.makeText(HomeActivity.this,"Image Path : "+outputFile,Toast.LENGTH_SHORT).show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }


    private void getDate() {
        // TODO Auto-generated method stub
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        datenTime = dateFormatter.format(today);


        String data[] = datenTime.split(" ");
        dte = data[0];
        tme = data[1];
        System.out.println("##### Date : " + dte);
        System.out.println("##### Time : " + tme);


        String time[] = tme.split(":");
        hh = time[0];
        mm = time[1];
        ss = time[2];

        Log.v("##Lambda Date",""+datenTime);

    }



    public class DrawingPanel extends View implements View.OnTouchListener {

        private Canvas mCanvas;
        private Path mPath;
        private Paint mPaint, circlePaint, outercirclePaint;

        // private ArrayList<Path> undonePaths = new ArrayList<Path>();
        private float xleft, xright, xtop, xbottom;

        public DrawingPanel(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);

            this.setOnTouchListener(this);

            circlePaint = new Paint();
            mPaint = new Paint();
            outercirclePaint = new Paint();
            outercirclePaint.setAntiAlias(true);
            circlePaint.setAntiAlias(true);
            mPaint.setAntiAlias(true);
            //mPaint.setColor(0xFFFFFFFF);
            mPaint.setColor(Color.GREEN);

            outercirclePaint.setColor(0x44FFFFFF);
            circlePaint.setColor(0xAADD5522);
            outercirclePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStyle(Paint.Style.FILL);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(6);
            outercirclePaint.setStrokeWidth(6);
            mCanvas = new Canvas();
            mPath = new Path();
            paths.add(mPath);
        }


        public void colorChanged(int color) {
            mPaint.setColor(color);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for (Path p : paths) {
                canvas.drawPath(p, mPaint);
            }

            if(drwCircle){


                canvas.drawColor(Color.WHITE);
                mPaint.setColor(Color.BLUE);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setAntiAlias(true);
                //canvas.drawCircle(mPivotX, mPivotY, radius, mPaint);

                canvas.drawCircle(getWidth()/2, getHeight()/2, 100, mPaint);


            }

            if(drwRect){


                canvas.drawColor(Color.WHITE);
                mPaint.setColor(Color.BLUE);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setAntiAlias(true);
                canvas.drawRect(100, 100, 400, 200, mPaint);

            }




        }


        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 0;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath = new Path();
            paths.add(mPath);
        }




        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // if (x <= cx+circleRadius+5 && x>= cx-circleRadius-5) {
                    // if (y<= cy+circleRadius+5 && cy>= cy-circleRadius-5){
                    // paths.clear();
                    // return true;
                    // }
                    // }
                    touch_start(x, y);
                    invalidate();

                    //mPaint.setXfermode(null);

                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }





}

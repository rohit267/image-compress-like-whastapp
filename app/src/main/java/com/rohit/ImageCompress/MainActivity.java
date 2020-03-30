package com.rohit.ImageCompress;

import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.obsez.android.lib.filechooser.ChooserDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    AlertDialog alertDialog;
    int PICK_IMAGE = 67;
    Button input, output, start;
    public EditText editText;
    String iFolder = null, oFolder = null;
    boolean per = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertDialog = new SpotsDialog.Builder().setContext(this).build();
        alertDialog.setMessage("Compressing...");
        alertDialog.setCancelable(false);
        input = findViewById(R.id.input);
        output = findViewById(R.id.output);
        start = findViewById(R.id.start);
        editText = findViewById(R.id.editText);
        editText.setFocusable(false);
        requestPermission();

        input.setOnClickListener(view -> chooseInputFolder());

        output.setOnClickListener(view -> chooseOutputFolder());

        start.setOnClickListener(view -> {
            try {
                alertDialog.show();
                if (iFolder != null && oFolder != null) {
                    startC();
                }
                else {
                    alertDialog.dismiss();
                    Toast.makeText(this,"Please select input and output folder",Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                alertDialog.dismiss();
                e.printStackTrace();
            }
        });
    }

    private void startC() {
        //alertDialog.show();
        File[] files = new File(iFolder).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".JPG") || files[i].getName().endsWith(".jpeg")
                    || files[i].getName().endsWith(".JPEG") || files[i].getName().endsWith(".png") || files[i].getName().endsWith(".PNG")) {
                compress(files[i].getAbsolutePath());
            }
        }
        alertDialog.dismiss();
    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        per = true;
                    } else {
                        per = false;
                    }
                });
    }

    private void chooseInputFolder() {

        if (per) {
            new ChooserDialog(MainActivity.this)
                    .withFilter(true, false)
                    .withStartFile("/storage/emulated/0/")
                    // to handle the result(s)
                    .withChosenListener(new ChooserDialog.Result() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onChoosePath(String path, File pathFile) {
                            iFolder = path;
//                            File[] files =new File(path).listFiles();
//
//                            for(int i=0;i<files.length;i++){
//                                editText.append(files[i].getName()+"\n");
//                            }
                        }
                    })
                    .build()
                    .show();
        } else {
            requestPermission();
        }

    }

    private void chooseOutputFolder() {

        if (per) {
            new ChooserDialog(MainActivity.this)
                    .withFilter(true, false)
                    .withStartFile("/storage/emulated/0/")
                    // to handle the result(s)
                    .withChosenListener(new ChooserDialog.Result() {
                        @Override
                        public void onChoosePath(String path, File pathFile) {
                            oFolder = path;
                        }
                    })
                    .build()
                    .show();
        } else {
            requestPermission();
        }

    }


    private void compress(String path) {
        ImageCompression imageCompression = new ImageCompression(getApplicationContext(), iFolder, oFolder);
        editText.append(" Compressing : " + new File(path));
        String newpath = imageCompression.compressImage(path);
        if (newpath == null) {
            editText.append(" Failed\n");
        } else {
            editText.append(" Done: " + new File(newpath).length() / 1024 + "kb\n ");
        }
    }
}



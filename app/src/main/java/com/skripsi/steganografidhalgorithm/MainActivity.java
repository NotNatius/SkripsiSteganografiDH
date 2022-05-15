package com.skripsi.steganografidhalgorithm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;
import com.ayush.imagesteganographylibrary.Text.TextEncoding;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TextEncodingCallback, TextDecodingCallback {
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Encode Class";
    private ImageView image;
    private Button encode;
    private Button decode;
    private TextView publicKeyAnda;
    private EditText publicKeyTeman;
    private EditText secretMessage;
    private Button saveImage;

    private TextEncoding textEncoding;
    private TextDecoding textDecoding;
    private ImageSteganography imageSteganography;
    private ProgressDialog save;
    private Uri filepath;

    private Bitmap original_image;
    private Bitmap encoded_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); //activityMain
        image=findViewById(R.id.ivCitraR); //ImageView Tengah
        encode= findViewById(R.id.btEncode); //tombol encode
        decode = findViewById(R.id.btDecode); //Tombol decode
        saveImage = findViewById(R.id.btSaveImage); //Tombol save image

        publicKeyAnda = findViewById(R.id.tvpublicuser1); //public key kita
        publicKeyTeman = findViewById(R.id.etPublic); //public key temen kita/lawan bicara
        secretMessage = findViewById(R.id.etSecretMessage); //secret message/pesan rahasia

        checkAndRequestPermissions();
        //Method Imageview kalau diclick
        image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                ImageChooser();
            }
        });
        encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null) {
                    if (secretMessage.getText() != null) {
                        //ImageSteganography Object instantiation
                        imageSteganography = new ImageSteganography(secretMessage.getText().toString(),
                                publicKeyTeman.getText().toString(), original_image);
                        //TextEncoding object Instantiation
                        textEncoding = new TextEncoding(MainActivity.this, MainActivity.this);
                        //Executing the encoding
                        textEncoding.execute(imageSteganography);
                        Toast.makeText(MainActivity.this, "Encode", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bitmap imgToSave = encoded_image;
                Thread PerformEncoding = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveToInternalStorage(imgToSave);
                    }
                });
                save = new ProgressDialog(MainActivity.this);
                save.setMessage("Saving, Please Wait...");
                save.setTitle("Saving Image");
                save.setIndeterminate(false);
                save.setCancelable(false);
                save.show();
                PerformEncoding.start();
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                secretMessage.setText(null);
                publicKeyAnda.setText(null);
            }
        });

        decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null) {
                    //Making the ImageSteganography object
                    ImageSteganography imageSteganography = new ImageSteganography(publicKeyTeman.getText().toString(),
                            original_image);

                    //Making the TextDecoding object
                    TextDecoding textDecoding = new TextDecoding(MainActivity.this, MainActivity.this);

                    //Execute Task
                    textDecoding.execute(imageSteganography);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filepath = data.getData();
            try {
                original_image = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);

                image.setImageBitmap(original_image);
            } catch (IOException e) {
                Log.d(TAG, "Error : " + e);
            }
        }
    }

    @Override
    public void onStartTextEncoding() {
        Toast.makeText(this, "Encoding...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        if (result != null && result.isEncoded()) {
            encoded_image = result.getEncoded_image();
            image.setImageBitmap(encoded_image);
            Toast.makeText(this, "Encoded", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Tidak Encoded", Toast.LENGTH_SHORT).show();
        }
        if (result != null) {
            if (!result.isDecoded())
                secretMessage.setText("No message found");
            else {
                if (!result.isSecretKeyWrong()) {
                    Toast.makeText(this, "Decoded", Toast.LENGTH_SHORT).show();
                    secretMessage.setText("" + result.getMessage());
                } else {
                    secretMessage.setText("Wrong secret key");
                }
            }
        } else {
            secretMessage.setText("Select Image First");
        }
    }
    private void saveToInternalStorage(Bitmap bitmapImage) {
        OutputStream fOut;
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Encoded" + ".PNG"); // the File to save ,
        try {
            fOut = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void ImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
    private void checkAndRequestPermissions() {
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
        }
    }
}
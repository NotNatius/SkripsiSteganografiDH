package com.skripsi.steganografidhalgorithm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.skripsi.steganografidhalgorithm.KeyExchange.*;

public class MainActivity extends AppCompatActivity implements TextEncodingCallback, TextDecodingCallback {
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Encode Class";
    private ImageView image;
    private TextView publicKeyAnda;
    private EditText publicKeyTeman;
    private EditText secretMessage;

    private TextEncoding textEncoding;
    private TextDecoding textDecoding;
    private ImageSteganography imageSteganography;
    private Uri filepath;

    private Bitmap encoded_image;
    private Bitmap original_image;
    private long[] secretAnda;
    private long[] publikTeman;
    private String commonKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); //activityMain
        image=findViewById(R.id.ivCitraR); //ImageView Tengah
        Button encode = findViewById(R.id.btEncode); //tombol encode
        Button decode = findViewById(R.id.btDecode); //Tombol decode
        Button saveImage = findViewById(R.id.btSaveImage); //Tombol save image
        ImageButton clipboard = findViewById(R.id.btClipboard);//Tombol copy clipboard

        publicKeyAnda = findViewById(R.id.tvpublicuser1); //public key kita
        publicKeyTeman = findViewById(R.id.etPublic); //public key temen kita/lawan bicara
        secretMessage = findViewById(R.id.etSecretMessage); //secret message/pesan rahasia

        checkAndRequestPermissions();

        BigInteger hex1 = GenerateRandom();
        secretAnda = longtoArray(hex1);
        long[] publikAnda = keyExchangeArray(secretAnda);
        String publikDecAnda = String.valueOf(decToHex(publikAnda));
        String publikAnda1 = Long.toHexString(Long.parseLong(publikDecAnda));
        publicKeyAnda.setText(publikAnda1);

        //Method Imageview kalau diclick
        image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                ImageChooser();
            }
        });

        clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Publik Key Anda", publicKeyAnda.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(MainActivity.this, "Publik Key telah dicopy",Toast.LENGTH_SHORT).show();
            }
        });

        encode.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String publikTeman1 = publicKeyTeman.getText().toString();
                publikTeman = hextoDecLast(publikTeman1);
                long[] commonAnda = keyExchangeArrayShare(publikTeman,secretAnda);
                long commonDec = decToHex(commonAnda);
                commonKey = Long.toHexString(commonDec);
                if (filepath != null) {
                    if (secretMessage.getText() != null) {
                        //ImageSteganography Object instantiation
                        imageSteganography = new ImageSteganography(secretMessage.getText().toString() ,
                                commonKey, original_image);
                        //TextEncoding object Instantiation
                        textEncoding = new TextEncoding(MainActivity.this, MainActivity.this);
                        //Executing the encoding
                        textEncoding.execute(imageSteganography);
                    }
                    secretMessage.setText(commonKey);
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
                PerformEncoding.start();
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                secretMessage.setText("");
            }
        });

        decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String publikTeman1 = publicKeyTeman.getText().toString();
                publikTeman = hextoDecLast(publikTeman1);
                long[] commonAnda = keyExchangeArrayShare(publikTeman,secretAnda);
                long commonDec = decToHex(commonAnda);
                commonKey = Long.toHexString(commonDec);
                if (filepath != null) {
                    //Making the ImageSteganography object
                    ImageSteganography imageSteganography = new ImageSteganography(commonKey, original_image);

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
        }
        else if (result != null) {
                if (!result.isDecoded() && !result.isSecretKeyWrong()) {
                    Toast.makeText(this, "Decoded", Toast.LENGTH_SHORT).show();
                    secretMessage.setText(result.getMessage());
                }else{
                    secretMessage.setText(result.getMessage());
                }
            } else {
                Toast.makeText(this, "Select Image First", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "any_picture_name");
        values.put(MediaStore.Images.Media.BUCKET_ID, "test");
        values.put(MediaStore.Images.Media.DESCRIPTION, "test Image taken");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/PNG");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
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
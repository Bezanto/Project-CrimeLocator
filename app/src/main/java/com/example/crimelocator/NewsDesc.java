package com.example.crimelocator;

import static androidx.constraintlayout.widget.Constraints.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class NewsDesc extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref;
    Button helpBtn;
    ImageView coverImage;
    TextView newsTitle, newsDesc, newsDate;
    RecyclerView galleryView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_desc);
        getSupportActionBar().hide();


        Intent dataIntent=getIntent();
        String name=dataIntent.getStringExtra("TITLE");
        String desc = dataIntent.getStringExtra("DESC");
        String date = dataIntent.getStringExtra("DATE");
        String id = dataIntent.getStringExtra("ID");
        String email = dataIntent.getStringExtra("EMAIL");



        newsTitle =findViewById(R.id.newsDescTitle);
        newsDesc = findViewById(R.id.newsDesc);
        newsDate = findViewById(R.id.newsDescDate);
        coverImage =findViewById(R.id.imageDesc);
        helpBtn =findViewById(R.id.helpButton);

        galleryView=findViewById(R.id.galleryView);
        galleryView.setHasFixedSize(true);
        galleryView.setLayoutManager(new LinearLayoutManager(this));

        galleryData[] galleryData=new galleryData[]{
                new galleryData(R.drawable.bg),
                new galleryData(R.drawable.casemateback),
                new galleryData(R.drawable.back),
                new galleryData(R.drawable.demo3),
                new galleryData(R.drawable.crimecon),
        };

        galleryAdapter galleryAdapter =new galleryAdapter(galleryData,NewsDesc.this);
        galleryView.setAdapter(galleryAdapter);


        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(NewsDesc.this,Doc_Upload.class);
                intent
                        .putExtra("ID", id)
                        .putExtra("EMAIL", email);
                startActivity(intent);
                finish();
            }
        });


        Log.d(TAG, "onCreate: "+ id + " " + email);
        Bitmap[] cover = getImage("News/"+id+"/cover.jpg", coverImage);

        newsTitle.setText(name);
        newsDate.setText(date);
        newsDesc.setText(desc);
    }

    public Bitmap[] getImage(String path, ImageView img){
        final Bitmap[] bitmap = new Bitmap[1];
        StorageReference ref = storage.getReference(path);
        try {
            File localFile = File.createTempFile("temp",".jpg");
            ref.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            bitmap[0] = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            Log.d(TAG, "onSuccess: Image fetched");
                            img.setImageBitmap(bitmap[0]);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Error While fetching" + e);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
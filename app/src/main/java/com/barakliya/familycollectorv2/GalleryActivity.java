package com.barakliya.familycollectorv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class GalleryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        if (item.getItemId() == R.id.main_mm) {
//            if()  ASK ILAN ABOUT HOW TO KNOW IF IM IN THE SAVE ACTIVITY
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId()== R.id.gallery_mm){
            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId()== R.id.about_mm){
            Intent intent = new Intent(getApplicationContext(), AboutUsActivity.class);
            startActivity(intent);
            finish();
        }
        if(item.getItemId()==R.id.settings_mm){
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}



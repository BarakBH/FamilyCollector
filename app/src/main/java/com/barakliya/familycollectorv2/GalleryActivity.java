package com.barakliya.familycollectorv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Model> list;

    private MyAdaptarActivity adapter;

    // Has to be the same path and EXACT NAME (Image) as for our main activity
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new MyAdaptarActivity(this, list);
        recyclerView.setAdapter(adapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Model model = dataSnapshot.getValue(Model.class);
                    list.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        menu.findItem(R.id.gallery_mm).setVisible(false);
        menu.findItem(R.id.login_mm).setVisible(false);
        menu.findItem(R.id.register_mm).setVisible(false);
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
//        if (item.getItemId()== R.id.gallery_mm){
//            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
//            startActivity(intent);
//            finish();
//        }
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
        if(item.getItemId()==R.id.signout_mm) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(item.getItemId()==R.id.exit_mm) {
            finish();
        }
        return true;
    }
}



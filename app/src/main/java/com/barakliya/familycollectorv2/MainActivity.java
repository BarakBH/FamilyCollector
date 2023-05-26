package com.barakliya.familycollectorv2;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



public class MainActivity extends AppCompatActivity {

//    private static final int CAMERA_REQUEST = 111;
//    private static final int GALLERY_REQUEST = 222;
//
//    private ImageView imageView;
//
//
//
//    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//    startActivityForResult(takePhotoIntent , REQUEST_CAMERA_PHOTO);_
//
//    protected void onActivityResult (int requestCode , int resultCode , Intent data)
//    {
//        super.onActivityResult(requestCode , resultCode , data);
//
//        switch(requestCode)
//        {
//            case REQUEST_CAMERA_PHOTO:
//                if(resultCode == RESULT_OK)
//                {
//                    Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
//                ((ImageView)findViewById(R.id.imageView)).setImageBitmap(thumbnailBitmap);
//
//            break;
//        }
//    }

    private Button uploadBtn, galleryBtn;
    private ImageView imageView;
    private ProgressBar progressBar;
    //This path is making a folder of name "image" into the database
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button captureButton = findViewById(R.id.captureButton);
//        captureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                File photoFile = createTempFile();
//
//                ImageCapture.OutputFileOptions outputFileOptions =
//                        new ImageCapture.OutputFileOptions.Builder(photoFile).build();
//
//                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(MainActivity.this),
//                        new ImageCapture.OnImageSavedCallback() {
//                            @Override
//                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                                Uri photoUri = Uri.fromFile(photoFile);
//                                uploadImageToFirestore(photoUri);
//                            }
//
//                            @Override
//                            public void onError(@NonNull ImageCaptureException exception) {
//
//                            }
//             });
//}
//        });

        uploadBtn = findViewById(R.id.uploadBtn);
        galleryBtn = findViewById(R.id.GalleryBtn);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBarId);

        progressBar.setVisibility(View.INVISIBLE);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GalleryActivity.class));
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                // deprecated ? tho at ilan example he uses the same method
                startActivityForResult(galleryIntent, 2);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null){
                    uploadToFirebase(imageUri);
                }else{
                    Toast.makeText(MainActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

        }
    }


    //uploading to firebase
    private void uploadToFirebase(Uri uri){
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //passing data to real time data base *******THATS HOW I CAN IMPLEMENT THE GALLERY (I THINK)*******
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // made model in activity folders for the use of transfer real time database
                        Model model = new Model(uri.toString());
                        String modelId = root.push().getKey();
                        root.child(modelId).setValue(model);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Uploaded Successfully !", Toast.LENGTH_SHORT).show();;
                        imageView.setImageResource(R.drawable.familycollectorlogo);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //on uploading show progress bar
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //on failure uploading the image make toast
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Upload Failed...", Toast.LENGTH_SHORT).show();;
            }
        });
    }


    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        menu.findItem(R.id.main_mm).setVisible(false);
        menu.findItem(R.id.login_mm).setVisible(false);
        menu.findItem(R.id.register_mm).setVisible(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        item.setVisible(false);

//        if (item.getItemId() == R.id.main_mm) {
////            if()  ASK ILAN ABOUT HOW TO KNOW IF IM IN THE SAVE ACTIVITY
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
        if (item.getItemId()== R.id.gallery_mm){
            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId()== R.id.about_mm){
//            if (this instanceof AboutUsActivity)
//                // Show toast message indicating that you are already in the AboutUsActivity
//                Toast.makeText(getApplicationContext(), "You are already in the About Us", Toast.LENGTH_SHORT).show();
//            }else{
                Intent intent = new Intent(getApplicationContext(), AboutUsActivity.class);
                startActivity(intent);
                finish();
//            }
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
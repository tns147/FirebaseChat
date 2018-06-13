package com.example.dell.firebasetesting;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.firebasetesting.ModelClasses.User;
import com.example.dell.firebasetesting.services.LocationMonitoringService;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class LogoutActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myEmail;
    Button buttonLogout,btnSubmit;
    EditText editTextName;
    TextView textView;
    String name,email;
    String userId;
    ProgressBar progressBar;
    CircleImageView imageViewImagePicker;
    ImageView imageViewEdit;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    private final int MY_PERMISSIONS_REQUEST_CAMERA=2384;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] Byte;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef =storage.getReference();
    StorageReference imagesRef;
    SharedPreferences sharedPreferences;
    final String  prefName="pref";
    final String prefKey = "crntUId";
    boolean booleanImage =false;
    String imagePath;
    Intent myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        myService = new Intent(LogoutActivity.this, LocationMonitoringService.class);
        sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(prefKey,"");
        imagePath = sharedPreferences.getString(prefKey,"null");
        imagesRef= storageRef.child(imagePath);
        progressBar = findViewById(R.id.progress_bar);
        imageViewEdit = findViewById(R.id.imageview_edit);
        btnSubmit = findViewById(R.id.button_update);
        btnSubmit.setOnClickListener(this);
        imageViewImagePicker = findViewById(R.id.profile_image);
        myEmail =FirebaseDatabase.getInstance().getReference().child("Users");
        textView = findViewById(R.id.textview_email);
        editTextName = findViewById(R.id.edittext_name);
        buttonLogout = findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(this);
        imageViewImagePicker.setOnClickListener(this);
        //imageUrlUpdate();
        firebaseChangeListner();
    }



    private void imageUrlUpdate()
    { storageRef.child(imagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                myEmail.child(userId).child("image").setValue(uri.toString());
                Picasso.get().load(uri.toString()).placeholder(R.drawable.icons8_customer_100).error(R.drawable.ic_launcher_background).into(imageViewImagePicker);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LogoutActivity.this, "Url Updating failed.", Toast.LENGTH_SHORT).show();
            }
        });         progressBar.setVisibility(View.GONE);
    }

    private void firebaseChangeListner() {

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                textView.setText(user.getEmail()+" \n"+user.getUsername());
                Picasso.get().load(user.getImage()).placeholder(R.drawable.icons8_customer_100).error(R.drawable.ic_launcher_background).into(imageViewImagePicker);
                progressBar.setVisibility(View.GONE);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        myEmail.child(userId).addValueEventListener(userListener);

    }


    private void startingIntent() {
        Intent intent = new Intent(LogoutActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.profile_image:
                showPictureDialog();
                break;
            case R.id.button_update:
                if (booleanImage)
                {uploadImage();
                 booleanImage=false;   }
                if (!(editTextName.getText().toString().trim().isEmpty()))
                { myEmail.child(userId).child("username").setValue(editTextName.getText().toString());}
                break;
            case R.id.button_logout:
                stopService(myService);
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startingIntent();
                break;
        }
    }
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera",
                "Cancel"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                            case 3:
                                dialog.dismiss();
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);

    }

    private void takePhotoFromCamera() {
        if (ActivityCompat.checkSelfPermission(LogoutActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(LogoutActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LogoutActivity.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            if (ActivityCompat.checkSelfPermission(LogoutActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    booleanImage =true;
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    saveImage(bitmap);
                    Toast.makeText(LogoutActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageViewImagePicker.setImageBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(LogoutActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {

            booleanImage =true;
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageViewImagePicker.setImageBitmap(thumbnail);
            saveImage(thumbnail);


        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void uploadImage()
    {
        progressBar.setVisibility(View.VISIBLE);
        imageViewImagePicker.setDrawingCacheEnabled(true);
        imageViewImagePicker.buildDrawingCache();
        Bitmap bitmap =((BitmapDrawable)imageViewImagePicker.getDrawable()).getBitmap();
        byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        Byte =byteArrayOutputStream.toByteArray();
        StorageReference imagesRefer = storageRef.child(imagePath);
        final UploadTask uploadTask =imagesRefer.putBytes(Byte);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LogoutActivity.this, "Upload Is Un-Successful!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageUrlUpdate();

            }
        });
    }
}

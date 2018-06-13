package com.example.dell.firebasetesting;

import android.Manifest;
import com.example.dell.firebasetesting.services.LocationMonitoringService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.firebasetesting.Adapter.UserAdapter;
import com.example.dell.firebasetesting.ModelClasses.User;
import com.example.dell.firebasetesting.services.LocationMonitoringService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatlistActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean mAlreadyStartedService = false;
    DatabaseReference myEmail;
    CircleImageView circleImageView;
    String crntUId;
    private TextView textVUsername;
    final String  prefName="pref";
    private ProgressBar progressBar;
    final String prefKey = "email";
    List<User> users;
    RecyclerView recyclerView;
    private
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        crntUId = sharedPreferences.getString("crntUId","0");
        textVUsername = findViewById(R.id.textV_username_chatlist);
        progressBar = findViewById(R.id.progress_bar_chatlist);
        circleImageView = findViewById(R.id.circleImageViewChatlist);
        users = new ArrayList<>();
        myEmail = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseChangeListner();
        recyclerView = new RecyclerView(this);
        recyclerView = findViewById(R.id.recyclerview_chatlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //set LayoutManager to RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {
                            //mMsgView.setText(getString(R.string.msg_location_service_started) + "\n Latitude : " + latitude + "\n Longitude: " + longitude);
                            //Toast.makeText(context, latitude +"/n"+longitude, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatlistActivity.this,LogoutActivity.class);
                //intent.putExtra("url",users.get(0).getImage());
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions())
        {
            startService();
        }
        else{
            requestPermissions();
        }
    }


    private void firebaseChangeListner() {

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //String name = ds.child("name").getValue(String.class);
                    User user = ds.getValue(User.class);
                    if (user.getUserId().equals(crntUId))
                    { Picasso.get().load(user.getImage()).placeholder(R.drawable.icons8_customer_100).into(circleImageView);
                        textVUsername.setText(user.getUsername());
                    }
                    //Log.d("TAG", name);
                    if (!user.getUserId().equals(crntUId))
                    {users.add(user);}
                }

                // Get Post object and use the values to update the UI
                //User user = dataSnapshot.getValue(User.class);
                //textView.setText(user.getEmail()+" \n"+user.getUsername());
                UserAdapter userAdapter = new UserAdapter(users,ChatlistActivity.this);
                // ...
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        myEmail.addListenerForSingleValueEvent(userListener);
    }
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i("ChatlistActiviyt", "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(ChatlistActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i("ChatlistActiviyt", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(ChatlistActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void startService() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService ) {

            //mMsgView.setText(R.string.msg_location_service_started);

            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissions())
        {
            startService();
        }
        else{
            requestPermissions();
        }
    }
}

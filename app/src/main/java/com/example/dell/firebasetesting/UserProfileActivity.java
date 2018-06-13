package com.example.dell.firebasetesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

  private String userName,imgUrl;
  private   TextView txtVuserName;
  private   CircleImageView cImgVProfilePicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    userName = getIntent().getStringExtra("userName");
    imgUrl = getIntent().getStringExtra("imageUrl");
    txtVuserName = findViewById(R.id.textview_userprofile);
    cImgVProfilePicture = findViewById(R.id.circleImageView_userProfile);
    txtVuserName.setText(userName);
        Picasso.get().load(imgUrl).placeholder(R.drawable.icons8_customer_100).into(cImgVProfilePicture);

    }
}

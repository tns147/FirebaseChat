package com.example.dell.firebasetesting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.firebasetesting.Adapter.MessagesAdapter;
import com.example.dell.firebasetesting.ModelClasses.ChatMessage;
import com.example.dell.firebasetesting.ModelClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView circlerImageView;
    RecyclerView mChatsRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private EditText mMessageEditText;
    private FloatingActionButton mSendingButton;
    private DatabaseReference mMessagesDBREF;
    private DatabaseReference mUsersRef;
    private List<ChatMessage>mMessagesList = new ArrayList<>();
    private MessagesAdapter adapter = null;
    private String mReceiverId,mReceiverName;
    private String imgUrl;
    private String userName ;
    DatabaseReference myEmail = FirebaseDatabase.getInstance().getReference();
    TextView txtVUserNameChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatsRecyclerView = findViewById(R.id.recyclerview_of_messages);
        mMessageEditText = findViewById(R.id.input);
        mSendingButton = findViewById(R.id.fab);
        txtVUserNameChat= findViewById(R.id.textV_username_chat);
        mChatsRecyclerView.setHasFixedSize(true);
        mLayoutManager= new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mChatsRecyclerView.setLayoutManager(mLayoutManager);

        mMessagesDBREF = FirebaseDatabase.getInstance().getReference().child("Messages");
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mReceiverId = getIntent().getStringExtra("USER_ID");
        imgUrl = getIntent().getStringExtra("imageUrl");
        circlerImageView = findViewById(R.id.circleImageView);
        Picasso.get().load(imgUrl).placeholder(R.drawable.icons8_customer_100).into(circlerImageView);
        userName = getIntent().getStringExtra("userName");
        txtVUserNameChat.setText(userName);
        circlerImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,UserProfileActivity.class);
                intent.putExtra("imageUrl",imgUrl);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });

        mSendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString();
                String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (message.isEmpty())
                {
                    Toast.makeText(ChatActivity.this, "You Must Enter some text!", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessageToFireBase(message,senderId,mReceiverId);
                    mMessageEditText.getText().clear();
                }
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        queryMessageBetweenThisUserAndClickedUser();
        queryRecipientName(mReceiverId);

    }

    private void sendMessageToFireBase(String message,String senderId,String recevierId)
    {
        mMessagesList.clear();
        ChatMessage newMsg = new ChatMessage(message,senderId,recevierId);
        mMessagesDBREF.push().setValue(newMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful())
                {
                    Toast.makeText(ChatActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ChatActivity.this, "Message Sent Successfully!!", Toast.LENGTH_SHORT).show();
                    mMessageEditText.setText(null);
                    //hideSoftKeyboard();
                }
            }
        });
    }



    private void queryMessageBetweenThisUserAndClickedUser()
    {
        mMessagesDBREF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()){
                    ChatMessage chatMessage = snap.getValue(ChatMessage.class);
                    if (chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())&&chatMessage.getReceiverId().equals(mReceiverId)||chatMessage.getSenderId().equals(mReceiverId)&& chatMessage.getReceiverId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        mMessagesList.add(chatMessage);
                    }
                }
                populateMessagesRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateMessagesRecyclerView()
    {
        adapter = new MessagesAdapter(mMessagesList,ChatActivity.this);
        mChatsRecyclerView.setAdapter(adapter);
    }
    private void queryRecipientName(final String recevierId)
    {
        mUsersRef.child(recevierId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User reception = dataSnapshot.getValue(User.class);
                mReceiverName =reception.getUsername();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

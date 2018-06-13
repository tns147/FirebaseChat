package com.example.dell.firebasetesting.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.firebasetesting.ChatActivity;
import com.example.dell.firebasetesting.ModelClasses.User;
import com.example.dell.firebasetesting.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> mUsersList;
    private Context mContext;

    public UserAdapter(List<User> myDataSet, Context context)
    {
        mUsersList = myDataSet;
        mContext =context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.user_single_row,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user =mUsersList.get(position);
        holder.txtVPersonName.setText(user.getUsername());
        Picasso.get().load(user.getImage()).placeholder(R.drawable.icons8_customer_100).into(holder.imgVPersonImage);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            goToChatActivity(user.getUserId(),user.getImage(),user.getUsername());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtVPersonName;
        public CircleImageView imgVPersonImage;
        public View layout;

        public ViewHolder(View v){
            super(v);
            layout =v;
            txtVPersonName = v.findViewById(R.id.userName);
            imgVPersonImage = v.findViewById(R.id.userImage);
        }
    }

    public void add(int position,User person)
    {mUsersList.add(position,person);
    notifyItemInserted(position);
    }

    public void remove(int position)
    {
        mUsersList.remove(position);
        notifyItemRemoved(position);
    }

    private void goToChatActivity(String personId,String imgUrl,String userName)
    {
        Intent goToChat = new Intent(mContext, ChatActivity.class);
        goToChat.putExtra("USER_ID",personId);
        goToChat.putExtra("imageUrl",imgUrl);
        goToChat.putExtra("userName",userName);
        mContext.startActivity(goToChat);
    }
}

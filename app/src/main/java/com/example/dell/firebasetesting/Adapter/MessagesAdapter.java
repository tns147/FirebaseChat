package com.example.dell.firebasetesting.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.firebasetesting.ModelClasses.ChatMessage;
import com.example.dell.firebasetesting.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    Context mContext;
    List<ChatMessage>mMessageList;
    final int ITEM_TYPE_SENT= R.layout.sent_messages_row;
    final int ITEM_TYPE_RECIEVE = R.layout.recieve_messages_row;
    public MessagesAdapter(List<ChatMessage> mMessageList, Context context)
    {
        this.mMessageList = mMessageList;
        mContext=context;
    }

    @Override
    public int getItemViewType(int position) {

        if (mMessageList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
         return ITEM_TYPE_SENT;
        }
        else{
            return ITEM_TYPE_RECIEVE;
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = null;
       if (viewType == ITEM_TYPE_SENT)
       {
           v= LayoutInflater.from(mContext).inflate(R.layout.sent_messages_row,null);
       }
       else if (viewType == ITEM_TYPE_RECIEVE)
       {
           v = LayoutInflater.from(mContext).inflate(R.layout.recieve_messages_row,null);

       }
           return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChatMessage chatMessage = mMessageList.get(position);
        holder.txtVChatMessage.setText(chatMessage.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtVChatMessage;
        public View layouts;
        public ViewHolder(View itemView) {
            super(itemView);
            layouts = itemView;
            txtVChatMessage=itemView.findViewById(R.id.chatMsgTextView);
        }
    }
    public void add(int position,ChatMessage person)
    {mMessageList.add(position,person);
        notifyItemInserted(position);
    }
    public void remove(int position)
    {
        mMessageList.remove(position);
        notifyItemRemoved(position);
    }
}

package com.example.mercury;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {
    Context chatActivity;
    ArrayList<Users> usersArrayList;

    public UserAdapter(ChatActivity chatActivity, ArrayList<Users> usersArrayList) {
        this.chatActivity=chatActivity;
        this.usersArrayList=usersArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(chatActivity).inflate(R.layout.item_user_row,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Users users=usersArrayList.get(position);

        holder.user_name.setText(users.name);
        holder.user_status.setText(users.status);
        Picasso.get().load(users.imageURI).into(holder.user_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(chatActivity,messageActivity.class);
                intent.putExtra("name",users.getName());
                intent.putExtra("ReceiverImage",users.getImageUri());
                intent.putExtra("uid",users.getUid());
                chatActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        CircleImageView user_image;
        TextView user_name, user_status;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            user_image=itemView.findViewById(R.id.user_image);
            user_name=itemView.findViewById(R.id.user_name);
            user_status=itemView.findViewById(R.id.user_status);
        }
    }
}

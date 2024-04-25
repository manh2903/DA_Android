package com.ndm.da_test.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ndm.da_test.Entities.User;
import com.ndm.da_test.Interface.IClickItemFriendListener;
import com.ndm.da_test.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private IClickItemFriendListener iClickItemFriendListener;
    private List<User> mListFriend;

    public  FriendAdapter(List<User> list,IClickItemFriendListener IClickItemFriendListener)
    {
        mListFriend = list;
        iClickItemFriendListener = IClickItemFriendListener;
    }
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends,parent,false);
        return new FriendViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User user = mListFriend.get(position);
        if(mListFriend == null )
        {
            return;
        }
        holder.txt_full_name.setText(user.getFullName());
        holder.txt_email.setText(user.getEmail());
        Glide.with(holder.itemView.getContext())
                .load(user.getUri())
                .error(R.drawable.ic_friend)
                .into(holder.img_user2);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iClickItemFriendListener.onItemClick(user);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mListFriend != null)
        {
            Log.d("listfriend", String.valueOf(mListFriend.size()));
            return  mListFriend.size();
        }
        return 0;
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img_user2;
        TextView txt_full_name, txt_email;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_full_name = itemView.findViewById(R.id.txt_full_name);
            txt_email = itemView.findViewById(R.id.txt_email);
            img_user2 = itemView.findViewById(R.id.img_user2);
        }
    }

}

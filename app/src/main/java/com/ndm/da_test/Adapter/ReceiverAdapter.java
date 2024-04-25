package com.ndm.da_test.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ndm.da_test.Entities.User;
import com.ndm.da_test.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverAdapter.ReceiverViewHolder>{
    private List<User> musersReceiver;
    public ReceiverAdapter ( List<User> usersReceiver){

        musersReceiver = usersReceiver;

    }

    @NonNull
    @Override
    public ReceiverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_accept,parent,false);
        return new ReceiverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiverViewHolder holder, int position) {

        User user = musersReceiver.get(position);
        if(musersReceiver == null )
        {
            return;
        }
        String full_name = user.getFullName();
        String[] parts = full_name.split(" ");
        holder.txt_full_name.setText(parts[parts.length - 1]);
        holder.txt_email.setText(user.getEmail());
        Glide.with(holder.itemView.getContext())
                .load(user.getUri())
                .error(R.drawable.ic_friend)
                .into(holder.img_user2);
        holder.btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xác định vị trí của item trong danh sách
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {


                    String receiverId = musersReceiver.get(adapterPosition).getId();


                    delete(receiverId);


                    musersReceiver.remove(adapterPosition);

                    notifyItemRemoved(adapterPosition);


                    Log.d("RequestSendAdapter", "Button Hủy clicked for user: " + user.getFullName());
                }
            }
        });

        holder.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addSenderAsFriendAndDeleteReceiver(user);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(musersReceiver != null)
        {
            return musersReceiver.size();
        }
        return 0;
    }


    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user2;
        TextView txt_full_name, txt_email;
        Button btn_huy,btn_ok;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_full_name = itemView.findViewById(R.id.txt_full_name);
            txt_email = itemView.findViewById(R.id.txt_email);
            btn_huy = itemView.findViewById(R.id.btn_huy);
            btn_ok = itemView.findViewById(R.id.btn_ok);
            img_user2 = itemView.findViewById(R.id.img_user2);
        }
    }

    private void addSenderAsFriendAndDeleteReceiver(User user) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Thêm người nhận vào bảng friend của người gửi
            DatabaseReference senderFriendRef = FirebaseDatabase.getInstance().getReference().child("friend").child(currentUserId);
            senderFriendRef.child(user.getId()).setValue(true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                int position = musersReceiver.indexOf(user);
                                if (position != -1) {
                                    musersReceiver.remove(position);
                                    notifyItemRemoved(position);
                                }
                                Log.d("ReceiverAdapter", "Added receiver as friend for sender");
                            } else {
                                Log.e("ReceiverAdapter", "Error adding receiver as friend for sender", task.getException());
                            }
                        }
                    });

            // Thêm người gửi vào bảng friend của người nhận
            DatabaseReference receiverFriendRef = FirebaseDatabase.getInstance().getReference().child("friend").child(user.getId());
            receiverFriendRef.child(currentUserId).setValue(true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("ReceiverAdapter", "Added sender as friend for receiver");
                            } else {
                                Log.e("ReceiverAdapter", "Error adding sender as friend for receiver", task.getException());
                            }
                        }
                    });

            delete(user.getId());
        }
    }


    private void delete(String receiverId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference receiversRef = FirebaseDatabase.getInstance().getReference().child("receiverFriend").child(currentUserId);

            DatabaseReference sendsRef = FirebaseDatabase.getInstance().getReference().child("sendFriend").child(receiverId);
            // Sử dụng Query để tìm và xóa nút con có giá trị bằng receiverId
            receiversRef.orderByValue().equalTo(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("ReceiverAdapter", "Receiver deleted successfully");
                                } else {
                                    Log.e("ReceiverAdapter", "Error deleting receiver", task.getException());
                                }
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ReceiverAdapter", "Error reading database", databaseError.toException());
                }
            });

            sendsRef.orderByValue().equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("RequestSendAdapter", "Receiver deleted successfully");
                                } else {
                                    Log.e("RequestSendAdapter", "Error deleting receiver", task.getException());
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("RequestSendAdapter", "Error reading database", databaseError.toException());
                }
            });
        }
    }
}

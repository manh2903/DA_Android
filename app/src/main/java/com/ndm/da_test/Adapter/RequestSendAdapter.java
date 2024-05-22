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

public class RequestSendAdapter extends RecyclerView.Adapter<RequestSendAdapter.RequestSendViewHolder>{

    private List<User> musersSend;

    public RequestSendAdapter ( List<User> usersSend){
        musersSend = usersSend;
    }

    @NonNull
    @Override
    public RequestSendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_request,parent,false);
        return new RequestSendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestSendViewHolder holder, int position) {

        User user = musersSend.get(position);
        if(musersSend == null )
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
                    // Lấy id của người gửi và người nhận tương ứng

                    String receiverId = musersSend.get(adapterPosition).getId();

                    // Xóa id của người nhận khỏi danh sách trong id của người gửi
                    deleteReceiverFromSender( receiverId);

                    // Xóa item khỏi danh sách
                    musersSend.remove(adapterPosition);
                    // Cập nhật giao diện
                    notifyItemRemoved(adapterPosition);

                    // Hiển thị thông báo hoặc thực hiện các hành động khác nếu cần
                    Log.d("RequestSendAdapter", "Button Hủy clicked for user: " + user.getFullName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(musersSend != null)
        {
            return musersSend.size();
        }
        return 0;
    }

    public class RequestSendViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user2;
        TextView txt_full_name, txt_email;
        Button btn_huy;
        public RequestSendViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_full_name = itemView.findViewById(R.id.txt_full_name);
            txt_email = itemView.findViewById(R.id.txt_email);
            btn_huy = itemView.findViewById(R.id.btn_huy);
            img_user2 = itemView.findViewById(R.id.img_user2);
        }
    }

    private void deleteReceiverFromSender(String receiverId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("sendFriend").child(currentUserId);
            // Sử dụng Query để tìm và xóa nút con có giá trị bằng receiverId
            requestsRef.orderByValue().equalTo(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
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



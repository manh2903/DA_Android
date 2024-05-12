package com.ndm.da_test.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ndm.da_test.Entities.Data;
import com.ndm.da_test.Entities.Noti_receiver;
import com.ndm.da_test.Interface.IClickNotiListener;
import com.ndm.da_test.R;

import java.util.List;

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.NotiViewHolder> {

    private IClickNotiListener iClickNotiListener;
    private List<Data> notiReceivers;



    public NotiAdapter(List<Data> NotiReceivers, IClickNotiListener listener)
    {
        notiReceivers = NotiReceivers;
        iClickNotiListener = listener;

    }


    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noti, parent, false);
        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        Data notiReceiver = notiReceivers.get(position);
        if(notiReceivers == null)
        {
            return;
        }
        holder.tv_title.setText(notiReceiver.getTitle());
        holder.tv_Body.setText(notiReceiver.getBody());
        holder.tv_Time.setText(notiReceiver.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickNotiListener.onItemClick(notiReceiver);

            }
        });
    }

    @Override
    public int getItemCount() {
        if(notiReceivers != null)
        {
            return notiReceivers.size();
        }
        return 0;
    }

    public class NotiViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_Body,tv_Time;

        LinearLayout layout;

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_Body = itemView.findViewById(R.id.tv_body);
            tv_Body.setMaxLines(2); // Số dòng tối đa
            tv_Time = itemView.findViewById(R.id.tv_time);
            layout = itemView.findViewById(R.id.layout_item);
        }
    }

}

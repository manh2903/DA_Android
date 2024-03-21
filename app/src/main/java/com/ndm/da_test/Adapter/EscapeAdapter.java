package com.ndm.da_test.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ndm.da_test.Entities.Escape;
import com.ndm.da_test.R;

import java.util.List;

public class EscapeAdapter extends RecyclerView.Adapter<EscapeAdapter.EscapeViewHolder>   {

    private Context mContext;
    private List<Escape> mEscape;

    public EscapeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Escape> mEscape) {
        this.mEscape = mEscape;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EscapeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_action_escape, parent, false);
        return new EscapeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EscapeViewHolder holder, int position) {
        Escape escape = mEscape.get(position);
        if(escape == null)
        {
            return;
        }
        holder.tv_id.setText(escape.getId());
        holder.tv_name_action.setText(escape.getName());
    }

    @Override
    public int getItemCount() {
        if (mEscape != null)
        {
            return  mEscape.size();
        }
        return 0;
    }

    public class EscapeViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_id, tv_name_action;

        public EscapeViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.tv_name);
            tv_name_action = itemView.findViewById(R.id.tv_name_action);
        }
    }

}

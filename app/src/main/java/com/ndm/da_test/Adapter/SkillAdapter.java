package com.ndm.da_test.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ndm.da_test.Entities.Skill;
import com.ndm.da_test.Interface.IClickItemSkillListener;
import com.ndm.da_test.R;

import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {

    private IClickItemSkillListener iClickItemSkillListener;
    private List<Skill> mSkillList;
    public SkillAdapter(List<Skill> skillList, IClickItemSkillListener listener) {
        mSkillList = skillList;
        iClickItemSkillListener = listener;
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_escape_skill, parent, false);
        return new SkillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        Skill skill = mSkillList.get(position);
        if(mSkillList == null)
        {
            return;
        }
        try {
            int tvIdValue = position + 1;
            holder.tvId.setText(String.valueOf(tvIdValue));
            holder.tvNameAction.setText(skill.getName());
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iClickItemSkillListener.onItemClick(skill);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(mSkillList != null)
        {
            return mSkillList.size();
        }
        return 0;
    }

    public class SkillViewHolder extends RecyclerView.ViewHolder {
        TextView tvId;
        TextView tvNameAction;
        LinearLayout layout;

        public SkillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvNameAction = itemView.findViewById(R.id.tv_name_action);
            layout = itemView.findViewById(R.id.layout_item);
        }
    }
}

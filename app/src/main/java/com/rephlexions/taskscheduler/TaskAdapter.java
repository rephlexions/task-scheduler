package com.rephlexions.taskscheduler;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.rephlexions.taskscheduler.db.Task;

import org.w3c.dom.Text;


public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskHolder> {
    private onItemClickListener listener;

    public TaskAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getPriority().equals(newItem.getPriority());
        }
    };

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // parent is the recycler view itself and since the RV is in the MainActivity we can get the context
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task currentTask = getItem(position);
        holder.textViewTitle.setText(currentTask.getTitle());
        holder.textViewPriority.setText(currentTask.getPriority());
        if(currentTask.getDescription().isEmpty()){
            holder.descriptionIcon.setVisibility(View.INVISIBLE);
        }
        else {holder.descriptionIcon.setVisibility(View.VISIBLE);}
        if(currentTask.getPriority().equals("Low")){
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
        else if(currentTask.getPriority().equals("Medium")){
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
        }
        else if(currentTask.getPriority().equals("High")){
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#E91E63")));
        }
        else if (currentTask.getPriority() == null || currentTask.getPriority().equals("None")){
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        }
    }

    public Task getTaskAt(int position) {
        return getItem(position);
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewPriority;
        private CheckBox checkBox;
        private ImageView descriptionIcon;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);
            checkBox = itemView.findViewById(R.id.checkBox);
            descriptionIcon = itemView.findViewById(R.id.description_icon);
            boolean checked = checkBox.isChecked();

            //Set listener on card view items
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: https://android.jlelse.eu/android-handling-checkbox-state-in-recycler-views-71b03f237022
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}

package com.rephlexions.taskscheduler;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.rephlexions.taskscheduler.db.Task;

import static com.rephlexions.taskscheduler.AddEditTaskActivity.getDate;


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
        holder.textViewCategory.setText(currentTask.getCategory());

        if (currentTask.getDueDate() == 0L) {
            holder.textViewDate.setVisibility(View.INVISIBLE);
            holder.textViewTime.setText("No alarm set");
            holder.textViewTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            String[] dateTime = getDate(currentTask.getDueDate(), "MM/dd/yy HH:mm");
            holder.textViewDate.setVisibility(View.VISIBLE);
            holder.textViewTime.setVisibility(View.VISIBLE);
            holder.textViewTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alarm_on_black, 0, 0, 0);
            holder.textViewDate.setText(dateTime[0]);
            holder.textViewTime.setText(dateTime[1]);
        }

        if (currentTask.getStatus().equals("completed")) {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.checkBox.setChecked(true);
        } else if (currentTask.getStatus().equals("pending")) {
            holder.textViewTitle.setPaintFlags(0);
            holder.checkBox.setChecked(false);
        } else if (currentTask.getStatus().equals("ongoing")) {
            holder.textViewTitle.setPaintFlags(0);
            holder.checkBox.setChecked(false);
        }

        if (currentTask.getPriority().equals("Low")) {
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        } else if (currentTask.getPriority().equals("Medium")) {
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
        } else if (currentTask.getPriority().equals("High")) {
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#E91E63")));
        } else if (currentTask.getPriority() == null || currentTask.getPriority().equals("None")) {
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        }
    }

    public Task getTaskAt(int position) {
        return getItem(position);
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private CheckBox checkBox;
        private TextView textViewDate;
        private TextView textViewTime;
        private TextView textViewCategory;

        public TaskHolder(@NonNull final View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            checkBox = itemView.findViewById(R.id.item_checkBox);
            textViewDate = itemView.findViewById(R.id.task_alarm_date);
            textViewTime = itemView.findViewById(R.id.task_alarm_time);
            textViewCategory = itemView.findViewById(R.id.task_category);

            //Set listener on card view items
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Task currentTask = getItem(position);
                    if (checkBox.isChecked()) {
                        currentTask.setStatus("completed");
                        textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else if (!checkBox.isChecked()) {
                        currentTask.setStatus("pending");
                        textViewTitle.setPaintFlags(0);
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

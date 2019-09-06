package com.rephlexions.taskscheduler.utils;

import com.rephlexions.taskscheduler.db.Category;

import java.util.Collections;
import java.util.List;

public class CategoryListAdapter {

    private List<Category> mCategory = Collections.emptyList(); // Cached copy of words


    public void setCategory(List<Category> category){
        mCategory = category;
    }

    // getItemCount() is called many times, and when it is first called,
// mWords has not been updated (means initially, it's null, and we can't return null).
    public int getItemCount() {
        if (mCategory != null)
            return mCategory.size();
        else return 0;
    }
}
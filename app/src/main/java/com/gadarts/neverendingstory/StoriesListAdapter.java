package com.gadarts.neverendingstory;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gadarts.neverendingstory.bl.Story;
import com.gadarts.neverendingstory.fragments.CurrentStoryFragment;

import java.util.ArrayList;
import java.util.Optional;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class StoriesListAdapter extends BaseAdapter {
    public static final String KEY_STORY_ID = "story_id";
    private final ArrayList<Story> stories;
    private final androidx.fragment.app.FragmentManager supportFragmentManager;

    public StoriesListAdapter(ArrayList<Story> stories,
                              FragmentManager supportFragmentManager) {
        this.stories = stories;
        this.supportFragmentManager = supportFragmentManager;
    }

    @Override
    public int getCount() {
        return stories.size();
    }

    @Override
    public Object getItem(int position) {
        return stories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Optional<View> optional = Optional.ofNullable(convertView);
        return optional.orElseGet(() -> createNewItemView(position, parent));
    }

    private View createNewItemView(int position, ViewGroup parent) {
        TextView textView = new TextView(parent.getContext());
        textView.setOnClickListener(view -> {
            FragmentTransaction transaction = supportFragmentManager.beginTransaction();
            Fragment currentStoryFragment = new CurrentStoryFragment();
            transaction.replace(R.id.main_activity, currentStoryFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        textView.setText(stories.get(position).getName());
        return textView;
    }
}

package com.gadarts.neverendingstory;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gadarts.neverendingstory.activities.StoryViewActivity;
import com.gadarts.neverendingstory.bl.Story;

import java.util.ArrayList;
import java.util.Optional;

import androidx.annotation.RequiresApi;

public class StoriesListAdapter extends BaseAdapter {
    private static final String ERROR_INVALID_INDEX = "The provided index must be a natural number!";
    public static final String SELECTED_STORY = "selected_story";
    private ArrayList<Story> stories = new ArrayList<>();

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
        if (position < 0) throw new IndexOutOfBoundsException(ERROR_INVALID_INDEX);
        Optional<View> optional = Optional.ofNullable(convertView);
        return optional.orElseGet(() -> createNewItemView(position, parent));
    }

    private View createNewItemView(int position, ViewGroup parent) {
        Activity activity = (Activity) parent.getContext();
        TextView textView = new TextView(activity);
        Story story = stories.get(position);
        textView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, StoryViewActivity.class);
            intent.putExtra(SELECTED_STORY, story.getId());
            activity.startActivity(intent);
        });
        textView.setText(story.getTitle());
        return textView;
    }

    public void clear() {
        stories.clear();
    }

    public void setList(ArrayList<Story> stories) {
        this.stories.clear();
        this.stories.addAll(stories);
    }
}

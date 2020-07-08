package com.gadarts.neverendingstory;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gadarts.neverendingstory.activities.StoryViewActivity;
import com.gadarts.neverendingstory.models.Story;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

import androidx.annotation.RequiresApi;

public class StoriesListAdapter extends BaseAdapter {
    private static final String ERROR_INVALID_INDEX = "The provided index must be a natural number!";
    public static final String SELECTED_STORY = "selected_story";
    private static final String LABEL_BY = "By %s";
    private static final int ITEM_PADDING = 10;
    private static final int PREVIEW_PADDING = 20;
    private static final int PREVIEW_MARGIN = 10;
    private static final int TEXT_VIEW_PADDING = 10;
    public static final String THREE_POINTS = "...";
    private static final int MAX_PREVIEW_LENGTH = 80;
    private final ArrayList<Story> stories = new ArrayList<>();

    @Override
    public int getCount() {
        return stories.size();
    }

    @Override
    public Object getItem(final int position) {
        return stories.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        if (position < 0) throw new IndexOutOfBoundsException(ERROR_INVALID_INDEX);
        Optional<View> optional = Optional.ofNullable(convertView);
        return optional.orElseGet(() -> createListItem(position, parent));
    }

    private View createListItem(final int position, final ViewGroup parent) {
        Activity activity = (Activity) parent.getContext();
        Story story = stories.get(position);
        LinearLayout linearLayout = createListItemView(activity, story);
        linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(activity, StoryViewActivity.class);
            intent.putExtra(SELECTED_STORY, story.getId());
            activity.startActivity(intent);
        });
        return linearLayout;
    }

    @NotNull
    private LinearLayout createListItemView(final Activity activity, final Story story) {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(ITEM_PADDING, ITEM_PADDING, ITEM_PADDING, ITEM_PADDING);
        linearLayout.addView(createTextView(activity, story.getTitle()));
        createPreview(activity, story, linearLayout);
        String labelBy = String.format(LABEL_BY, story.getOwner().getName());
        linearLayout.addView(createTextView(activity, labelBy));
        return linearLayout;
    }

    private void createPreview(final Activity activity,
                               final Story story,
                               final LinearLayout linearLayout) {
        if (story.getParagraphs().isEmpty()) return;
        TextView preview = createTextView(activity, getPreviewShortened(story));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        preview.setPadding(PREVIEW_PADDING, PREVIEW_PADDING, PREVIEW_PADDING, PREVIEW_PADDING);
        params.setMargins(PREVIEW_MARGIN, PREVIEW_MARGIN, PREVIEW_MARGIN, PREVIEW_MARGIN);
        preview.setLayoutParams(params);
        preview.setBackground(activity.getDrawable(R.drawable.text_view_rounded_corners));
        linearLayout.addView(preview);
    }

    private String getPreviewShortened(final Story story) {
        String content = story.getParagraphs().get(0).getContent();
        if (content.length() > MAX_PREVIEW_LENGTH) {
            content = content.substring(0, MAX_PREVIEW_LENGTH) + THREE_POINTS;
        }
        return content;
    }


    @NotNull
    private TextView createTextView(final Activity activity, final String text) {
        TextView textView = new TextView(activity);
        textView.setPadding(
                TEXT_VIEW_PADDING,
                TEXT_VIEW_PADDING,
                TEXT_VIEW_PADDING,
                TEXT_VIEW_PADDING);
        textView.setText(text);
        return textView;
    }

    public void clear() {
        stories.clear();
    }

    public void setList(final ArrayList<Story> stories) {
        this.stories.clear();
        this.stories.addAll(stories);
    }
}

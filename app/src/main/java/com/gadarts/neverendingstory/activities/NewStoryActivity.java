package com.gadarts.neverendingstory.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.gadarts.neverendingstory.HttpCallTask;
import com.gadarts.neverendingstory.OnRequestResult;
import com.gadarts.neverendingstory.R;
import com.google.gson.Gson;

import java.util.HashMap;

import androidx.fragment.app.FragmentActivity;

public class NewStoryActivity extends FragmentActivity {


    private static final String POST_NEW_STORY = ListActivity.HOST + "new_story";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_PARAGRAPH = "paragraph";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);
        Button publishButton = findViewById(R.id.new_story_publish_button);
        EditText titleEditText = findViewById(R.id.new_story_title_input);
        EditText paragraphEditText = findViewById(R.id.new_story_paragraph_input);
        publishButton.setOnClickListener(view -> {
            OnRequestResult onSuccess = (String response, Gson gson) -> {
//                ArrayList<String> storiesNames = gson.fromJson(gson.fromJson(response, JsonObject.class).get(STORIES), ArrayList.class);
//                ArrayList<Story> stories = new ArrayList<>();
//                storiesNames.forEach(name -> stories.add(new Story(name)));
//                StoriesListAdapter adapter = new StoriesListAdapter(stories, getSupportFragmentManager());
//                StoriesListFragment fragment = new StoriesListFragment();
//                fragment.setAdapter(adapter);
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.add(R.id.main_activity, fragment, "stories_list_fragment");
//                ft.commit();
            };
            HttpCallTask task = new HttpCallTask(POST_NEW_STORY, HttpCallTask.RequestTypes.POST, onSuccess);
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put(PARAMETER_TITLE, String.valueOf(titleEditText.getText()));
            parameters.put(PARAMETER_PARAGRAPH, String.valueOf(paragraphEditText.getText()));
            task.setParameters(parameters);
            task.execute();
        });
    }


}

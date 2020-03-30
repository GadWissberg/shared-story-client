package com.gadarts.neverendingstory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.gadarts.neverendingstory.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.FragmentActivity;

public class ListActivity extends FragmentActivity {

    public static final String HOST = "http://192.168.1.136:5000/";
    private static final String GET_STORIES = HOST + "get_stories";
    private static final String STORIES = "stories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button logout = findViewById(R.id.button_logout);
        logout.setOnClickListener(view -> {
            SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_LOGIN, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(LoginActivity.KEY_MAIL, null);
            editor.putString(LoginActivity.KEY_PASSWORD, null);
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStoryActivity.class);
            startActivity(intent);
        });
//        OnRequestResult onSuccess = (String response, Gson gson) -> {
//            ArrayList<String> storiesNames = gson.fromJson(gson.fromJson(response, JsonObject.class).get(STORIES), ArrayList.class);
//            ArrayList<Story> stories = new ArrayList<>();
//            storiesNames.forEach(name -> stories.add(new Story(name)));
//            StoriesListAdapter adapter = new StoriesListAdapter(stories, getSupportFragmentManager());
//            StoriesListFragment fragment = new StoriesListFragment();
//            fragment.setAdapter(adapter);
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.add(R.id.main_activity, fragment, "stories_list_fragment");
//            ft.commit();
//        };
//        HttpCallTask task = new HttpCallTask(GET_STORIES, HttpCallTask.RequestTypes.GET, onSuccess);
//        task.execute();
    }


}

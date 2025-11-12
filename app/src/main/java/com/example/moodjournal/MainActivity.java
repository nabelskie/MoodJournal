package com.example.moodjournal;

import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private List<String> moodKeys;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewMoods);
        sharedPreferences = getSharedPreferences("MoodJournal", MODE_PRIVATE);
        moodKeys = new ArrayList<>();

        registerForContextMenu(listView);

        findViewById(R.id.btnAddMood).setOnClickListener(v ->
                startActivity(new Intent(this, AddMoodActivity.class)));

        listView.setOnItemClickListener((parent, view, position, id) ->
                editMoodEntry(position));

        loadMoodEntries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMoodEntries();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Options");
        menu.add("Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        deleteMoodEntry(info.position);
        return true;
    }

    private void loadMoodEntries() {
        List<String> moodEntries = new ArrayList<>();
        moodKeys.clear();

        sharedPreferences.getAll().forEach((key, value) -> {
            if (key.startsWith("mood_")) {
                moodKeys.add(key);
                moodEntries.add(value.toString());
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moodEntries);
        listView.setAdapter(adapter);
    }

    private void deleteMoodEntry(int position) {
        sharedPreferences.edit().remove(moodKeys.get(position)).apply();
        Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
        loadMoodEntries();
    }

    private void editMoodEntry(int position) {
        Intent intent = new Intent(this, AddMoodActivity.class);
        intent.putExtra("EDIT_KEY", moodKeys.get(position));
        intent.putExtra("EDIT_VALUE", adapter.getItem(position));
        startActivity(intent);
    }
}
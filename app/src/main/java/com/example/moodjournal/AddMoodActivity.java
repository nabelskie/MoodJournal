package com.example.moodjournal;

import android.content.*;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddMoodActivity extends AppCompatActivity {
    private RadioGroup radioGroupMood;
    private EditText editTextNote;
    private String editKey;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood);

        radioGroupMood = findViewById(R.id.radioGroupMood);
        editTextNote = findViewById(R.id.editTextNote);
        Button btnSave = findViewById(R.id.btnSave);
        SharedPreferences sharedPreferences = getSharedPreferences("MoodJournal", MODE_PRIVATE);

        // Check edit mode
        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_KEY")) {
            isEditMode = true;
            editKey = intent.getStringExtra("EDIT_KEY");
            btnSave.setText("Update Mood");
            prefillData(intent.getStringExtra("EDIT_VALUE"));
        }

        btnSave.setOnClickListener(v -> saveOrUpdateMood(sharedPreferences));
    }

    private void prefillData(String moodData) {
        try {
            String mood = moodData.split(" - ")[1].split(":")[0].trim();
            int radioId = getResources().getIdentifier("radio" + mood, "id", getPackageName());
            radioGroupMood.check(radioId);

            if (moodData.contains(":") && moodData.split(":").length > 1) {
                editTextNote.setText(moodData.split(":")[1].trim());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOrUpdateMood(SharedPreferences sharedPreferences) {
        String mood = getSelectedMood();
        if (mood.isEmpty()) {
            Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = editTextNote.getText().toString();
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
        String value = date + " - " + mood + ": " + note;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String key = isEditMode ? editKey : "mood_" + System.currentTimeMillis();

        editor.putString(key, value);
        editor.apply();

        Toast.makeText(this, isEditMode ? "Updated!" : "Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String getSelectedMood() {
        int selectedId = radioGroupMood.getCheckedRadioButtonId();
        if (selectedId == R.id.radioHappy) return "Happy";
        if (selectedId == R.id.radioSad) return "Sad";
        if (selectedId == R.id.radioAngry) return "Angry";
        if (selectedId == R.id.radioCalm) return "Calm";
        return "";
    }
}
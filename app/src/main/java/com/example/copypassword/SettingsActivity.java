/*package com.example.copypassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.example.copypassword.databinding.ActivitySettingsBinding;
import com.example.copypassword.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    int id, note;
    String name, lesson;
    ArrayList<Model> arrayList;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        arrayList = new ArrayList<>();
        binding.recyclerview1.setLayoutManager(new LinearLayoutManager(this));
        adapter=new Adapter(arrayList);
        binding.recyclerview1.setAdapter(adapter);
    }

    private void getData() {
        SQLiteDatabase database = this.openOrCreateDatabase("datas", MODE_PRIVATE, null);

        Cursor cursor = database.rawQuery("select * from datas", null);
        int idIndex = cursor.getColumnIndex("id");
        int nameIndex=cursor.getColumnIndex("name");
        int lessonIndex=cursor.getColumnIndex("lesson");
        int noteIndex=cursor.getColumnIndex("note");
        arrayList.clear();

        while (cursor.moveToNext()) {
            id = cursor.getInt(idIndex);
            name = cursor.getString(nameIndex);
            lesson = cursor.getString(lessonIndex);
            note = cursor.getInt(noteIndex);

            Model model = new Model(id, name, lesson, note);
            arrayList.add(model);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    public void addData (View view) {
        Intent intent = new Intent(this, AddDataActivity.class);
        intent.putExtra("info", "new");
        startActivity(intent);
    }
}*/
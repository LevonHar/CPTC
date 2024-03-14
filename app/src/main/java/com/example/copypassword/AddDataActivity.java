package com.example.copypassword;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.copypassword.databinding.ActivityAddDataBinding;

public class AddDataActivity extends AppCompatActivity {
    SQLiteDatabase database;
    int dataId;
    private ActivityAddDataBinding binding;
    CheckBox showPassword;
    EditText nameEditText, lessonEditText;
    Button updateButton, addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        showPassword = findViewById(R.id.showPassword);
        nameEditText = findViewById(R.id.nameEditText);
        lessonEditText = findViewById(R.id.lessonEditText);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    nameEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    nameEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        database = openOrCreateDatabase("datas", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS datas (id INTEGER PRIMARY KEY, name VARCHAR, lesson VARCHAR, note INTEGER)");

        Intent intent = getIntent();
        String getInfo = intent.getStringExtra("info");

        if (getInfo.equals("new")) {
            binding.updateButton.setVisibility(View.GONE);
            binding.deleteButton.setVisibility(View.GONE);
        } else {
            binding.addButton.setVisibility(View.GONE);
            dataId = intent.getIntExtra("dataId", 0);

            Cursor cursor = database.rawQuery("select * from datas where id=?", new String[]{String.valueOf(dataId)});
            int nameIndex = cursor.getColumnIndex("name");
            int lessonIndex = cursor.getColumnIndex("lesson");

            while (cursor.moveToNext()) {
                binding.nameEditText.setText(cursor.getString(nameIndex));
                binding.lessonEditText.setText(cursor.getString(lessonIndex));
            }
            cursor.close();
        }

        updateButton = findViewById(R.id.updateButton);
        addButton = findViewById(R.id.addButton);
    }

    public void add(View view) {
        String name = binding.nameEditText.getText().toString();
        String lesson = binding.lessonEditText.getText().toString();

        String sqlString = "insert into datas (name, lesson, note) VALUES (?,?,?)";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1, name);
        sqLiteStatement.bindString(2, lesson);

        /*if (!name.matches(TEXT_PATTERN)) {
            nameEditText.setError("Enter Proper Password (maximum 10 characters)");
            nameEditText.requestFocus();
        } else if (!lesson.isEmpty() && !lesson.matches(TEXT_PATTERN)) {
            lessonEditText.setError("Enter Proper Name (maximum 10 characters)");
            lessonEditText.requestFocus();
        } else {
            sendUserToNextActivity();
            sqLiteStatement.execute();
            onBackPressed();
        }*/

        if (lesson.isEmpty()) {
            lessonEditText.setError("Line must not be empty");
            lessonEditText.requestFocus();
        } else if (name.isEmpty()) {
            nameEditText.setError("Line must not be empty");
            nameEditText.requestFocus();
        } else {
            sendUserToNextActivity();
            sqLiteStatement.execute();
            onBackPressed();
        }
    }

    public void update(View view) {
        try {
            String sqlString = "update datas set name=?, lesson=?, note=? where id=?";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, binding.nameEditText.getText().toString());
            sqLiteStatement.bindString(2, binding.lessonEditText.getText().toString());
            sqLiteStatement.bindLong(4, dataId);

            /*if(!name.matches(TEXT_PATTERN)) {
                nameEditText.setError("Enter Proper Password (maximum 10 characters)");
                nameEditText.requestFocus();
            } else if (!lesson.isEmpty() && !lesson.matches(TEXT_PATTERN)) {
                lessonEditText.setError("Enter Proper Name (maximum 10 characters)");
                lessonEditText.requestFocus();
            } else {
                sendUserToNextActivity();
                sqLiteStatement.execute();
            }*/
            String name = binding.nameEditText.getText().toString();
            String lesson = binding.lessonEditText.getText().toString();


            if (lesson.isEmpty()) {
                lessonEditText.setError("Line must not be empty");
                lessonEditText.requestFocus();
            } else if (name.isEmpty()) {
                nameEditText.setError("Line must not be empty");
                nameEditText.requestFocus();
            } else {
                sendUserToNextActivity();
                sqLiteStatement.execute();
                onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delete(View view) {
        String sqlString = "delete from datas where id=?";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindLong(1, dataId);

        sqLiteStatement.execute();
        onBackPressed();
        //intentMainActivity();
    }

    /*private void intentMainActivity() {
        Intent intent = new Intent(AddDataActivity.this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void performAuthForUpdate() {
        String name = nameEditText.getText().toString();

        if (name.isEmpty() || !name.matches(TEXT_PATTERN)) {
            nameEditText.setError("Enter Proper Password (maximum 10 characters)");
            nameEditText.requestFocus();
        } else {
            sendUserToNextActivity();
        }
    }

    private void performAuthForAdd() {
        String lesson = lessonEditText.getText().toString();

        if (lesson.isEmpty() || !lesson.matches(TEXT_PATTERN)) {
            lessonEditText.setError("Enter Proper Password (maximum 10 characters)");
            lessonEditText.requestFocus();
        } else {
            sendUserToNextActivity();
        }
    }*/
    private void sendUserToNextActivity() {
        Intent intent = new Intent(AddDataActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
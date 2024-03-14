package com.example.copypassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.copypassword.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    Button button1;
    int id, note;
    String name, lesson;
    ArrayList<Model> arrayList;
    Adapter adapter;
    private ActivityMainBinding binding;

    RecyclerView recyclerView;
    TextView emptyTextView;

    ImageView imageView;
    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        recyclerView = findViewById(R.id.recyclerview);
        emptyTextView = findViewById(R.id.emptyTextView);

        arrayList = new ArrayList<>();
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter=new Adapter(arrayList);
        binding.recyclerview.setAdapter(adapter);

        button1 = findViewById(R.id.button1);

        imageView = findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
    }

    private void onClick() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        registerForContextMenu(recyclerView);

        SQLiteDatabase database = this.openOrCreateDatabase("datas", MODE_PRIVATE, null);

        // Create the table if it doesn't exist
        database.execSQL("CREATE TABLE IF NOT EXISTS datas (id INTEGER PRIMARY KEY, name TEXT, lesson TEXT, note INTEGER)");

        Cursor cursor = database.rawQuery("SELECT * FROM datas", null);
        int idIndex = cursor.getColumnIndex("id");
        int nameIndex = cursor.getColumnIndex("name");
        int lessonIndex = cursor.getColumnIndex("lesson");
        int noteIndex = cursor.getColumnIndex("note");
        arrayList.clear();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String lesson = cursor.getString(lessonIndex);
                int note = cursor.getInt(noteIndex);

                Model model = new Model(id, name, lesson, note);
                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        if (arrayList.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getData();
        onClick();
    }
    private BiometricPrompt getPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser(errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Succeeded!");
                Intent intent = new Intent(MainActivity.this, AddDataActivity.class);
                intent.putExtra("info", "new");
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed!");
            }
        };
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, callback);
        return biometricPrompt;
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void getData() {
        SQLiteDatabase database = this.openOrCreateDatabase("datas", MODE_PRIVATE, null);

        // Create the table if it doesn't exist
        database.execSQL("CREATE TABLE IF NOT EXISTS datas (id INTEGER PRIMARY KEY, name TEXT, lesson TEXT, note INTEGER)");

        Cursor cursor = database.rawQuery("SELECT * FROM datas", null);
        int idIndex = cursor.getColumnIndex("id");
        int nameIndex = cursor.getColumnIndex("name");
        int lessonIndex = cursor.getColumnIndex("lesson");
        int noteIndex = cursor.getColumnIndex("note");
        arrayList.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String lesson = cursor.getString(lessonIndex);
            int note = cursor.getInt(noteIndex);

            Model model = new Model(id, name, lesson, note);
            arrayList.add(model);
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    public void addData (View view) {
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale);
        view.startAnimation(scaleAnimation);

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Please Verify")
                .setDescription("User Authentication is required to proceed")
                .setDeviceCredentialAllowed(true)
                .build();
        getPrompt().authenticate(promptInfo);
    }
}
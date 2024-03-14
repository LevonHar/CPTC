package com.example.copypassword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.copypassword.databinding.RecyclerviewRowBinding;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class Adapter extends RecyclerView.Adapter<Adapter.DataHolder> {

    ArrayList<Model> arrayList;

    TextView textView;
    Handler handler = new Handler();

    public Adapter(ArrayList<Model> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewRowBinding recyclerviewRowBinding = RecyclerviewRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DataHolder(recyclerviewRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.nameTextView.setText(arrayList.get(position).name);
        holder.binding.lessonEditView.setText(arrayList.get(position).lesson);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                textView = holder.binding.nameTextView;
                NotificationCompat.Builder builder1 = new NotificationCompat.Builder(
                        v.getContext(), "My Notification")
                        .setContentTitle("Notification")
                        .setContentText("Text copied to clipboard")
                        .setSmallIcon(R.drawable.baseline_content_copy_24)
                        .setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(v.getContext());
                managerCompat.notify(100, builder1.build());

                Handler h = new Handler();
                long delayInMilliseconds = 30000;
                h.postDelayed(new Runnable() {
                    public void run() {
                        managerCompat.cancel(100);
                    }
                }, delayInMilliseconds);
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", textView.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();

                String text = textView.getText().toString();
                ClipData clipData = ClipData.newPlainText("text", text);
                clipboard.setPrimaryClip(clipData);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ClipData emptyClipData = ClipData.newPlainText("", "");
                        clipboard.setPrimaryClip(emptyClipData);
                    }
                }, 30000); // Delete clipboard contents after 30 seconds

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager = v.getContext().getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }


                if (v.getContext() instanceof Activity) {
                    ((Activity) v.getContext()).finish();
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Handle long-press event for the item
                showPopupMenu(view, position);
                return true;
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Handle long-press event for the item
                showPopupMenu(view, position);
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class DataHolder extends RecyclerView.ViewHolder {
        private RecyclerviewRowBinding binding;

        public DataHolder(RecyclerviewRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void openAddDataActivity(Context context, int position) {
        Intent intent = new Intent(context, AddDataActivity.class);
        intent.putExtra("dataId", arrayList.get(position).id);
        intent.putExtra("info", "old");
        context.startActivity(intent);
    }

    private void showPopupMenu(View view, int position) {
        Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator.hasVibrator()) {
            // Vibrate for 100 milliseconds
            vibrator.vibrate(100);
        }
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.example_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        // Handle edit menu item click
                        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Please Verify")
                                .setDescription("User Authentication is required to proceed")
                                .setDeviceCredentialAllowed(true)
                                .build();
                        BiometricPrompt biometricPrompt = getPrompt1(view.getContext(), position);

                        if (biometricPrompt != null) {
                            biometricPrompt.authenticate(promptInfo);
                        } else {
                            Intent intent = new Intent(view.getContext(), AddDataActivity.class);
                            intent.putExtra("dataId", arrayList.get(position).id);
                            intent.putExtra("info", "old");
                            view.getContext().startActivity(intent);
                        }

                        return true;
                    default:
                        return false;
                }
            }
        });
        // Show the PopupMenu
        popupMenu.show();
    }

    private BiometricPrompt getPrompt1(Context context, int position) {
        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser(context, errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser(context, "Authentication Succeeded!");

                openAddDataActivity(context, position);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser(context, "Authentication Failed!");
            }
        };
        BiometricPrompt biometricPrompt;

        if (context instanceof MainActivity) {
            biometricPrompt = new BiometricPrompt((MainActivity) context, executor, callback);
        } else {
            // Handle the case when the context is not an instance of MainActivity
            // For example, show an error message or fallback to a different authentication method
            biometricPrompt = null; // Set biometricPrompt to an appropriate value
        }

        return biometricPrompt;

    }

    private void notifyUser(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

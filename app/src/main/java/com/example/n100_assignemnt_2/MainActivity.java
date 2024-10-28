package com.example.n100_assignemnt_2;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseHelper firebaseHelper;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseHelper = new FirebaseHelper();
        taskAdapter = new TaskAdapter();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);


        // Observe tasks from Firebase
        firebaseHelper.getTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                taskAdapter.setTasks(tasks);  // Set tasks to RecyclerView adapter
            }
        });

        firebaseHelper.getTasks().observe(this, tasks -> {
            if (tasks != null) {
                taskAdapter.setTasks(tasks);  // Update the RecyclerView with new tasks
            }
        });


        // Example usage of CRUD methods
        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.addTask("New Task Description", "High");
                Toast.makeText(MainActivity.this, "Task added", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.updateTask("taskIdToUpdate", "Updated Description", "Medium");
                Toast.makeText(MainActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.deleteTask("taskIdToDelete");
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

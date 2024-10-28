package com.example.n100_assignemnt_2;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {
    private DatabaseReference database;
    private MutableLiveData<List<Task>> tasksLiveData;

    public FirebaseHelper() {
        database = FirebaseDatabase.getInstance().getReference("tasks");
        tasksLiveData = new MutableLiveData<>();
        fetchTasks(); // Fetch tasks to initialize LiveData
    }


    // READ: Retrieve tasks as LiveData (sorted by priority)
    public LiveData<List<Task>> getTasks() {
        return tasksLiveData;
    }

    private void fetchTasks() {
        database.orderByChild("priority").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                tasksLiveData.setValue(taskList);  // Update LiveData with the sorted list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseHelper", "Data read cancelled or failed", error.toException());
            }
        });
    }

    // UPDATE an existing task
    public void updateTask(String taskId, String newDescription, String newPriority) {
        DatabaseReference taskRef = database.child(taskId);
        taskRef.child("description").setValue(newDescription);
        taskRef.child("priority").setValue(newPriority);
    }
    public void addTask(String description, String priority) {
        String taskId = database.push().getKey();  // Generates a unique ID
        Task task = new Task(taskId, description, priority);

        if (taskId != null) {
            database.child(taskId).setValue(task).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    Log.d("FirebaseHelper", "Task added successfully");
                } else {
                    Log.e("FirebaseHelper", "Task addition failed", task1.getException());
                }
            });
        }
    }

    // DELETE a task by its ID
    public void deleteTask(String taskId) {
        database.child(taskId).removeValue();
    }
}

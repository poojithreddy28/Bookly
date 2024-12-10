package com.example.bookly;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UpdateBookshelfActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String userId;
    private RecyclerView bookshelfRecyclerView;
    private InlineEditBookshelfAdapter adapter;
    private List<Book> bookshelfBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bookshelf);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        bookshelfBooks = new ArrayList<>();

        bookshelfRecyclerView = findViewById(R.id.bookshelfRecyclerView);
        bookshelfRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InlineEditBookshelfAdapter(this, bookshelfBooks, this::updateBookInFirestore);
        bookshelfRecyclerView.setAdapter(adapter);

        fetchBookshelfBooks();
    }

    private void fetchBookshelfBooks() {
        db.collection("users")
                .document(userId)
                .collection("bookshelf")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        bookshelfBooks.clear();
                        bookshelfBooks.addAll(task.getResult().toObjects(Book.class));
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(UpdateBookshelfActivity.this, "Error fetching books", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error fetching books", task.getException());
                    }
                });
    }

    private void updateBookInFirestore(Book book) {
        db.collection("users")
                .document(userId)
                .collection("bookshelf")
                .document(book.getTitle())
                .update("genre", book.getGenre())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Updated book: " + book.getTitle(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating book", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error updating book", e);
                });
    }
}
package com.example.bookly;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewBookshelfActivity extends AppCompatActivity {

    private RecyclerView bookshelfRecyclerView;
    private BookshelfAdapter bookshelfAdapter;
    private List<Book> bookshelfBooks;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookshelf);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bookshelfRecyclerView = findViewById(R.id.bookshelfRecyclerView);
        bookshelfRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookshelfBooks = new ArrayList<>();
        bookshelfAdapter = new BookshelfAdapter(this, bookshelfBooks);
        bookshelfRecyclerView.setAdapter(bookshelfAdapter);

        fetchBookshelfBooks();
    }

    private void fetchBookshelfBooks() {
        db.collection("users")
                .document(userId)
                .collection("bookshelf")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);
                            bookshelfBooks.add(book);
                        }
                        bookshelfAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ViewBookshelfActivity.this, "Error fetching bookshelf books", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error fetching bookshelf books", task.getException());
                    }
                });
    }
}
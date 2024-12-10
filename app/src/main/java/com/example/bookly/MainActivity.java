package com.example.bookly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private RequestQueue requestQueue;
    private static final String TAG = "GoogleBooksAPI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        booksRecyclerView = findViewById(R.id.booksRecyclerView);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(this, bookList);
        booksRecyclerView.setAdapter(bookAdapter);

        requestQueue = Volley.newRequestQueue(this);

        fetchUsername();
        fetchBooksFromGoogleBooksAPI();
        fetchBooksFromFirestore();

        Button viewBookshelfButton = findViewById(R.id.viewBookshelfButton);
        Button updateBookshelfButton = findViewById(R.id.updateBookshelfButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Navigate to View and Update Bookshelf Activities
        viewBookshelfButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewBookshelfActivity.class);
            startActivity(intent);
        });

        updateBookshelfButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpdateBookshelfActivity.class);
            startActivity(intent);
        });

        // Logout functionality
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void fetchUsername() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String firstName = task.getResult().getString("firstName");
                        String lastName = task.getResult().getString("lastName");

                        if (firstName != null && lastName != null) {
                            String username = firstName + " " + lastName;
                            TextView greetingTextView = findViewById(R.id.greetingTextView);
                            greetingTextView.setText("Welcome, " + username + "!");
                        }
                    }
                });
    }

    private void fetchBooksFromGoogleBooksAPI() {
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=subject:fiction&maxResults=10&key=AIzaSyCwBBNHgJctTwc4vjArSzake2BcrRmJuJE";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                response -> {
                    try {
                        JSONArray items = response.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject bookObject = items.getJSONObject(i);
                            JSONObject volumeInfo = bookObject.getJSONObject("volumeInfo");

                            String title = volumeInfo.optString("title", null);
                            if (title == null || title.isEmpty()) {
                                Log.e(TAG, "Skipping book with no title.");
                                continue;
                            }

                            String authors = volumeInfo.has("authors") ? volumeInfo.getJSONArray("authors").getString(0) : "Unknown Author";
                            String description = volumeInfo.optString("description", "No Description Available");
                            String imageUrl = volumeInfo.has("imageLinks") ? volumeInfo.getJSONObject("imageLinks").optString("thumbnail", "") : "";

                            // Create a Book object
                            Book book = new Book(title, authors, "Unknown Genre", description, imageUrl, false);

                            // Add book to local list and save to Firestore
                            bookList.add(book);
                            saveBookToFirestore(book);
                        }
                        bookAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JSON", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching books: " + error.getMessage());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Response Code: " + error.networkResponse.statusCode);
                    }
                });

        requestQueue.add(request);
    }

    private void saveBookToFirestore(Book book) {
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            Log.e("Firestore", "Skipping book with no title.");
            return;
        }

        db.collection("books")
                .add(book)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Book added: " + book.getTitle()))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding book", e));
    }

    private void fetchBooksFromFirestore() {
        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);
                            bookList.add(book);
                        }
                        bookAdapter.notifyDataSetChanged();
                    }
                });
    }
}
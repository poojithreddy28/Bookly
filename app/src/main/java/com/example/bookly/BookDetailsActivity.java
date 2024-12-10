package com.example.bookly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookDetailsActivity extends AppCompatActivity {

    private TextView bookTitleTextView, bookAuthorTextView, bookDescriptionTextView;
    private ImageView bookImageView;
    private Button addToBookshelfButton;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bookTitleTextView = findViewById(R.id.bookTitleTextView);
        bookAuthorTextView = findViewById(R.id.bookAuthorTextView);
        bookDescriptionTextView = findViewById(R.id.bookDescriptionTextView);
        bookImageView = findViewById(R.id.bookImageView);
        addToBookshelfButton = findViewById(R.id.addToBookshelfButton);

        Book book = (Book) getIntent().getSerializableExtra("book");

        if (book != null) {
            bookTitleTextView.setText(book.getTitle());
            bookAuthorTextView.setText("Author: " + book.getAuthor());

            // Truncate description to 30 words
            String truncatedDescription = truncateDescription(book.getDescription(), 30);
            bookDescriptionTextView.setText(truncatedDescription);

            // Load image with Glide
            Glide.with(this)
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.splash_book) // Add a placeholder image in your `res/drawable` folder
                    .error(R.drawable.splash_book) // Add an error image in your `res/drawable` folder
                    .into(bookImageView);

            addToBookshelfButton.setOnClickListener(v -> {
                db.collection("users")
                        .document(userId)
                        .collection("bookshelf")
                        .document(book.getTitle())
                        .set(book)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(BookDetailsActivity.this, book.getTitle() + " added to your bookshelf!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BookDetailsActivity.this, "Failed to add book to bookshelf", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }
    }

    private String truncateDescription(String description, int maxWords) {
        String[] words = description.split("\\s+");
        if (words.length <= maxWords) {
            return description;
        }
        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < maxWords; i++) {
            truncated.append(words[i]).append(" ");
        }
        return truncated.toString().trim() + "...";
    }
}
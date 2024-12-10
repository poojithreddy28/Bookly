package com.example.bookly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.BookshelfViewHolder> {

    private Context context;
    private List<Book> bookshelfBooks;
    private FirebaseFirestore db;
    private String userId;

    public BookshelfAdapter(Context context, List<Book> bookshelfBooks) {
        this.context = context;
        this.bookshelfBooks = bookshelfBooks;
        this.db = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public BookshelfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookshelf_item, parent, false);
        return new BookshelfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookshelfViewHolder holder, int position) {
        Book book = bookshelfBooks.get(position);

        holder.bookTitleTextView.setText(book.getTitle());
        holder.bookAuthorTextView.setText("Author: " + book.getAuthor());
        Glide.with(context).load(book.getImageUrl()).into(holder.bookImageView);

        // Set initial read/unread status
        holder.readStatusButton.setText(book.isRead() ? "Mark as Unread" : "Mark as Read");

        // Handle read/unread toggle
        holder.readStatusButton.setOnClickListener(v -> {
            boolean newStatus = !book.isRead();
            book.setRead(newStatus);
            holder.readStatusButton.setText(newStatus ? "Mark as Unread" : "Mark as Read");

            // Update Firestore
            db.collection("users")
                    .document(userId)
                    .collection("bookshelf")
                    .document(book.getTitle())
                    .update("read", newStatus)
                    .addOnSuccessListener(aVoid -> {
                        String statusMessage = newStatus ? "Marked as Read" : "Marked as Unread";
                        Toast.makeText(context, book.getTitle() + " " + statusMessage, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error updating status", Toast.LENGTH_SHORT).show());
        });

        // Handle Remove from Bookshelf
        holder.removeButton.setOnClickListener(v -> {
            db.collection("users")
                    .document(userId)
                    .collection("bookshelf")
                    .document(book.getTitle())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        bookshelfBooks.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, bookshelfBooks.size());
                        Toast.makeText(context, book.getTitle() + " removed from your bookshelf!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error removing book", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return bookshelfBooks.size();
    }

    static class BookshelfViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitleTextView, bookAuthorTextView;
        ImageView bookImageView;
        Button readStatusButton, removeButton;

        public BookshelfViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            bookImageView = itemView.findViewById(R.id.bookImageView);
            readStatusButton = itemView.findViewById(R.id.readStatusButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
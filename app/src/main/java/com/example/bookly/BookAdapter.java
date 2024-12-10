package com.example.bookly;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;

    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookTitleTextView.setText(book.getTitle());
        holder.bookAuthorTextView.setText("Author: " + book.getAuthor());

        // Load the book cover image using Glide
        Glide.with(context)
                .load(book.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.splash_book) // Placeholder image
                        .error(R.drawable.splash_book)             // Fallback image
                        .centerCrop())                             // Scale type
                .into(holder.bookImageView);

        // Set item click listener to open BookDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailsActivity.class);
            intent.putExtra("book", book); // Pass the selected book object to the details activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitleTextView, bookAuthorTextView;
        ImageView bookImageView; // ImageView for book cover

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            bookImageView = itemView.findViewById(R.id.bookImageView); // Initialize ImageView
        }
    }
}
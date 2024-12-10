package com.example.bookly;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class InlineEditBookshelfAdapter extends RecyclerView.Adapter<InlineEditBookshelfAdapter.ViewHolder> {

    private Context context;
    private List<Book> bookshelfBooks;
    private BookUpdateListener bookUpdateListener;

    public InlineEditBookshelfAdapter(Context context, List<Book> bookshelfBooks, BookUpdateListener bookUpdateListener) {
        this.context = context;
        this.bookshelfBooks = bookshelfBooks;
        this.bookUpdateListener = bookUpdateListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inline_edit_bookshelf_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookshelfBooks.get(position);

        Glide.with(context).load(book.getImageUrl()).into(holder.bookImageView);
        holder.bookTitleTextView.setText(book.getTitle());
        holder.bookAuthorTextView.setText(book.getAuthor());
        holder.genreEditText.setText(book.getGenre());

        // Update genre in real-time as user types
        holder.genreEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                book.setGenre(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save button to update book in Firestore
        holder.saveButton.setOnClickListener(v -> bookUpdateListener.onBookUpdated(book));
    }

    @Override
    public int getItemCount() {
        return bookshelfBooks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImageView;
        TextView bookTitleTextView, bookAuthorTextView;
        EditText genreEditText;
        Button saveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImageView = itemView.findViewById(R.id.bookImageView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            bookAuthorTextView = itemView.findViewById(R.id.bookAuthorTextView);
            genreEditText = itemView.findViewById(R.id.genreEditText);
            saveButton = itemView.findViewById(R.id.saveButton);
        }
    }

    public interface BookUpdateListener {
        void onBookUpdated(Book book);
    }
}
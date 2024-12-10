package com.example.bookly;

import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String author;
    private String genre;
    private String description;
    private String imageUrl;
    private boolean read; // New property for tracking read/unread status

    // Default constructor required for Firebase Firestore deserialization
    public Book() {}

    // Constructor with all properties
    public Book(String title, String author, String genre, String description, String imageUrl, boolean read) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.imageUrl = imageUrl;
        this.read = read;
    }

    public Book(String title, String authors, String unknownGenre, String description, String imageUrl) {
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isRead() { return read; } // Getter for read status
    public void setRead(boolean read) { this.read = read; } // Setter for read status
}
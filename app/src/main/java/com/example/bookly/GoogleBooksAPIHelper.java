package com.example.bookly;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleBooksAPIHelper {
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    public static List<Book> fetchBooks(String query) {
        List<Book> books = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        try {
            String url = GOOGLE_BOOKS_API_URL + query + "&maxResults=12";
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();

            JsonObject jsonResponse = JsonParser.parseString(responseData).getAsJsonObject();
            JsonArray items = jsonResponse.getAsJsonArray("items");

            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                    String title = volumeInfo.get("title").getAsString();
                    String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown Author";
                    String description = volumeInfo.has("description") ? volumeInfo.get("description").getAsString() : "No description available";
                    String imageUrl = volumeInfo.has("imageLinks") ? volumeInfo.getAsJsonObject("imageLinks").get("thumbnail").getAsString() : "";

                    books.add(new Book(title, authors, "Unknown Genre", description, imageUrl));
                }
            }
        } catch (Exception e) {
            Log.e("GoogleBooksAPI", "Error fetching books", e);
        }

        return books;
    }
}
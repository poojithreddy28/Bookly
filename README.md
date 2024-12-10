
# 📚 Bookly - Personal Book Library Management

## 📖 App Description

Bookly is a mobile application designed to help users organize and manage their reading journey effectively. The app allows users to:

- 🔍 **Browse books** using the Google Books API.
- 💾 **Save books** to their personal bookshelf in the Firebase Firestore cloud storage.
- 📑 **View detailed information** about each book, including title, author, description, and cover image.
- 🎨 **Enjoy a clean, modern interface** for seamless navigation.

---

## 🔗 APIs Used

- **Firebase Authentication API**: Enables secure user login, signup, and authentication using email/password or other identity providers.
- **Google Books API**: Fetches book details such as titles, authors, descriptions, and cover images.
- **Firebase Firestore**: Securely stores and manages user data and bookshelves.

---

## 🛠 Technologies Used

- **Android Studio/Java**: Used for developing a responsive and user-friendly mobile application.
- **Firebase**: For authentication and cloud storage of user data.
- **Google Books API**: To fetch book-related details.

---

## 📋 Workflow Diagram of Bookly

```plaintext
Splash Screen → Login Page → Signup Page → Home Screen → View Bookshelf → Update Bookshelf
```

---

## 🌟 Activity Screens

- **Splash Screen**: Engages the user with a visually appealing introduction.
- **Login/Signup Pages**: Secure user authentication using Firebase Authentication.
- **Home Screen**: Displays a collection of books retrieved via the Google Books API.
- **Bookshelf**: A personalized storage area for user-selected books.
- **Detailed Book View**: Displays in-depth details about a book, including a truncated description.

---

## 🐞 Problems Encountered

1. **Integration with Google Books API**
   - **Issue**: Challenges in handling invalid or incomplete responses (e.g., missing titles, descriptions, or authors).
   - **Solution**: Implemented data validation and fallback logic to ensure the app didn’t crash or display blank data.

2. **Cleartext Traffic Issue**
   - **Issue**: Images from the API weren’t loading due to restrictions on cleartext HTTP traffic.
   - **Solution**: Configured a `network_security_config` file to allow HTTP connections for specific domains.

3. **Firebase Firestore Integration**
   - **Issue**: Synchronizing user data with Firestore, especially when updating or saving bookshelf items.
   - **Solution**: Optimized Firestore queries and used real-time updates to minimize latency.

4. **Handling Large Descriptions**
   - **Issue**: Long book descriptions cluttered the UI.
   - **Solution**: Truncated descriptions to 30 words with a “Read More” option.

---

## 🚀 Future Improvements

1. **Email Verification for User Accounts**: Add an email verification system during sign-up to ensure account authenticity.
2. **Advanced Book Search with Filters**: Allow users to filter books by title, author, genre, publication year, and user reviews.
3. **Offline Access to Bookshelf**: Enable offline viewing of bookshelves by caching data locally.
4. **Push Notifications**: Add notifications to remind users about unfinished books or suggest new arrivals.
5. **Customizable User Profiles**: Allow users to personalize profiles with avatars, bios, and preferred genres.

---


## 📂 Folder Structure

- `lib/`: Contains Dart source files for Flutter development.
- `assets/`: Stores images and resources for the application.
- `firebase/`: Contains Firebase configuration files.

---


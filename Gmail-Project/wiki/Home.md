# Pigeon-UI

## 📨 Overview

Welcome to the official documentation for **PigeonUI**, an Android-based email client inspired by Gmail.  
This project replicates the functionality and sleek design of Gmail, offering real-time communication, message labeling, and a modern dark mode interface.  
Developed using **Android Studio** with **Java**, **MVVM architecture**, and a **Node.js + MongoDB backend**, PigeonUI is built for smooth, responsive interaction across devices.

---

## 🌟 Key Features

### User Authentication
Secure signup and login using JWT-based sessions, with full validation both client and server side.

### Inbox & Email Management
Fully functional inbox, sent, and drafts views.  
Supports reading, composing, sending, and deleting emails.

### Labeling System
Custom label assignment to emails (e.g., Work, Personal, Spam).

### Dark Mode Support
Seamless theme switching between light and dark modes, including UI color adjustments, icons, and logos.

### Message Preview & Sorting
Email previews show subject, sender, and snippet.  
Sort by time, read/unread, or label.

### Responsive Design & Accessibility
Built to match Gmail UX expectations while offering customization.

### Local Caching
Supports Room DB to cache data for better offline support and performance.

---

## 🛠️ Tech Stack

### Backend
- Node.js with Express.js
- MongoDB (Mongoose ORM)
- JWT for authentication
- RESTful API structure
- Docker for containerization and deployment
-
### Frontend (React Web App)
- React.js with functional components
- Axios for API communication
- 
### Android App (Frontend)
- Java with Android Studio
- MVVM Architecture
- Retrofit for API communication
- LiveData + ViewModel for reactive UI
- Room Database for local storage

---

## 🧱 Project Structure

This project follows a **modular MVVM architecture** on the Android side and **MVC architecture** on the backend.

## 🚀 Next Steps

- **[Getting Started](GettingStarted.md)** – Installation, setup, and configuration instructions
- **[Backend API](BackendAPI.md)** – API endpoints database structure & recommendation system info
- **[Android App Docs](AndroidApp.md)** – Mobile app details
- **[Frontend Documentation](Web.md)** - Web app details

## 👥 Contributors

- **Shirel Ben Baruch** 
- **Matan Marx**
- **Ofek Kaharizi**

Thank you for checking out our project!

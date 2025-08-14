# 🚀 Getting Started with PigeonUI

Welcome to the PigeonUI setup guide.  
This page will walk you through the prerequisites, installation, and configuration steps needed to get the **Android email client** and its **Node.js backend** up and running.

---

## Prerequisites

### 🔧 General Requirements
Make sure you have the following installed:

- Git – for cloning the repository
- Docker & Docker Compose – for easy deployment of backend services

### 📱 Android App Requirements
- Android Studio (latest stable version)
- Java 8+
- Gradle (managed automatically by Android Studio)

---

## Installation & Setup

### Step 1: Clone the Repository

```bash
    git clone https://github.com/matanmarx24/Gmail-Project-Part-5.git
    cd Gmail-Project-Part-5
```
### Step 2: Run the server
To run the project using Docker-Compose:
```
docker-compose up --build -d
```

### Step 3: Setup the Android App
- Open Android Studio
- Choose Open an Existing Project
- Select the pigeonui-android directory
- Wait for Gradle sync to complete
- Connect an Android device or start an emulator
- Click Run

## Next Steps
- **[Backend API](BackendAPI.md)** – API endpoints database structure & recommendation system info
- **[Frontend Documentation](Web.md)** - Web app details
- **[Android App Docs](AndroidApp.md)** – Mobile app details


You're all set! Enjoy building your Netflix-like streaming platform!
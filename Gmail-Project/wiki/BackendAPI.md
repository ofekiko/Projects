# BackendApi – PigeonUI

This document describes the backend architecture and API details of the **PigeonUI** email platform.  
It is built with **Node.js + Express.js**, follows the **MVC pattern**, and supports user authentication, mailbox actions, and RESTful API communication with the Android app.

---

## 🧰 Tech Stack

- **Node.js & Express.js** – API framework  
- **MongoDB** – NoSQL database for storing users and emails  
- **JWT Authentication** – Secure login and session management  
- **Multer** – For file uploads (e.g., profile pictures)  
- **Docker + Docker-Compose** – For deployment and container orchestration

---

## 🗂️ Project Structure

ApiServer/
├── controllers/       # Handles request logic for each route (e.g., login, register)
├── middleware/        # Custom middleware such as JWT authentication, error handling, file uploads
├── models/            # Mongoose schemas for users, emails, labels, etc.
├── routes/            # Defines Express routes and connects them to controllers
├── services/          # Business logic and helper functions (e.g., token generation, DB actions)
├── .env               # Environment variables for local or container use
├── app.js             # Main entry point that sets up the Express app and routes
├── Dockerfile         # Instructions to containerize the backend with Docker
├── package.json       # Project metadata and NPM dependencies
├── package-lock.json  # Exact version lock for dependencies (auto-generated)

BlackListServer/
├── data/              # Data files (e.g., blacklisted IPs, emails)
├── src/               # Source code for blacklist handling
├── Dockerfile         # Docker config for the blacklist microservice
├── server             # Executable or script to run the blacklist server

## 📡 Command Line Usage  (for the blacklist managment)

- `POST [URL]` | Adds a URL to the Bloom Filter and to the blacklist file. 
- `DELETE [URL]` | Removes a URL from the blacklist file (Bloom Filter is **not** changed).
- `GET [URL]` | Checks if the URL is in the blacklist.


## Tests for the Blacklist managment

In order to test the Blacklist managment you can run the dockerFileTests and see the output like the following

### Build the tests
```bash
docker build -f ./DockerfileTests -t bloomfilter-tests .
```

### Run the tests
```bash
docker run -it --rm bloomfilter-tests
```

<img src="src/images/tests_passed.png" width="500"/>

## API Endpoints – PigeonUI

### Authentication

- #### POST `/api/tokens`  
**Description:** Login as user and returns a JWT token.  
**Response:**
```json
{
  "token": "<jwt_token>"
}
```

### User Management

- #### POST `/api/users`
**Description:** Registers a new user account.
**Response:** 201 Created

- #### GET `/api/users/:id`
**Description:** Fetches a user's profile information from MongoDB.
**Response:** User object

### Mail Management

- #### GET `/api/mails`
**Description:** Retrieves all inbox emails for the authenticated user.  
**Response:** Returns last 50 mails.

---

- #### POST `/api/mails`
**Description:** Sends a new email (only if all fields are valid, including recipients)  
**Response:**  `201 Created`

---

- #### GET `/api/emails/:id`
**Description:** Retrieves a specific email by its ID.  
**Response:**  
Returns a single email object.

---
- #### GET `/api/mails/:id`  
**Description:** Retrieves a specific email by its unique ID.  
**Response:** A single email object.

---
- #### PATCH `/api/mails/:id`  
**Description:** Updates an existing email (e.g., subject, content, labels).  
**Response:**  Updated email object.  `200 OK`

---
- #### DELETE `/api/mails/:id`  
**Description:** Deletes an email by its ID.  
**Response:**  `204 No Content`

### Labels

- #### GET `/api/labels`  
**Description:** Returns all labels associated with the authenticated user.  
**Note:** Requires `x-user-id` header for identifying the user.  
**Response:**  Array of label objects.

---

- #### POST `/api/labels`  
**Description:** Creates a new label for the authenticated user.  
**Response:**  `201 Created`

---

- #### GET `/api/labels/:id`  
**Description:** Retrieves a specific label by its ID.  
**Response:**  Label object.

---

- #### PATCH `/api/labels/:id`  
**Description:** Updates the name or properties of a label.  
**Response:**  Updated label object.  `200 OK`

---

- #### DELETE `/api/labels/:id`  
**Description:** Deletes a label by its ID.  
**Response:**  `204 No Content`

---

### Blacklist

- #### POST `/api/blacklist`  
**Description:** Adds a new URL to the global blacklist (e.g., blocked senders or phishing links).  
**Request Body Example:**
```json
{
    curl -i -X POST http://localhost:8080/api/blacklist \
    -H "Content-Type: application/json" \
    -H "x-user-id: 1" \
    -d '{"url": "https://example.com"}'}
```

## Next Steps
- **[Frontend Documentation](Web.md)** - Web app details
- **[Android App Docs](AndroidApp.md)** – Mobile app details
const express = require('express')
const cookieParser = require('cookie-parser');
const { verifyToken } = require('./middleware/auth');
const cors = require('cors');
require('dotenv').config();
const mongoose = require('mongoose');

mongoose.connect(process.env.CONNECTION_STRING)
  .then(() => console.log("✅ MongoDB connected"))
  .catch(err => console.error("❌ MongoDB connection error:", err));

var app = express()

app.use(cookieParser());
app.use(express.json());

app.use(cors({
  origin: 'http://localhost:3000',
  credentials: true
}));

// Import route handlers.
const users = require('./routes/users')
const tokens = require('./routes/tokens')
const mails = require('./routes/mails')
const labels = require('./routes/labels')
const blacklist = require('./routes/blacklist')

// Register API routes.
app.use('/api/users', users)
app.use('/api/tokens', tokens)
// Public route – used by the frontend to verify auth status
app.get("/api/auth/validate", (req, res) => {
  const token = req.cookies.token;

  if (!token) return res.sendStatus(401);

  try {
    const jwt = require('jsonwebtoken');
    const payload = jwt.verify(token, process.env.JWT_SECRET);
    res.sendStatus(200); 
  } catch {
    res.sendStatus(401); 
  }
});

app.use(verifyToken);
app.use('/api/mails', mails)
app.use('/api/labels', labels)
app.use('/api/blacklist', blacklist)

// Start the server on port 8080.
app.listen(8080, '0.0.0.0', () => {
  console.log("✅ Server running on http://0.0.0.0:8080");
});

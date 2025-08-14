const express = require('express');
const router = express.Router();
const users = require('../controllers/users');
const { verifyToken } = require('../middleware/auth');
router.get('/check-username', users.checkUsername);

// Get current user details from token
router.get('/me', verifyToken, users.getMe);

// Register a new user
router.post('/', users.register);

// Get user details by user ID
router.get('/:id', users.getUser);

// Get user details by user email
router.get('/mail/:mail', users.getUserByMail);

// Logout
router.post('/logout', users.logout);


module.exports = router;

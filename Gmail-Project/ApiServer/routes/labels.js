const express = require('express');
const router = express.Router();
const labelsController = require('../controllers/labels');
const { verifyToken } = require('../middleware/auth');

// Create a new label for a user
router.post('/', verifyToken, labelsController.addLabel);

// Get all labels that belong to a specific user (user ID is in headers)   
router.get('/', verifyToken, labelsController.getLabelsByUserId)

// Get a specific label by its ID
router.get('/:id', verifyToken, labelsController.getLabelById);

// Update the name of a specific label by its ID
router.patch('/:id', verifyToken, labelsController.updateLabelName);

// Delete a specific label by its ID
router.delete('/:id', verifyToken, labelsController.deleteLabel);

module.exports = router;

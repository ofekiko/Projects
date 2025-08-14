const express = require('express')
var router = express.Router()
const blacklist = require('../controllers/blacklist')

// Route to delete a specific URL from the Blacklist.
router.delete('/:id', blacklist.deleteUrl)

// Route to add a specific URL to the Blacklist.
router.post('/', blacklist.addUrl) 


module.exports = router
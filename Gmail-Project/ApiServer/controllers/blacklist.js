const blacklist = require('../models/blacklist')
const users = require('../models/users');

const addUrl = async (req, res) => {

    const userId = req.user.id;
    // Check if userId or name is missing
    if (userId == null) {
        return res.status(400).json({ error: 'Missing userId' });
    }

    const user = users.getUserById(userId);
    if (!user) {
        return res.status(404).json({ error: 'User not found' });
    }

    // Get the URL from the request body.
    const url = req.body.url

    // Send the URL to be added to the Blacklist.
    const output = await blacklist.addUrl(url)

    // If the URL was successfully added to the Blacklist (valid URL).
    if (output === '201 Created') 
    {
        return res.status(201).end()
    }

    // If the provided URL is not valid.
    else
        return res.status(400).json({ error: 'This is not a valid URL' })

}

const deleteUrl = async (req, res) => {

    const userId = req.user.id;
    // Check if userId or name is missing
    if (userId == null) {
        return res.status(400).json({ error: 'Missing userId' });
    }

    const user = users.getUserById(userId);
    if (!user) {
        return res.status(404).json({ error: 'User not found' });
    }
    // Get the URL from the request body.
    const url = req.params.id

    // Send the URL to be removed from the Blacklist.
    const output = await blacklist.deleteUrl(url)

    // If the URL was successfully removed (i.e., it was valid and existed in the Blacklist).
    if (output === '204 No Content') 
        return res.status(204).end() 

    // If the provided URL is not valid.
    else if (output === '400 Bad Request')
        return res.status(400).json({ error: 'This is not a valid URL' })
    
    // If the URL does not exist in the Blacklist.
    else
        return res.status(404).json({ error: 'URL Not Found' })
    
}

module.exports = { addUrl, deleteUrl }


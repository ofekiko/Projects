const Mail = require('../models/mails');
const { getUserById} = require('../models/users');
const { checkIfEmail } = require('../models/mails');
const { getLabelByLabelName } = require('../models/labels');
const mongoose = require ('mongoose');

// Gets the latest 50 mail from the latest mail
exports.get50LatestMails = async (req, res) => {
    // Gets the user id from the user and check if the user does indeed exist
    const userId = req.user.id;
    const user = await getUserById(userId);
    if (!user) { return res.status(400).json({ error: 'Please provide a valid User ID' }); }
    // Prints the last 50 mails of the user
    const mails = await Mail.get50LatestMails(userId);
    return res.json(mails);
}

// Gets all the mails in the a certain box
exports.getMailsInBox = async (req, res) => {
    // Gets the user id from the user and check if the user does indeed exist
    const userId = req.user.id;                 
    const user = await getUserById(userId);
    // Gets the box from the user and check if the box does indeed exist
    const box = req.params.box;
    if (!user) { return res.status(400).json({ error: 'Please provide a valid User ID' }); }
    if (!box || typeof box !== 'string') {
        return res.status(400).json({ error: 'Please provide a valid box' });
    }
    // Prints all the mails in the certain box
    const mails = await Mail.getMailsInBox(box, userId);
    return res.json(mails);
}

// Gets a mail by it's ID
exports.getMailById = async (req, res) => {
    // Gets the user id from the user and check if the user does indeed exist
    const userId = req.user.id;
    const user = await getUserById(userId);
    if (!user) { return res.status(400).json({ error: 'Please provide a valid User ID' }); }
    // Gets the mail id from the user and check if the input is valid
    const id = req.params.id;
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).json({ error: 'Invalid Mail ID format.' });
    }

    // Check if the mail exists and prints it
    const mail = await Mail.getMailById(id, userId);
    if (!mail) { return res.status(404).json({ error: 'Mail not found' }); }
    return res.json(mail);
}

// Creates a mail
exports.createMail = async (req, res) => {
    // Gets the user id from the user and check if the user does indeed exist
    const userId = req.user.id;
    const user = await getUserById(userId);
    if (!user) { return res.status(400).json({ error: 'Please provide a valid User ID' }); }
    // Gets the variables from the user and checks if they are valid, if not, print a fitting error and change the status
    const { title, content, recipientsEmails, toSend = false } = req.body;
    if (typeof title !== 'string') { return res.status(400).json({ error: 'Title should be string' }); }
    if (typeof content !== 'string') { return res.status(400).json({ error: 'Content should be string' }); }
    if (!Array.isArray(recipientsEmails)) { return res.status(400).json({ error: 'Recipients should be an array' }); }
    if (toSend) {
        for (const email of recipientsEmails) {
            if (!checkIfEmail(email)) {
                return res.status(400).json({ error: 'Emails should be in the correct format' });
            }
        }
    }

    if (typeof toSend !== 'boolean') { return res.status(400).json({ error: 'toSend shpuld be boolean' }); }
    // Creates a mail with the variables and check if it contains a Blacklisted URL
    const newMail = await Mail.createMail(title, content, userId, recipientsEmails, toSend);
    if (newMail === 2) { return res.status(400).json({ error: 'Please provide atleast one recipient' }); }
    if (newMail === 3) { return res.status(400).end(); }
    res.set('Location', `/api/mails/${newMail.id}`);
    return res.status(201).json(newMail);
}

// Deletes a mail
exports.deleteMail = async (req, res) => {
    // Checks if the ID inputed is valid
    const id = req.params.id;
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).json({ error: 'Invalid Mail ID format.' });
    }
    // Gets the user id from the user and check if the user does indeed exist
    const userId = req.user.id;
    const user = await getUserById(userId);
    if (!user) { return res.status(400).json({ error: 'Please provide a valid User ID' }); }
    // Deletes the mail if found, if not print an error
    const deleted = await Mail.deleteMail(id, userId);
    if (!deleted) { return res.status(404).json({ error: 'Mail not found' }); }
    return res.status(204).end();
}

// Updates a draft mail
exports.updateMail = async (req, res) => {
    // Checks if the ID inputed is valid
    const id = req.params.id;
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).json({ error: 'Invalid Mail ID format.' });
    }
    // Check if there are updates provided
    const updates = req.body;
    if (!updates) {
        return res.status(400).json({ error: 'Please provide updates' });
    }
    // Checks if the user provided was valid
    const userId = req.user.id;
    const user = await getUserById(userId);
    if (!user) {
        return res.status(400).json({ error: 'Please provide a valid User ID' });
    }

    // Checks for valid recipients and if the mail contains blacklisted URLs
    if (updates.toSend === true) {
        if (!Array.isArray(updates.recipientsEmails) || updates.recipientsEmails.length === 0) {
            return res.status(400).json({ error: 'At least one recipient is required to send the mail' });
        }
        const invalidEmails = updates.recipientsEmails.filter(email => !checkIfEmail(email));
        if (invalidEmails.length > 0) {
            return res.status(400).json({ error: 'One or more recipient emails are invalid' });
        }
        if (Mail.containsBlacklistedUrl && Mail.containsBlacklistedUrl(updates.content)) {
            return res.status(400).json({ error: 'Email content contains blacklisted URLs' });
        }
    }
    // Checks if the mail was updated successfully
    const updatedMail = await Mail.updateMail(id, userId, updates);
    if (!updatedMail) {
        return res.status(404).json({ error: 'Mail not found' });
    }
    if (updatedMail === 2) {
        return res.status(400).json({ error: 'Mail already sent' });
    }
    if (updatedMail === 3) {
        return res.status(400).json({ error: 'Please provide valid Recipients emails' });
    }
    return res.status(200).json(updatedMail);
}

// Gets the mails that contain the query that was provided
exports.searchMail = async (req, res) => {
    const query = req.params.query;
    const userId = req.user?.id;
    const box = req.query.box;

    if (!userId) return res.status(401).json({ error: 'Unauthorized' });

    const user = await getUserById(userId);
    if (!user) return res.status(400).json({ error: 'Invalid user' });

    if (!query || typeof query !== 'string') {
        return res.status(400).json({ error: 'Invalid query' });
    }

    let matchingMails = await Mail.searchMail(query, userId);

    // If it was requested to search in a certain box
    if (box && typeof box === 'string') {
        matchingMails = matchingMails.filter(mail => mail.box === box);
    }

    // Removed duplicates 
    const unique = Array.from(new Map(matchingMails.map(m => [m.id, m])).values());

    return res.status(200).json(unique);
}

// Gets mails matching query in a specific box
exports.searchMailInBox = async (req, res) => {
    // Gets the query from the user to check what mails contain it
    const query = req.params.query;
    const box = req.params.box;
    // Gets the user id from the user and check if the user does indeed exist
    const userId = req.user.id;
    const user = await getUserById(userId);
    if (!user) { return res.status(400).json({ error: 'Please provide a valid User ID' }); }
    // Checks if query was inputed and if its a string
    if (!query || typeof query !== 'string') { return res.status(400).json({ error: 'Please provide a valid query' }); }
    if (!box || typeof box !== 'string' || !['Inbox', 'Trash', 'Spam', 'Drafts', 'Sent'].includes(box)) {
        return res.status(400).json({ error: 'Please provide a valid box' });
    }
    const matchingMails = await Mail.searchMail(query, box, userId);
    return res.status(200).json(matchingMails);
}

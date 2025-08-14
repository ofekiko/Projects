const Mail = require('./Mail'); // Import the new Mail model
const mongoose = require('mongoose');

const { connectBlackList, handleBlackList } = require('../services/blacklist')
const { getFirstNameById, getLastNameById, getEmailById, findByEmail } = require('./users')
// Puts all the mails of the user into one array and then take the last 50 and flip their order
const get50LatestMails = async (userId) => {
    const mailsOfUser = await Mail.find({ userId: new mongoose.Types.ObjectId(userId) })
        .sort({ sentAt: -1, createdAt: -1 })
        .limit(50);
    return mailsOfUser;
}

// Gets all the mails in the a certain box
const getMailsInBox = async (box, userId) => {
    if (!mongoose.Types.ObjectId.isValid(userId)) {
        console.error("getMailsInBox: Invalid userId format");
        return [];
    }
    const mailsInBox = await Mail.find({ userId: new mongoose.Types.ObjectId(userId), box: box });
    return mailsInBox;
};

// Searches a mail by its id and return it
const getMailById = async (id, userId) => {
    if (!mongoose.Types.ObjectId.isValid(id) || !mongoose.Types.ObjectId.isValid(userId)) {
        return null; // Or throw an error
    }
    const mail = await Mail.findOne({
        _id: new mongoose.Types.ObjectId(id),
        userId: new mongoose.Types.ObjectId(userId)
    });
    if (mail && !mail.read) { // Only mark as read and save if it wasn't already
        mail.read = true;
        await mail.save();
    }
    return mail;
};

// creates a mail and checks if it contains a Blacklisted URL
const createMail = async (title, content, senderUserId, recipientsEmails, toSend) => {
    // Ensure senderUserId is a valid ObjectId
    if (!mongoose.Types.ObjectId.isValid(senderUserId)) {
        console.error("Invalid senderUserId provided:", senderUserId);
        return null;
    }

    const authorEmail = await getEmailById(senderUserId);
    const authorFirstName = await getFirstNameById(senderUserId);
    const authorLastName = await getLastNameById(senderUserId);

    // Checks if the mail contains a Blacklisted URL
    const isBlacklisted = await checkIfBlacklisted(title, content);

    const newMailData = {
        title,
        content,
        userId: senderUserId,
        authorId: senderUserId,
        author: authorEmail,
        authorFirstName: authorFirstName,
        authorLastName: authorLastName,
        recipientsEmails,
        toSend,
        read: true,
        box: null,
        isBlacklisted: isBlacklisted,
        isSenderCopy: true
    };

    if (!toSend) {
    newMailData.sentAt = new Date(); }

    if (newMailData.toSend) {
        if (recipientsEmails.length === 0) { return 2; }
        newMailData.box = 'Sent';
        const createdMail = await Mail.create(newMailData);
        createdMail.sentAt = await sendMail(createdMail);
        await createdMail.save();
        return createdMail;
    } else {
        if (!recipientsEmails.length && !content && !title) { return 3; }
        newMailData.box = 'Drafts';
        newMailData.read = true; 
        newMailData.isSenderCopy = true;
        const draftMail = await Mail.create(newMailData);
        return draftMail;
    }
};

// Sends the mail to all the recipients in the recipientsEmails variable
const sendMail = async (mail) => {
    const time = new Date(); // Use Date object for sentAt

    let newBoxForRecipients = 'Inbox';
    if (mail.isBlacklisted) {
        newBoxForRecipients = 'Spam';
    }

    // Iterate over recipients and create copies
    for (const email of mail.recipientsEmails) {
        const recipientUser = await findByEmail(email); 
        if (recipientUser) {
            const copyMailData = {
                title: mail.title,
                content: mail.content,
                sentAt: time,
                userId: recipientUser._id,
                authorId: mail.authorId,
                author: mail.author,
                authorFirstName: mail.authorFirstName,
                authorLastName: mail.authorLastName,
                recipientsEmails: mail.recipientsEmails,
                toSend: true,
                read: false,
                box: newBoxForRecipients,
                isBlacklisted: mail.isBlacklisted,
                isSenderCopy: false
            };
            await Mail.create(copyMailData);
        }
    }
    return time;
};

// Deletes the mail by its id from the user
const deleteMail = async (id, userId) => {
    if (!mongoose.Types.ObjectId.isValid(id) || !mongoose.Types.ObjectId.isValid(userId)) {
        return false;
    }
    const mail = await Mail.findOne({
        _id: new mongoose.Types.ObjectId(id),
        userId: new mongoose.Types.ObjectId(userId)
    });
    if (!mail) {
        return false;
    }
    mail.box = 'Trash';
    await mail.save();
    return true;
};
// Updates unsent mails
const updateMail = async (id, userId, updates) => {
    // Validate input IDs
    if (!mongoose.Types.ObjectId.isValid(id) || !mongoose.Types.ObjectId.isValid(userId)) {
        return null;
    }

    const mailObjectId = new mongoose.Types.ObjectId(id);
    const userIdObjectId = new mongoose.Types.ObjectId(userId);
    const mail = await Mail.findOne({ _id: mailObjectId, userId: userIdObjectId });

    // Return null if mail was not found
    if (!mail) return null;

    // If the mail was already sent, only allow updating 'read' or 'box'
    if (mail.toSend === true) {
        if (updates.read !== undefined) mail.read = updates.read;
        if (updates.box !== undefined) mail.box = updates.box;
        await mail.save();
        return mail;
    }

    // Update fields BEFORE sending
    if (updates.title !== undefined) mail.title = updates.title;
    if (updates.content !== undefined) mail.content = updates.content;

    // Validate and update recipients
if (updates.recipientsEmails !== undefined) {
    if (!Array.isArray(updates.recipientsEmails)) return 3;

    if (updates.toSend === true) {
        for (const email of updates.recipientsEmails) {
            if (!checkIfEmail(email)) return 3;
        }
    }
    if (!mail.sentAt) {
    mail.sentAt = new Date();}

    mail.recipientsEmails = updates.recipientsEmails;
}

    // Update box if provided
    if (updates.box !== undefined) mail.box = updates.box;

    // Handle sending the mail
    if (updates.toSend === true) {
        mail.toSend = true;
        mail.box = 'Sent';

        // Recheck blacklist based on the final content
        mail.isBlacklisted = await checkIfBlacklisted(mail.title, mail.content);

        // Send the mail and set sentAt time
        mail.sentAt = await sendMail(mail);
    }

    // Save the updated mail
    await mail.save();
    return mail;
};


// Returns all the mails of the user that contains the string inputed
const searchMail = async (query, userIdString) => {
    if (!mongoose.Types.ObjectId.isValid(userIdString)) {
        return [];
    }
    const userId = new mongoose.Types.ObjectId(userIdString);
    const LowerCaseQuery = query.toLowerCase();

    // Use Mongoose's $or and $regex for searching across multiple fields
    const matchingMails = await Mail.find({
        userId: userId,
        $or: [
            { title: { $regex: LowerCaseQuery, $options: 'i' } },
            { content: { $regex: LowerCaseQuery, $options: 'i' } },
            { author: { $regex: LowerCaseQuery, $options: 'i' } },
            { authorFirstName: { $regex: LowerCaseQuery, $options: 'i' } },
            { authorLastName: { $regex: LowerCaseQuery, $options: 'i' } },
            { recipientsEmails: { $regex: LowerCaseQuery, $options: 'i' } } 
        ]
    });
    return matchingMails;
};
// Returns all the mails of the user that contains the string inputed in a certain box
const searchMailInBox = async (query, box, userIdString) => {
    if (!mongoose.Types.ObjectId.isValid(userIdString)) {
        return [];
    }
    const userId = new mongoose.Types.ObjectId(userIdString);
    const LowerCaseQuery = query.toLowerCase();
    const matchingMails = await Mail.find({
        userId: userId,
        box: box,
        $or: [
            { title: { $regex: LowerCaseQuery, $options: 'i' } },
            { content: { $regex: LowerCaseQuery, $options: 'i' } },
            { author: { $regex: LowerCaseQuery, $options: 'i' } },
            { authorFirstName: { $regex: LowerCaseQuery, $options: 'i' } },
            { authorLastName: { $regex: LowerCaseQuery, $options: 'i' } },
            { recipientsEmails: { $regex: LowerCaseQuery, $options: 'i' } }
        ]
    });
    return matchingMails;
}
// Checks if the mail contains a Blacklisted URL
const checkIfBlacklisted = async (title, content) => {
    // Connect to the Blacklist URL
    const client = connectBlackList()
    // The url format
    const urlCheckRegex = /(https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,}(\/\S*)?/g
    // Checks if there are URLs in the inputed strings and put them into an array
    const urlsInContent = content.match(urlCheckRegex) || []
    const urlsInTitle = title.match(urlCheckRegex) || []
    const foundUrls = [...urlsInTitle, ...urlsInContent]

    // Checks if the URLs that are in the mail are blacklisted. if there are, dont create the mail
    if (foundUrls.length > 0) {
        for (const url of foundUrls) {
            const message = 'GET ' + url + '\n'
            const isBlocked = await handleBlackList(client, message)
            if (isBlocked === '200 Ok\n\ntrue true') {
                client.end()
                return true
            }
        }
    }
    // Close the server
    client.end()
    return false
}

// Check if the email provided was in fact an email
const checkIfEmail = (email) => {
    // First, check if the email ends with "@PigeonUI.com"
    if (!email.endsWith("@PigeonUI.com")) {
        return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // Test the email against the regex
    return emailRegex.test(email);
}
// Return the current time as a string
const getTime = () => {
    const time = new Date()
    const year = time.getFullYear()
    const month = (time.getMonth() + 1).toString().padStart(2, '0')
    const day = time.getDate().toString().padStart(2, '0')
    const hours = time.getHours().toString().padStart(2, '0')
    const minutes = time.getMinutes().toString().padStart(2, '0')
    const seconds = time.getSeconds().toString().padStart(2, '0')
    return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`
}

module.exports = {
    getMailById,
    get50LatestMails,
    getMailsInBox,
    createMail,
    deleteMail,
    updateMail,
    searchMail,
    searchMailInBox,
    checkIfEmail
}
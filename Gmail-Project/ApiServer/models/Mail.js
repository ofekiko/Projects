const mongoose = require('mongoose');

const mailSchema = new mongoose.Schema({
    title: { type: String, default: '' },
    content: { type: String, default: '' },
    sentAt: { type: Date, default: null },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    authorId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    author: { type: String, required: true },
    authorFirstName: { type: String, required: true },
    authorLastName: { type: String, required: true },
    recipientsEmails: [{ type: String }],
    toSend: { type: Boolean, default: false },
    read: { type: Boolean, default: false },
    box: {
        type: String,
        required: true,
        trim: true
    },
    isBlacklisted: { type: Boolean, default: false },
    isSenderCopy: { type: Boolean, default: true }
}, {
    timestamps: true 
});

module.exports = mongoose.model('Mail', mailSchema);
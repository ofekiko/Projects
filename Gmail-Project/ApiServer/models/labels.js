const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const labelSchema = new Schema({
    userId: {
        type: Schema.Types.ObjectId,
        ref: 'User', 
        required: true
    },
    name: {
        type: String,
        required: true
    }
});

labelSchema.index({ userId: 1, name: 1 }, { unique: true });

module.exports = mongoose.model('Label', labelSchema);
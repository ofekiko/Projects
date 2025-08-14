const Label = require('../models/labels');

// Create a new label and add it to the data base.
const createLabel = async (labelData) => {
    const label = new Label({ ...labelData })
    return await label.save();
};

// Find a label by its ID.
const getLabelByLabelId = async (labelId) =>{
    return await Label.findById(labelId);
};

// Get all labels for a specific user.
const getLabelByUserId = async (userId) =>{
    return await Label.find({ userId })
};

// Update the name of a label by its ID.
const updateLabelName = async (labelId, newName) => {
    const label = await Label.findById(labelId);
    if (!label) return null;
    label.name = newName;
    await label.save();
    return label;
};

// Delete a label by its ID.
const deleteLabel = async (labelId) => {
    const label = await getLabelByLabelId(labelId);
    if (!label) return null;
    await label.deleteOne();
    return label;
};

// Get a label by user ID and its name.
const getLabelByUserIdAndName = async (userId, name) => {
    return await Label.findOne({ userId, name })
};


// Export all functions for external use
module.exports = {
   createLabel,
   getLabelByLabelId,
   getLabelByUserId,
   updateLabelName,
   deleteLabel,
   getLabelByUserIdAndName
}
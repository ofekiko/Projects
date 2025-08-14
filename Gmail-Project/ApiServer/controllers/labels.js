const labels = require('../services/labels');

// Create a new label for a specific user
exports.addLabel = async (req, res) => {
    const userId = req.user.id;
    const { name } = req.body;

    // Check if name is missing
    if (name == null) {
        return res.status(400).json({ error: 'Missing label name' });
    }

    const existingLabel = await labels.getLabelByUserIdAndName(userId, name);
    if (existingLabel) {
        return res.status(409).json({ error: `Label "${name}" already exists`, label: existingLabel });
    }

    const newLabel = await labels.createLabel({
        userId,
        name,
    });

    res.status(201).location(`/api/labels/${newLabel._id}`).json(newLabel);
};

// Get a label by its ID
exports.getLabelById = async (req, res) => {
    const labelId = req.params.id;
    const label = await labels.getLabelByLabelId(labelId);

    if (!label) {
        return res.status(404).json({ error: 'Label not found' });
    }

    res.status(200).json(label);
};

// Get all labels for a specific user
exports.getLabelsByUserId = async (req, res) => {
    const userId = req.user.id;
    const userLabels = await labels.getLabelByUserId(userId);
    res.status(200).json(userLabels);
};

// Update a label's name by its ID
exports.updateLabelName = async (req, res) => {
    const userId = req.user.id;
    const labelId = req.params.id;
    const { name } = req.body;

    if (!name) {
        return res.status(400).json({ error: 'Missing name in request body' });
    }
    
    const existingLabel = await labels.getLabelByUserIdAndName(userId, name);
    if (existingLabel) {
        return res.status(409).json({ error: `Label "${name}" already exists`, label: existingLabel });
    }

    const updatedLabel = await labels.updateLabelName(labelId, name);

    if (!updatedLabel) {
        return res.status(404).json({ error: 'Label not found' });
    }

    res.status(200).json(updatedLabel);
};

// Delete a label by its ID
exports.deleteLabel = async (req, res) => {
    const labelId = req.params.id;
    const success = await labels.deleteLabel(labelId);

    if (!success) {
        return res.status(404).json({ error: 'Label not found' });
    }

    res.status(204).send();
};

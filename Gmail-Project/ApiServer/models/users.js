const User = require('./User');

// Find a user by their unique ID
const getUserById = async (id) => {
    return await User.findById(id);
};

// Find a username by their unique ID
const getUsernameById = async (id) => {
    const user = await User.findById(id);
    return user ? user.username : null;
};

// Find a user's first name by their unique ID
const getLastNameById = async (id) => {
    const user = await User.findById(id);
    return user ? user.lastName : null;
};

// Find a user's last name by their unique ID
const getFirstNameById = async (id) => {
    const user = await User.findById(id);
    return user ? user.firstName : null;
};

// Find a user by their username
const findByUsername = async (username) => {
    return await User.findOne({ username });
};

// Find a user by their email address
const findByEmail = async (email) => {
    return await User.findOne({ email });
};

// Create a new user and save it to MongoDB
const createUser = async (userData) => {
    const newUser = new User(userData);
    return await newUser.save();
};

// Get user ID by email
const getIdByEmail = async (email) => {
    const user = await User.findOne({ email });
    return user ? user._id : null;
};

// Get email by user ID
const getEmailById = async (id) => {
    const user = await User.findById(id);
    return user ? user.email : null;
};

// Export the functions
module.exports = {
    getUserById,
    getFirstNameById,
    getLastNameById,
    getUsernameById,
    getEmailById,
    getIdByEmail,
    findByEmail,
    findByUsername,
    createUser
};

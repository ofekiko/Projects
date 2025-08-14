const users = require('../models/users');
const jwt = require('jsonwebtoken');

// Register a new user
exports.register = async (req, res) => {
  try {
    const { firstName, lastName, username, password, confirmPassword, gender, birthdate, imageUri } = req.body;

    if (!firstName || !lastName || !username || !password || !confirmPassword || !gender || !birthdate) {
      return res.status(400).json({ error: 'This field is required' });
    }
    if (!imageUri) {
      return res.status(400).json({ error: 'Profile image is required' });
    }
    if (
      typeof firstName !== 'string' ||
      typeof username !== 'string' ||
      typeof password !== 'string' ||
      typeof confirmPassword !== 'string' ||
      typeof birthdate !== 'string'
    ) {
      return res.status(400).json({ error: 'Invalid data type: all fields must be strings' });
    }

    if (password !== confirmPassword) {
      return res.status(400).json({ error: 'Passwords do not match!' });
    }

    if (gender !== 'male' && gender !== 'female') {
      return res.status(400).json({ error: 'Gender must be either "male" or "female"' });
    }

    const passwordIsValid = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/.test(password);
    if (!passwordIsValid) {
      return res.status(400).json({ error: 'Password must be at least 8 characters long and contain both letters and numbers' });
    }

    const isValidDate = (dateString) => {
      const [day, month, year] = dateString.split('/');
      const date = new Date(`${year}-${month}-${day}`);
      const now = new Date();
      return !isNaN(date.getTime()) && date <= now;
    };

    if (!isValidDate(birthdate)) {
      return res.status(400).json({ error: 'Invalid birthdate format!' });
    }

    const email = username + "@PigeonUI.com";

    const existingEmail = await users.findByEmail(email);
    const existingUsername = await users.findByUsername(username);


    if (existingEmail || existingUsername) {
      return res.status(400).json({ error: 'Username already exists' });
    }

    const newUser = await users.createUser({
      firstName,
      lastName,
      username,
      email,
      password,
      gender,
      birthdate,
      imageUri
    });

    res.status(201).json({ message: "User created successfully", id: newUser._id });
  } catch (error) {
    console.error("Registration error:", error);
    res.status(500).json({ error: "Server error during registration" });
  }
};

exports.getMe = async (req, res) => {
  const userId = req.user.id;
  const user = await users.getUserById(userId);

  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }

  const { password, ...safeUser } = user.toObject();
  return res.status(200).json(safeUser);
};

// Get a user by their ID
exports.getUser = async (req, res) => {
  try {
    const id = req.params.id;
    const user = await users.getUserById(id);

    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    const { password, ...safeUser } = user.toObject();
    res.status(200).json(safeUser);
  } catch (error) {
    res.status(500).json({ error: "Server error" });
  }
};

// Get a user by their email
exports.getUserByMail = async (req, res) => {
  try {
    const mail = req.params.mail;
    const user = await users.findByEmail(mail);

    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    const { password, ...safeUser } = user.toObject();
    res.status(200).json(safeUser);
  } catch (error) {
    res.status(500).json({ error: "Server error" });
  }
};

// Login with username and password
exports.login = async (req, res) => {
  console.log("🧪 Login body:", req.body);
  try {
    const { username, password } = req.body;
    const user = await users.findByUsername(username);

    if (!user || user.password !== password) {
      return res.status(401).json({ error: 'Incorrect username or password' });
    }

    const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, { expiresIn: '1h' });
    res.cookie('token', token, {
      httpOnly: true,
      secure: false,
      sameSite: 'lax',
      maxAge: 3600000

    })
    console.log("Token created");

    return res.status(200).json({
      message: "Login successful",
      token,
      id: user._id
    });
  } catch (error) {
    res.status(500).json({ error: "Server error during login" });
  }
};


// Logout - clear the auth token cookie
exports.logout = (req, res) => {
  res.clearCookie('token', {
    httpOnly: true,
    secure: false,
    sameSite: 'lax'
  });
  return res.status(200).json({ message: 'Logged out successfully' });
};

// check if username already exist
exports.checkUsername = async (req, res) => {
  const username = req.query.username;

  if (!username) {
    return res.status(400).json({ error: 'Username is required' });
  }

  try {
    const existingUser = await users.findByUsername(username);
    const available = !existingUser;
    res.json({ available });
  } catch (err) {
    console.error("Error checking username:", err);
    res.status(500).json({ error: 'Server error while checking username' });
  }
};

const jwt = require('jsonwebtoken');

// Middleware to verify JWT token
const verifyToken = (req, res, next) => {
  // Try to get token from cookies
  const cookieToken = req.cookies.token;
  // Try to get token from Authorization header
  const authHeader = req.headers['authorization'];
  const headerToken = authHeader && authHeader.split(' ')[1];

  // Use the token from cookie first, otherwise from header
  const token = cookieToken || headerToken; 

  // If no token found, respond with 401 Unauthorized
  if (!token) {
    return res.status(401).json({ message: 'No token provided'});
  }

  try {
    // Verify the token using your secret key
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    // Continue to the next middleware or route handler
    next();
  } catch (err) {
    // If token is invalid or expired, respond with 403 Forbidden
    return res.status(403).json({ message: 'Invalid token' });
  }
};

module.exports = {
  verifyToken
};

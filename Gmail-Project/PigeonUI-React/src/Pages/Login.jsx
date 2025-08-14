import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../style/Login.css';
import pigeonLogo from '../images/logo.png';
import pigeonLogoDark from '../images/logodark.png';
import axios from 'axios';

const Login = () => {
  // State variables for user input and UI state
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isDarkMode, setIsDarkMode] = useState(false);

  const navigate = useNavigate();
  // Automatically check the user's system theme preference (dark/light mode)
  useEffect(() => {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    setIsDarkMode(prefersDark);
  }, []);

const [errorMessage, setErrorMessage] = useState('');  

// Function that runs when the form is submitted
  const handleSubmit = async (e) => {
    setErrorMessage('');
  e.preventDefault();

  try {
    // Send login request to the backend server
    const res = await axios.post(
      'http://localhost:8080/api/tokens', // Login API endpoint

      {
        username,
        password,
      },
      {
        withCredentials: true, // Send cookies
      }
    );

    localStorage.setItem('token', res.data.token); 
    localStorage.setItem('userId', res.data.id);
    // Navigate to the inbox page after successful login
    navigate("/inbox");

  } catch (err) {
    // Show error message if login fails
    const message = err.response?.data?.error || 'Login failed.';
    console.error('Login error:', message);
    setErrorMessage(message);
  }
};

  return (
    // Main container with conditional class for dark mode
    <div className={`login-page ${isDarkMode ? 'dark-mode' : ''}`}>
      <div className="logo-header">
<img
  src={isDarkMode ? pigeonLogoDark : pigeonLogo}
  alt="PigeonUI Logo"
  className="logo-image"
/>
      </div>
    {/* Dark mode toggle button */}
      <div className="toggle-container">
  <button className="toggle-icon" onClick={() => setIsDarkMode(!isDarkMode)}>
        {isDarkMode ? '☀️' : '🌙'}  </button>
</div>
    {/* Login card container */}
      <div className="login-card">
        <form className="login-form" onSubmit={handleSubmit}>
          {/* Username input */}
          <label htmlFor="username">Username</label>
          <input
            type="text"
            id="username"
            name="username"
              placeholder="Enter your username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        {/* Password input */}
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            name="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        {/* Submit button */}
          <button type="submit" className="login-button">Login</button>
        {/* Show error message if login fails */}
        {errorMessage && (
            <div className="error-message">
              {errorMessage}
            </div>
          )}
</form>
      {/* Link to registration page */}
        <div className="register-link">
          <span>Don't have an account?</span>
          <a href="/register">Register here</a>
        </div>
      </div>
    </div>
  );
};

export default Login;

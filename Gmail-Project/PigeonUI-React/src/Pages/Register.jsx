import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../style/Register.css';
import pigeonLogo from '../images/logo.png';
import pigeonLogoDark from '../images/logodark.png';
import { FiEye, FiEyeOff, FiCheck } from 'react-icons/fi';

const Register = () => {
  // State to store the form input values
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    username: '',
    password: '',
    confirmPassword: '',
    gender: '',
    birthdate: '',
    image: '', // Will store base64 of the uploaded image
  });
  // Dark mode toggle based on system preference or button
  const [isDarkMode, setIsDarkMode] = useState(false);
  // Toggle visibility of password fields
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  // Error message to show to user if form fails
  const [errorMessage, setErrorMessage] = useState('');
  // Keep track of which form fields are invalid
  const [invalidFields, setInvalidFields] = useState([]);
  // Track if passwords match (for green checkmark)
  const [passwordsMatch, setPasswordsMatch] = useState(false);
  // Ref to handle resetting the file input
  const fileInputRef = useRef(null);
  // React Router's hook to redirect user after registration
  const navigate = useNavigate();

  // Set dark mode automatically based on system settings
  useEffect(() => {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    setIsDarkMode(prefersDark);
  }, []);

  // Handle input changes for all form fields
  const handleChange = (e) => {
    const { name, value } = e.target;
    // Update form data and check if passwords match
    setFormData((prev) => {
      const updated = { ...prev, [name]: value };
      if (updated.password && updated.confirmPassword) {
        setPasswordsMatch(updated.password === updated.confirmPassword);
      } else {
        setPasswordsMatch(false);
      }
      return updated;
    });
    // Clear any previous error for this specific field
    setInvalidFields((prev) => prev.filter((field) => field !== name));
  };


  // Convert image file to base64 and store it in formData
const handleImageUpload = (e) => {
  const file = e.target.files[0];
  if (!file) return;

  const reader = new FileReader();

  reader.onload = (event) => {
    const img = new Image();
    img.src = event.target.result;

    img.onload = () => {
      const MAX_WIDTH = 800;
      const MAX_HEIGHT = 800;
      const JPEG_QUALITY = 0.5;

      let width = img.width;
      let height = img.height;

      if (width > height && width > MAX_WIDTH) {
        height *= MAX_WIDTH / width;
        width = MAX_WIDTH;
      } else if (height > MAX_HEIGHT) {
        width *= MAX_HEIGHT / height;
        height = MAX_HEIGHT;
      }

      const canvas = document.createElement('canvas');
      canvas.width = width;
      canvas.height = height;

      const ctx = canvas.getContext('2d');
      ctx.drawImage(img, 0, 0, width, height);

      const base64Compressed = canvas.toDataURL('image/jpeg', JPEG_QUALITY);

      setFormData((prev) => ({ ...prev, image: base64Compressed }));
    };
  };

  reader.onerror = (err) => {
    console.error("Error reading image:", err);
  };

  reader.readAsDataURL(file);
};

  // Handle form submission (when user clicks Register)
  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    const invalids = [];

    // Check if passwords match
    if (formData.password !== formData.confirmPassword) {
      invalids.push('password', 'confirmPassword');
      setErrorMessage("Passwords do not match");
    }

    if (new Date(formData.birthdate) > new Date()) {
      invalids.push('birthdate');
      setErrorMessage("Birthdate cannot be in the future");
    }

  if (!formData.image || formData.image.trim() === '') {
    invalids.push('image');
    setErrorMessage("Profile image is required");
  }

    try {
      // Send registration request to backend API
      await axios.post(
        'http://localhost:8080/api/users',
        {
          ...formData,
          gender: formData.gender.toLowerCase(),
          imageUri: formData.image,
        },
        { withCredentials: true }
      );
      // On success, redirect to login page
      navigate("/login");
    } catch (err) {
      const message = err.response?.data?.error || 'Registration failed.';
      setErrorMessage(message);
      // Mark username as invalid if backend complains about it
      if (message.toLowerCase().includes('username') || message.toLowerCase().includes('email')) {
        invalids.push('username');
      }
    }
    // Highlight invalid fields
    setInvalidFields(invalids);
  };
  // Helper to check if a field has an error
  const hasError = (field) => invalidFields.includes(field);

  return (
    // Main container for the registration page with optional dark mode
    <div className={`register-page ${isDarkMode ? 'dark-mode' : ''}`}>
    {/* App logo at the top of the page */}
      <div className="logo-header">
        <img
          src={isDarkMode ? pigeonLogoDark : pigeonLogo}
          alt="PigeonUI Logo"
          className="logo-image"
        />
      </div>

    {/* Dark/light mode toggle */}
      <div className="toggle-container">
        <button className="toggle-icon" onClick={() => setIsDarkMode(!isDarkMode)}>
          {isDarkMode ? '☀️' : '🌙'}
        </button>
      </div>
    {/* Registration form card */}
      <div className="register-card">
        <form className="register-form" onSubmit={handleSubmit}>
        {/* === Row 1: First and Last Name === */}
          <div className="form-row">
            <div className="form-group">
              <label>First Name</label>
              <input
                type="text"
                name="firstName"
                value={formData.firstName}
                placeholder="Enter your first name"
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Last Name</label>
              <input
                type="text"
                name="lastName"
                value={formData.lastName}
                placeholder="Enter your last name"
                onChange={handleChange}
              />
            </div>
          </div>

        {/* === Row 2: Username and Password === */}
          <div className="form-row">
            <div className="form-group">
              <label>Username</label>
              <input
                type="text"
                name="username"
                value={formData.username}
                placeholder="Create a username"
                onChange={handleChange}
                required
                className={hasError('username') ? 'input-error' : ''}
              />
            </div>
            <div className="form-group password-group">
              <label>Password</label>
              <div className="password-wrapper">
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={formData.password}
                  placeholder="Create a password"
                  onChange={handleChange}
                  required
                  className={hasError('password') ? 'input-error' : ''}
                />
                <button
                  type="button"
                  className="eye-button"
                  onClick={() => setShowPassword((prev) => !prev)}
                >
                  {showPassword ? <FiEyeOff /> : <FiEye />}
                </button>
              </div>
            </div>
          </div>

        {/* === Row 3: Confirm Password and Gender === */}
          <div className="form-row">
            <div className="form-group password-group">
              <label>Confirm Password</label>
              <div className="password-wrapper">
                <input
                  type={showConfirmPassword ? 'text' : 'password'}
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  placeholder="Re-enter your password"
                  onChange={handleChange}
                  required
                  className={hasError('confirmPassword') ? 'input-error' : ''}
                />
                <button
                  type="button"
                  className="eye-button"
                  onClick={() => setShowConfirmPassword((prev) => !prev)}
                >
                  {showConfirmPassword ? <FiEyeOff /> : <FiEye />}
                </button>
                {/* Checkmark if passwords match */}
                {passwordsMatch && <FiCheck className="checkmark" />}
              </div>
            </div>
            <div className="form-group">
              <label>Gender</label>
              <select
                name="gender"
                value={formData.gender}
                onChange={handleChange}
                required
              >
                <option value="">Select gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
              </select>
            </div>
          </div>

        {/* === Row 4: Birthdate and Profile Image === */}
          <div className="form-row">
            <div className="form-group">
              <label>Birthdate</label>
              <input
                type="date"
                name="birthdate"
                value={formData.birthdate}
                onChange={handleChange}
                required
                className={hasError('birthdate') ? 'input-error' : ''}
              />
            </div>
            <div className="form-group">
              <label>Profile Image</label>
              <label htmlFor="imageUpload" className="custom-upload">Upload Image</label>
              <input
                id="imageUpload"
                type="file"
                name="image"
                accept="image/*"
                onChange={handleImageUpload}
                ref={fileInputRef}
                style={{ display: 'none' }}
              />
              {/* Preview uploaded image with remove button */}
              {formData.image && (
                <div className="image-preview">
                  <img src={formData.image} alt="Preview" />
                  <button
                    type="button"
                    className="remove-image-btn"
                    onClick={() => {
                      setFormData((prev) => ({ ...prev, image: '' }));
                      if (fileInputRef.current) {
                        fileInputRef.current.value = '';
                      }
                    }}
                  >
                    ✖
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* Error Message */}
          {errorMessage && (
            <div className="error-message">
              {errorMessage}
            </div>
          )}
        {/* Submit button */}
          <button type="submit" className="register-button">Register</button>
        </form>
      {/* Link to login page */}
        <div className="register-link">
          <span>Already have an account?</span>
          <Link to="/">Login here</Link>
        </div>
      </div>
    </div>
  );
};

export default Register;

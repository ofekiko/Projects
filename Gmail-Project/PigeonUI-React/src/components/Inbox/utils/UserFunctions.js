import axios from 'axios';

// Function that connects to the API server and retrieves the user's details.
export const getUser = async (userId) => {
  const res = await fetch(`http://localhost:8080/api/users/${encodeURIComponent(userId)}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'include'
  });

  return await res.json();
};

// Function that connects to the API server and retrieves the user's details by their email.
export const getUserByMail = async (mail) => {
  const res = await fetch(`http://localhost:8080/api/users/mail/${encodeURIComponent(mail)}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'include'
  });

  return await res.json();
};

// Function that connects to the API server and performs a logout operation.
export const logoutFromServer = async () => {
  try {
    await axios.post('http://localhost:8080/api/users/logout', {}, {
      withCredentials: true
    });
  } catch (err) {
    console.error('Logout failed on server side (continuing client cleanup):', err);
  }
};

// Component that generates a dynamic avatar for the user's profile picture.
export const DynamicAvatar = ({ name, gender }) => {
  // Display the first letter of the user first name.
  const firstLetter = name?.charAt(0)?.toUpperCase() || '?';

  // The background color of the picture will be blue if the user is male and pink if the user is female.
  const backgroundColor = gender === 'female'
    ? '#f48fb1' 
    : gender === 'male'
      ? '#42a5f5' 
      : '#bdbdbd';

  return (
    <div className="dynamic-avatar" style={{ backgroundColor }}>
      {firstLetter}
    </div>
  );
};



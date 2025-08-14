import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";

// Base URL of the backend server
const BASE_URL = "http://localhost:8080"; 

export default function PrivateRoute({ children }) {
    // State to track if the user is authenticated
  const [isAuthenticated, setIsAuthenticated] = useState(null);

  useEffect(() => {
    // Function to check if the user is authenticated
    const checkAuth = async () => {
      try {
        // Send request to the backend to validate authentication
        const res = await fetch(`${BASE_URL}/api/auth/validate`, { credentials: "include" });

        if (res.ok) {
          setIsAuthenticated(true);
        } else {
          setIsAuthenticated(false);
        }
      } catch {
        setIsAuthenticated(false);
      }
    };

    checkAuth();
  }, []);

  // While authentication is being checked, show a loading message
  if (isAuthenticated === null) {
    return <div>Checking authentication...</div>;
  }
  // If the user is not authenticated, redirect them to the login page
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  // If the user is authenticated, show the protected content (children)
  return children;
}

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './Pages/Login';
import Register from './Pages/Register';
import InboxPage from './Pages/InboxPage';
import PrivateRoute from './components/PrivateRoute';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
          <Route path="/login" element={<Login />} /> 
        <Route path="/register" element={<Register />} />
        {/* should be only for logged in users */}
<Route
  path="/inbox"
  element={
    <PrivateRoute>
      <InboxPage />
    </PrivateRoute>
  }
/>
      </Routes>
    </Router>
  );
}

export default App;
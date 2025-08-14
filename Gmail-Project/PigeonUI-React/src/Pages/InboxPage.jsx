// Import from react
import React, { useState, useCallback } from 'react';

// Import components
import TopBarInbox from '../components/Inbox/TopBarInbox.jsx';
import SearchBox from '../components/Inbox/SearchBox';
import LeftMenu from '../components/Inbox/LeftMenu';
import MailBox from '../components/Inbox/MailBox';
import '../style/Inbox/InboxPage.css';

// The Inbox Page
export default function InboxPage() {
  // Mailbox and UI State 
  const [currentBox, setCurrentBox] = useState('Inbox');
  const [darkMode, setDarkMode] = useState(false);

  // Data Refresh Signals State
  const [refreshCountsSignal, setRefreshCountsSignal] = useState(0);
  const [refreshMailboxContentSignal, setRefreshMailboxContentSignal] = useState(0);

  // Search Functionality State 
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState(null);
  const [searchInCurrentBoxOnly, setSearchInCurrentBoxOnly] = useState(true);

  // Callback function to refresh mail counts and labels
  const handleRefreshCountsAndLabels = useCallback(() => {
    setRefreshCountsSignal(prevSignal => prevSignal + 1);
  }, []);

  // Callback function triggered when mail content changes
  const onMailContentChange = useCallback(() => {
    setRefreshMailboxContentSignal(prev => prev + 1);
    setRefreshCountsSignal(prev => prev + 1);
  }, []);

  // Handler function to handle the mail search operation using the function in the API server
  const handleSearch = async () => {
    if (searchTerm.trim() === '') {
      setSearchResults(null);
      return;
    }

    try {
      const token = localStorage.getItem('token');
      console.log("🔐 Token:", token);

      let url = `http://localhost:8080/api/mails/search/${searchTerm}`;
      if (searchInCurrentBoxOnly) {
        url += `?box=${currentBox}`;
      }

      const res = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!res.ok) throw new Error("Search failed");

      const results = await res.json();
      setSearchResults(results);
    } catch (err) {
      console.error('Search error:', err);
      setSearchResults([]);
    }
  };


  return (
    <div className={`page-wrapper ${darkMode ? 'dark-mode' : ''}`}>
      {/* The component for the top menu */}
      <div className="inbox-container">
        <TopBarInbox
          darkMode={darkMode}
          toggleDarkMode={() => setDarkMode(!darkMode)}
        />
        {/* The component for the left menu */}
        <div className="left-section">
          <LeftMenu
            currentBox={currentBox}
            setCurrentBox={setCurrentBox}
            refreshCountsSignal={refreshCountsSignal}
            onMailContentChange={onMailContentChange}
          />
        </div>
        {/* The component for the search box */}
        <div className="main-section">
          <SearchBox
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onEnter={handleSearch}
            searchInCurrentBoxOnly={searchInCurrentBoxOnly}
            setSearchInCurrentBoxOnly={setSearchInCurrentBoxOnly}
          />
          {/* The component for the mail box */}
          <MailBox
            currentBox={currentBox}
            emails={searchResults !== null ? searchResults : undefined}

            onRefreshCountsAndLabels={handleRefreshCountsAndLabels}
            refreshMailboxContentSignal={refreshMailboxContentSignal}
            onMailContentChange={onMailContentChange}
          />
        </div>
      </div>
    </div>
  );
}

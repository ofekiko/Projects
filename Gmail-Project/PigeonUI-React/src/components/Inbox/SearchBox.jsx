import { FaSearch } from 'react-icons/fa';
import '../../style/Inbox/SearchBox.css';

// Component that renders the search box for filtering emails.
const SearchBox = ({ value, onChange, onEnter, searchInCurrentBoxOnly, setSearchInCurrentBoxOnly }) => {
  return (
    <div className="search-box-container">
      <FaSearch className="search-icon" />
      
      {/* Input field for searching emails */}
      <input
        type="text"
        placeholder="Search..."
        value={value}
        onChange={onChange}
        onKeyDown={(e) => e.key === 'Enter' && onEnter()}
        className="search-input"
      />
      
      {/* Checkbox to limit the search to the current mailbox */}
      <label className="search-checkbox-label">
        <input
          type="checkbox"
          checked={searchInCurrentBoxOnly}
          onChange={(e) => setSearchInCurrentBoxOnly(e.target.checked)}
        />
        In current box only
      </label>
    </div>
  );
};


export default SearchBox;

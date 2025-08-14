// Import component
import MailListItem from './MailListItem';

// Import css
import '../../style/Inbox/MailBox.css'; // Corrected path and capitalization

// The component of the list of mails
function MailList({ mails, onMailClick, onMailSelect, selectedMail }) {
  // If no mails, show format for empty box
  if (!mails || mails.length === 0) {
    return (
      <div className="mail-list-empty-message">
        No mails in this box
      </div>
    );
  }

  // Render the list of mails.
  return (
    <div className="mail-list-container">
      {/* Map over the 'mails' array to render a MailListItem for each mail */}
      {mails.map(mail => (
        <MailListItem
          key={mail.id} 
          mail={mail}   
          onMailClick={onMailClick} 
          onMailSelect={onMailSelect}
          isSelected={selectedMail && selectedMail.id === mail.id}
        />
      ))}
    </div>
  );
}

export default MailList;

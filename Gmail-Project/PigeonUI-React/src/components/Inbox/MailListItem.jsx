// Import from react
import { MdOutlineMail, MdMail } from 'react-icons/md';
// Import from css
import '../../style/Inbox/MailBox.css';


// The component of each item at the mail list
function MailListItem({ mail, onMailClick, onMailSelect, isSelected }) {
  // Determine the CSS class based on the mail's read status (read/unread)
  const mailStatusClass = mail.read ? 'mail-item-read' : 'mail-item-unread';

  return (
    // The main div element representing a single mail item in the list
    <div
      className={`mail-list-item ${mailStatusClass} ${isSelected ? 'mail-item-selected' : ''}`}
      onClick={() => {
        if (isSelected) {
          onMailClick(mail);
        } else {
          onMailSelect(mail);
        }
      }}
      role="button" 
      tabIndex={0} 
      aria-label={`from -${mail.author}, title ${mail.title}`}

      // Drag mail
      draggable={mail.box !== 'Spam'}
      onDragStart={(e) => {
        if (mail.box === 'Spam') {
          e.preventDefault();
          return;
        }
        e.dataTransfer.setData("mailId", mail.id);
        e.dataTransfer.setData("toSend", mail.toSend);
        e.dataTransfer.setData("box", mail.box); 
        e.dataTransfer.setData("isSenderCopy", mail.isSenderCopy);
      }}
    >
      {/* Container for the mail status icon */}
      <div className="mail-icon-container">
        {mail.read ? <MdOutlineMail className="mail-read-icon" /> : <MdMail className="mail-unread-icon" />}
      </div>

      {/* Container for mail information: sender and subject */}
      <div className="mail-info">
        <div className="mail-sender">{mail.authorFirstName} {mail.authorLastName}</div>
        <div className="mail-subject">
          <strong>{mail.title}</strong>
          <span className="mail-snippet"> —  {mail.content.substring(0, 70)}...</span>
        </div>
      </div>

      {/* Container for the mail's timestamp */}
      <div className="mail-timestamp">
        {mail.sentAt}
      </div>

    </div>
  );
}

export default MailListItem;

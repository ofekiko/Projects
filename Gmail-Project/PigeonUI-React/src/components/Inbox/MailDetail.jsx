// Import from React
import React, { useState, useEffect } from 'react';
import {
  MdArrowBack,
  MdDeleteForever,
  MdReply,
  MdForward,
  MdMail,
  MdMarkEmailRead,
  MdMoveToInbox
} from 'react-icons/md';

// Import functions and components
import { DynamicAvatar, getUserByMail } from './utils/UserFunctions';
import SenderProfile from './SenderProfile';

// Import from css
import '../../style/Inbox/MailBox.css';
import '../../style/Inbox/MailDetail.css';

// The component of the mail view
function MailDetail({ mail, onBack, onDelete, onReply, onForward, onToggleReadStatus, onMoveMail, labels }) {
  const [showMoveDropdown, setShowMoveDropdown] = useState(false);
  const [selectedDestination, setSelectedDestination] = useState('');
  const [senderUser, setSenderUser] = useState(null);

  useEffect(() => {
    if (mail?.author) {
      getUserByMail(mail.author).then(user => {
        setSenderUser(user);
      }).catch(err => {
        console.error('Error fetching sender info:', err);
      });
    }
  }, [mail]);

  if (!mail) {
    return <div className="mail-detail-empty-message">Please select a mail</div>;
  }

  const readUnreadIcon = mail.read ? <MdMail /> : <MdMarkEmailRead />;
  const readUnreadText = mail.read ? 'Mark as Unread' : 'Mark as Read';

  const desiredStaticBoxes = [
    { name: 'Inbox', value: 'Inbox' },
    { name: 'Spam', value: 'Spam' },
    { name: 'Trash', value: 'Trash' },
  ];

  const allPossibleDestinations = [
    ...desiredStaticBoxes,
    ...(labels || [])
      .filter(label => !['Sent', 'Drafts'].includes(label.name))
      .map(label => ({ name: label.name, value: label.name }))
  ];

  const handleMoveAction = () => {
    if (selectedDestination && onMoveMail) {
      onMoveMail(mail, selectedDestination);
      setShowMoveDropdown(false);
      setSelectedDestination('');
      onBack();
    }
  };

  return (
    <div className="mail-detail-container">
      {/* Header with actions */}
      <div className="mail-detail-sticky-actions">
        <div className="mail-detail-back-wrapper">
          <button onClick={onBack} className="mail-detail-back-button" aria-label="Back to Inbox">
            <MdArrowBack />
            <span>Back</span>
          </button>
        </div>
        <div className="mail-detail-actions-right">
          {!mail.isSenderCopy && (
            <button onClick={onReply} className="mail-detail-action-button" aria-label="Reply">
              <MdReply />
            </button>
          )}
          <button onClick={onForward} className="mail-detail-action-button" aria-label="Forward">
            <MdForward />
          </button>
          <button onClick={onDelete} className="mail-detail-action-button" aria-label="Delete">
            <MdDeleteForever />
          </button>
          <div className="mail-detail-action-dropdown-wrapper">
            <button
              onClick={() => setShowMoveDropdown(!showMoveDropdown)}
              className="mail-detail-action-button"
              aria-label="Move mail"
              title="Move mail"
            >
              <MdMoveToInbox />
            </button>
            {showMoveDropdown && (
              <div className="mail-detail-dropdown-content">
                <select
                  value={selectedDestination}
                  onChange={(e) => setSelectedDestination(e.target.value)}
                  className="mail-detail-dropdown-select"
                >
                  <option value="">Move to...</option>
                  {allPossibleDestinations.map(box => {
                    const isInvalidMove = mail.box === 'Sent' && (box.value === 'Inbox' || box.value === 'Drafts') ||
                    (mail.isSenderCopy && box.value === 'Inbox');
                    if (box.value !== mail.box && !isInvalidMove) {
                      return <option key={box.value} value={box.value}>{box.name}</option>;
                    }
                    return null;
                  })}
                </select>
                <button
                  onClick={handleMoveAction}
                  disabled={!selectedDestination}
                  className="mail-detail-dropdown-confirm-button"
                >
                  Move
                </button>
              </div>
            )}
          </div>
          <button
            onClick={() => onToggleReadStatus(mail.id, !mail.read)}
            className="mail-detail-action-button"
            aria-label={readUnreadText}
            title={readUnreadText}
          >
            {readUnreadIcon}
          </button>
        </div>
      </div>

      {/* Mail subject */}
      <h2 className="mail-detail-subject">{mail.title}</h2>

      {/* Sender + time row */}
      <div className="mail-detail-header-row">
        <div className="mail-detail-sender-left">
          <div className="sender-avatar-hover-wrapper">
            {senderUser?.image ? (
              <img
                src={senderUser.image}
                alt="Sender"
                className="mail-detail-avatar"
              />
            ) : (
              <DynamicAvatar
                name={senderUser?.firstName || mail.author}
                gender={senderUser?.gender}
              />
            )}

            <div className="sender-profile-flyout">
              {senderUser && <SenderProfile user={senderUser} />}
            </div>
          </div>

          <div className="mail-detail-sender-info">
            <div className="mail-detail-sender-name">
              From: <strong>{senderUser?.username}</strong> &lt;{mail.author}&gt;
            </div>
          </div>
        </div>
        <div className="mail-detail-date-inline">{mail.sentAt}</div>
      </div>

      {/* Recipients */}
      {mail.recipientsEmails && mail.recipientsEmails.length > 0 && (
        <div className="mail-detail-recipients">
          <span className="mail-detail-recipients-label">To: </span>
          <span className="mail-detail-recipients-list">
            {mail.recipientsEmails.join(', ')}
          </span>
        </div>
      )}

      {/* Mail content */}
      <div className="mail-detail-content-wrapper">
        <div className="mail-detail-content-box">
          <p>
            {mail.content.split('\n').map((line, index) => (
              <React.Fragment key={index}>
                {line}<br />
              </React.Fragment>
            ))}
          </p>
        </div>
      </div>
    </div>
  );
}

export default MailDetail;

import ReactDOM from 'react-dom';
import { useState } from 'react';

// Popup component that lets the user compose or edit an email.
function NewMailComp({
  title,
  setTitle,
  content,
  setContent,
  recipientsEmails,
  setRecipientsEmails,
  createMail,
  errorMessage,
  setShowNewMailInput,
  quotedMail = null,
  type = null, // 'reply' or 'forward'
  onSuccessAfterSendOrCancel = null
}) {

  // Controls the animation effect when sending the email.
  const [isSending, setIsSending] = useState(false);

  // If replying or forwarding, add the original message before the user's content.
  const finalQuotedContent = quotedMail
    ? `[${type === 'reply' ? 'REPLY TO' : 'FWD FROM'}: ${quotedMail.author} | ${quotedMail.title}]\n${quotedMail.content}\n\n`
    : '';

  const modal = (
    <div className="modal-overlay">
      <div className={`modal-content ${isSending ? 'fly-away' : ''}`}>

        {/* Component header */}
        <div className="modal-header">New Message</div>

        {/* Input field for recipient email addresses */}
        <input
          type="text"
          value={recipientsEmails}
          onChange={(e) => setRecipientsEmails(e.target.value)}
          placeholder="To"
        />

        {/* Input field for the email subject */}
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Subject"
        />

        {/* Display the original message when replying or forwarding */}
        {quotedMail && (
          <div className="quoted-mail">
            <div className="quoted-mail-title">
              {type === 'reply' ? 'Replying to:' : 'Forwarded message:'}
            </div>
            <div className="quoted-mail-content">
              <strong>From:</strong> {quotedMail.author}<br />
              <strong>Subject:</strong> {quotedMail.title}<br />
              <strong>Content:</strong><br />
              <em>{quotedMail.content}</em>
            </div>
            <hr />
          </div>
        )}

        {/* Textarea for writing the email body */}
        <textarea
          type="text"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />

        {/* Display an error message if the email fails to send */}
        {errorMessage && (
          <p className="error-message">{errorMessage}</p>
        )}

        <div className="modal-actions">
          {/* Cancel button — saves the message as a draft */}
          <button
            className="cancel-btn"
            onClick={() => {
              const fullContent = finalQuotedContent + content;
              if (title.trim() || recipientsEmails.trim() || fullContent.trim()) {
                createMail(false, fullContent);
              }
              setShowNewMailInput(false);
              if (onSuccessAfterSendOrCancel) onSuccessAfterSendOrCancel(); 
            }}
          >
            Cancel
          </button>

          {/* Send button — only enabled when recipients field is not empty */}
          <button
            className={`create-btn ${recipientsEmails.trim() ? '' : 'disabled'}`}
            onClick={async () => {
              const fullContent = finalQuotedContent + content;
              const success = await createMail(true, fullContent);

              if (success) {
                setIsSending(true);
                setTimeout(() => {
                  setShowNewMailInput(false);
                  if (onSuccessAfterSendOrCancel) onSuccessAfterSendOrCancel();
                }, 1000);
              }
            }}
            disabled={!recipientsEmails.trim()}
          >
            Send
          </button>
        </div>
      </div>
    </div>
  );

  const portalTarget = document.querySelector('.page-wrapper') || document.body;
  return ReactDOM.createPortal(modal, portalTarget);
}

export default NewMailComp;

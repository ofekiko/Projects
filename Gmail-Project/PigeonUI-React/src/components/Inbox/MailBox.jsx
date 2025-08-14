// Import from React
import React, { useState, useEffect, useCallback } from 'react';

// Import Components
import MailList from './MailList';
import MailDetail from './MailDetail.jsx';
import NewMailComp from './MailComp.jsx';

// Import functions
import { fetchLabels } from './utils/labelFunctions';
import { addUrl, deleteUrl } from './utils/SpamFunctions';
import { deleteMail, updateMail, getMailsInBox, createMail as sendMailToAPI, editDraft } from './utils/MailFunctions';

// Import css
import '../../style/Inbox/MailBox.css';

// The component of the mail box
function Mailbox({ currentBox, emails, onRefreshCountsAndLabels, refreshMailboxContentSignal, onMailContentChange }) {
  // Hooks for mail display and interaction
  const [mails, setMails] = useState([]);
  const [selectedMail, setSelectedMail] = useState(null);
  const [openedMail, setOpenedMail] = useState(null);

  // Loading and error state for mail fetching
  const [, setIsLoading] = useState(true);
  const [, setError] = useState(null);

  // Hooks for composing new mails
  const [showNewMailInput, setShowNewMailInput] = useState(false);
  const [quotedMail, setQuotedMail] = useState(null);
  const [mailType, setMailType] = useState(null);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [recipientsEmails, setRecipientsEmails] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [, setToSend] = useState(false);

  // Hooks for mail labels
  const [labels, setLabels] = useState([]);
  const [labelErrorMessage, setLabelErrorMessage] = useState('');

  // Hooks for pagination
  const emailsPerPage = 50;
  const [currentPage, setCurrentPage] = useState(1);

  // Updating labels hook
  const refreshLabels = useCallback(() => {
    fetchLabels(setLabels, setLabelErrorMessage);
  }, []);

  // Updating mails hook
  const refreshMails = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    setSelectedMail(null);
    try {
      const fetchedMails = await getMailsInBox(currentBox);
      const sortedMails = [...fetchedMails].sort(
        (a, b) => new Date(b.timestamp) - new Date(a.timestamp)
      );
      setMails(sortedMails);
      setCurrentPage(1);
    } catch (err) {
      console.error("Failed to fetch mails:", err);
      setError("Failed to load mails. Please try again.");
    } finally {
      setIsLoading(false);
    }
  }, [currentBox]);

  // useEffect hook to manage mail fetching and updates
  useEffect(() => {
    if (emails && Array.isArray(emails)) {
      const unique = Array.from(new Map(emails.map(m => [m.id, m])).values());
      setMails(unique);
      setCurrentPage(1);
    } else {
      refreshMails();
    }
  }, [emails, currentBox, refreshMailboxContentSignal, onMailContentChange, refreshMails]);

  // useEffect hook to refresh labels
  useEffect(() => {
    refreshLabels();
  }, [refreshLabels]);

  // useEffect hook to reset opened/selected mail when changing boxes
  useEffect(() => {
    setOpenedMail(null);
    setSelectedMail(null);
  }, [currentBox]);

  // Handles opening a mail for detailed viewing
  const handleMailOpen = async (mail) => {
    if (mail.toSend === false) {
      setTitle(mail.title || '');
      setContent(mail.content || '');
      setRecipientsEmails((mail.recipientsEmails || []).join(' '));
      setToSend(false);
      setErrorMessage('');
      setShowNewMailInput(true);
      setQuotedMail(null);
      setMailType(null);
      setOpenedMail(mail);
      setSelectedMail(mail);
      refreshLabels();
    } else {
      setOpenedMail(mail);
      setSelectedMail(mail);
      refreshLabels();

      if (!mail.read) {
        try {
          const updatedMail = await updateMail(mail.id, { read: true });
          if (updatedMail) {
            setMails(prevMails =>
              prevMails.map(m => (m.id === mail.id ? updatedMail : m))
            );
            setOpenedMail(updatedMail);
            setSelectedMail(updatedMail);
          }
          if (onRefreshCountsAndLabels) {
            onRefreshCountsAndLabels();
          }
        } catch (err) {
          console.error("Failed to mark as read", err);
          setError("Failed to mark mail as read.");
        }
      }
    }
  };

  // Handles selecting a mail in the list
  const handleMailSelect = (mail) => {
    setSelectedMail(mail);
    setOpenedMail(null);
  };

  // Handles navigating back from the mail detail view to the mail list
  const handleBackToList = () => {
    setOpenedMail(null);
    setSelectedMail(null);
    if (onRefreshCountsAndLabels) {
      onRefreshCountsAndLabels();
    }
    refreshLabels();
    refreshMails();
  };

  // Handles deleting the currently opened mail
  const handleDeleteMail = async () => {
    if (openedMail) {
      if (openedMail.box === 'Spam') {
        deleteUrlsFromBlacklist(openedMail)
      }
      const success = await deleteMail(openedMail.id);
      if (success) {
        setOpenedMail(null);
        refreshMails();

        if (onMailContentChange) {
          onMailContentChange();
        }
        if (onRefreshCountsAndLabels) {
          onRefreshCountsAndLabels();
        }
        refreshLabels();
      } else {
        setError("Could not delete mail. Please try again.");
      }
    }
  };

  // Handles toggling the read status of a mail
  const handleToggleReadStatus = async (mailId, newReadStatus) => {
    try {
      const updates = { read: newReadStatus };
      await updateMail(mailId, updates);
      setMails(prevMails =>
        prevMails.map(m => (m.id === mailId ? { ...m, read: newReadStatus } : m))
      );
      setOpenedMail(prevOpened => prevOpened && prevOpened.id === mailId ? { ...prevOpened, read: newReadStatus } : prevOpened);
      setSelectedMail(prevSelected => prevSelected && prevSelected.id === mailId ? { ...prevSelected, read: newReadStatus } : prevSelected);

      if (onRefreshCountsAndLabels) {
        onRefreshCountsAndLabels();
      }
    } catch (err) {
      console.error("Error toggling mail read status:", err);
      setError("Could not update mail status. Please try again.");
    }
  };

  // Handles moving a mail to a different mailbox
  const handleMoveMail = async (mailToMove, destinationBox) => {
    if (
      mailToMove.box === 'Sent' &&
      (destinationBox === 'Inbox' || destinationBox === 'Drafts')
    ) {
      setError("You cannot move Sent mails to Inbox or Drafts.");
      return;
    }
    if (!mailToMove || !destinationBox) {
      console.error("Mail to move or destination box is undefined.");
      setError("Cannot move mail: Invalid destination.");
      return;
    }

    if (mailToMove.isSenderCopy && destinationBox === 'Inbox') {
      setError("You cannot move sent mails to Inbox.");
      return;
    }

    if (mailToMove.box === 'Sent' || mailToMove.box === 'Drafts') {
      console.warn(`Attempted to move mail from ${mailToMove.box}. This might require special handling (e.g., copying).`);
    }
    if (destinationBox === 'Spam' && mailToMove.box !== 'Spam') {
      addUrlsToBlacklist(mailToMove)
    }
    if (mailToMove.box === 'Spam' && destinationBox !== 'Spam') {
      deleteUrlsFromBlacklist(mailToMove)
    }

    const updates = { box: destinationBox };
    try {
      await updateMail(mailToMove.id, updates);
      setMails(prevMails => prevMails.filter(m => m.id !== mailToMove.id));

      if (openedMail && openedMail.id === mailToMove.id) {
        setOpenedMail(null);
      }
      if (selectedMail && selectedMail.id === mailToMove.id) {
        setSelectedMail(null);
      }

      if (onMailContentChange) {
        onMailContentChange();
      }
      if (onRefreshCountsAndLabels) {
        onRefreshCountsAndLabels();
      }
      refreshLabels();
      console.log(`Mail ${mailToMove.id} moved to ${destinationBox}.`);
    } catch (err) {
      console.error(`Failed to move mail ${mailToMove.id} to ${destinationBox}:`, err);
      setError(`Failed to move mail to ${destinationBox}.`);
      refreshMails();
    }
  };

  // Extracts URLs from mail content and title and adds them to a blacklist
  const addUrlsToBlacklist = async (mail) => {
    const urlCheckRegex = /(https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,}(\/\S*)?/g
    const urlsInContent = mail.content.match(urlCheckRegex) || []
    const urlsInTitle = mail.title.match(urlCheckRegex) || []
    const foundUrls = [...urlsInTitle, ...urlsInContent]

    for (const url of foundUrls) {
      await addUrl(url, () => { }, () => { }, setErrorMessage);
    }
  }

  // Extracts URLs from mail content and title and deletes them from a blacklist
  const deleteUrlsFromBlacklist = async (mail) => {
    const urlCheckRegex = /(https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,}(\/\S*)?/g
    const urlsInContent = mail.content.match(urlCheckRegex) || []
    const urlsInTitle = mail.title.match(urlCheckRegex) || []
    const foundUrls = [...urlsInTitle, ...urlsInContent]

    for (const url of foundUrls) {
      await deleteUrl(url, () => { }, () => { }, setErrorMessage);
    }
  }

  // Prepares the component for replying to the selected mail
  const handleReplyMail = () => {
    if (selectedMail) {
      setTitle('Re: ' + selectedMail.title);
      setRecipientsEmails(selectedMail.author);
      setContent('');
      setQuotedMail(selectedMail);
      setMailType('reply');
      setShowNewMailInput(true);
    }
  };

  // Prepares the component for forwarding the selected mail.
  const handleForwardMail = () => {
    if (selectedMail) {
      setTitle('Fwd: ' + selectedMail.title);
      setRecipientsEmails('');
      setContent('');
      setQuotedMail(selectedMail);
      setMailType('forward');
      setShowNewMailInput(true);
    }
  };

  // Handles creating and sending a new mail or editing/sending a draft
  const handleCreateMail = async (shouldSend, fullContent) => {
    const mailId = openedMail?.id;

    if (shouldSend && !recipientsEmails.trim()) {
      setErrorMessage('Recipient is required');
      return;
    }

    if (openedMail?.toSend === false && mailId) {
      // If currently this is draft mail and editing an existing draft
      await editDraft(
        title,
        setTitle,
        fullContent,
        setContent,
        recipientsEmails,
        setRecipientsEmails,
        shouldSend,
        setToSend,
        () => {
          setShowNewMailInput(false);
          setOpenedMail(null);
          setSelectedMail(null);
          if (onMailContentChange) onMailContentChange();
          if (onRefreshCountsAndLabels) onRefreshCountsAndLabels();
          refreshLabels();
        },
        setErrorMessage,
        mailId
      );
    } else {
      // If composing a new mail or sending a draft for the first time
      await sendMailToAPI(
        title,
        setTitle,
        fullContent,
        setContent,
        recipientsEmails,
        setRecipientsEmails,
        shouldSend,
        setToSend,
        () => {
          setShowNewMailInput(false);
          if (mailType === 'reply' || mailType === 'forward') {
            setOpenedMail(null);
            setSelectedMail(null);
          }
          if (onMailContentChange) onMailContentChange();
          refreshLabels();
        },
        setErrorMessage
      );
    }

    setQuotedMail(null);
    setMailType(null);
  };

  // Pagination calculations for displaying the correct subset of emails
  const indexOfLastEmail = currentPage * emailsPerPage;
  const indexOfFirstEmail = indexOfLastEmail - emailsPerPage;
  const currentEmails = mails.slice(indexOfFirstEmail, indexOfLastEmail);
  const totalPages = Math.ceil(mails.length / emailsPerPage);

  return (
    <div className="mailbox-container">
      <div className="mailbox-panel">
        {/* The component for the mail view */}
        {openedMail && openedMail.toSend !== false ? (
          <MailDetail
            mail={openedMail}
            onBack={handleBackToList}
            onDelete={handleDeleteMail}
            onReply={handleReplyMail}
            onForward={handleForwardMail}
            onToggleReadStatus={handleToggleReadStatus}
            onMoveMail={handleMoveMail}
            labels={labels}
          />
        ) : (
          // If no mails or not loading, show empty box message with placeholders
          mails.length === 0 ? (
            <div className="mail-placeholder-wrapper">
              <h2 className="empty-box-message">No emails in this box yet</h2>
              <div className="mail-list-item placeholder">
                <div className="mail-avatar">A</div>
                <div className="mail-icon-container">
                  <i className="mail-status-icon fas fa-envelope-open-text"></i>
                </div>
                <div className="mail-info">
                  <div className="mail-sender">Example Sender</div>
                  <div className="mail-subject">This is a sample email subject for preview</div>
                </div>
                <div className="mail-timestamp">12:45</div>
              </div>
            </div>
          ) : (
            // If mails exist, show pagination and MailList
            <>
              <div className="pagination">
                <button
                  onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                  disabled={currentPage === 1}
                >
                  ◀ Prev
                </button>
                <span>Page {currentPage} of {totalPages}</span>
                <button
                  onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                  disabled={currentPage === totalPages}
                >
                  Next ▶
                </button>
              </div>

              <MailList
                mails={currentEmails}
                onMailClick={handleMailOpen}
                onMailSelect={handleMailSelect}
                selectedMail={selectedMail}
              />
            </>
          )
        )}
      </div>
      {/* Conditional rendering for the new mail composition component */}
      {showNewMailInput && (
        <NewMailComp
          title={title}
          setTitle={setTitle}
          content={content}
          setContent={setContent}
          recipientsEmails={recipientsEmails}
          setRecipientsEmails={setRecipientsEmails}
          setShowNewMailInput={setShowNewMailInput}
          createMail={handleCreateMail}
          errorMessage={errorMessage}
          quotedMail={quotedMail}
          type={mailType}
          onSuccessAfterSendOrCancel={() => {
            setOpenedMail(null);  
            setSelectedMail(null); 
          }}
        />
      )}
    </div>
  );
}

export default Mailbox;

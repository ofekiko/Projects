// Import from React
import { useState, useEffect, useCallback } from 'react';
import { MdInbox, MdSend, MdDrafts, MdBlock, MdEdit, MdDelete, MdLabel } from 'react-icons/md';
import { FaTrash, FaEllipsisV } from 'react-icons/fa';

// Import functions
import { createMail as createMailFunc, countUnreadMailsMenu, updateMail } from './utils/MailFunctions';
import { fetchLabels, createLabel, deleteLabel, updateLabel } from './utils/labelFunctions';
import { addUrl, deleteUrl } from './utils/SpamFunctions';
import { renderMenuItem } from './utils/MenuItems';

// Import Components
import NewLabelComp from './NewLabelComp';
import BlackListComp from './BlackListComp';
import NewMailComp from './MailComp';

// Style and images
import '../../style/Inbox/LeftMenu.css';
import logo from '../../images/logof.png';
import pigeonIcon from '../../images/bird.png';

// The component of left menu
function LeftMenu({ currentBox, setCurrentBox, refreshCountsSignal, onMailContentChange }) {

  // Label hooks
  const [showNewLabelInput, setShowNewLabelInput] = useState(false);
  const [newLabelName, setNewLabelName] = useState('');
  const [editLabelId, setEditLabelId] = useState(null);
  const [labels, setLabels] = useState([]);

  // Mail hooks
  const [, setToSend] = useState(false);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [recipientsEmails, setRecipientsEmails] = useState('');
  const [showNewMailInput, setShowNewMailInput] = useState(false);
  const [mailCounts, setMailCounts] = useState({ Inbox: 0, Sent: 0, Drafts: 0, Trash: 0, Spam: 0 });

  // Blacklist hooks
  const [url, setUrl] = useState('');
  const [urlMode, setUrlMode] = useState(null);

  // Error hooks
  const [labelErrorMessage, setLabelErrorMessage] = useState('');
  const [mailErrorMessage, setMailErrorMessage] = useState('');
  const [blacklistErrorMessage, setBlacklistErrorMessage] = useState('');

  // Drag hooks
  const [, setDragOverLabel] = useState(null);
  const [dragOverTarget, setDragOverTarget] = useState(null);
  const [openDropdownId, setOpenDropdownId] = useState(null);

  // Menu Items
  const staticMenu = [
    { name: 'Inbox', icon: MdInbox, mailCount: mailCounts.Inbox },
    { name: 'Sent', icon: MdSend, mailCount: mailCounts.Sent },
    { name: 'Drafts', icon: MdDrafts, mailCount: mailCounts.Drafts },
    { name: 'Trash', icon: MdDelete, mailCount: mailCounts.Trash },
    { name: 'Spam', icon: MdBlock, mailCount: mailCounts.Spam }
  ];

  // Updating menu items count hook
  const fetchCounts = useCallback(async () => {
    const inbox = await countUnreadMailsMenu('Inbox');
    const sent = await countUnreadMailsMenu('Sent');
    const drafts = await countUnreadMailsMenu('Drafts');
    const trash = await countUnreadMailsMenu('Trash');
    const spam = await countUnreadMailsMenu('Spam');
    setMailCounts({ Inbox: inbox, Sent: sent, Drafts: drafts, Trash: trash, Spam: spam });
  }, 
  []);

  // Updating menu items count effect
  useEffect(() => { fetchCounts(); }, [refreshCountsSignal, fetchCounts]);

  // Updating labels hook
  const refreshLabels = useCallback(() => {
    fetchLabels(setLabels, setLabelErrorMessage);
  }, []);

  useEffect(() => {
    refreshLabels();
  }, []);

  // Updating labels effect
  useEffect(() => { refreshLabels(); }, [refreshCountsSignal, refreshLabels]);

  // Dragging mails effect
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (!event.target.closest('.label-dropdown')) setOpenDropdownId(null);
    };
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  // Helper function for creating an email.
  const createMail = async (toSendValue = true, fullContent = content) => {
    const success = await createMailFunc(
      title,
      setTitle,
      fullContent,
      setContent,
      recipientsEmails,
      setRecipientsEmails,
      toSendValue,
      setToSend,
      () => { },
      setMailErrorMessage
    );

    if (success) {
      if (onMailContentChange) onMailContentChange();
      fetchCounts();
    }

    return success;

  };

  // Helper function for editing an existing label.
  const handleEditLabel = (label) => {
    setNewLabelName(label.name);
    setEditLabelId(label._id);
    setLabelErrorMessage('');
    setShowNewLabelInput(true);
    setOpenDropdownId(null);
  };

  // Helper function for deleting an existing label.
  const handleDeleteLabel = (labelId) => {
    deleteLabel(labelId, refreshLabels, setLabelErrorMessage);
    setOpenDropdownId(null);
  };

  return (
    <div className="left-menu-wrapper">
      {/* The logo of our application */}
      <div className="logo-container">
        <img src={logo} alt="Pigeon logo" className="pigeon-logo" />
      </div>

      {/* The compose button */}
      <button className="compose-btn" onClick={() => {
        setTitle(''); 
        setContent(''); 
        setRecipientsEmails(''); 
        setToSend(false);
        setShowNewMailInput(true); 
        setMailErrorMessage('');
      }}>
        <img src={pigeonIcon} alt="Compose" className="compose-icon" />
        <span className="compose-text">Compose</span>
      </button>

      {/* The left menu items */}
      <ul className="list-group">
        {staticMenu.map(item =>
          renderMenuItem({
            name: item.name,
            IconComponent: item.icon,
            mailCount: item.mailCount,
            currentBox,
            setCurrentBox,
            onMailContentChange,
            updateMail,
            fetchCounts,
            dragOverTarget,
            setDragOverTarget
          })
        )}
      </ul>

      {/* The labels */}
      <button className="labels-btn" onClick={() => {
        setNewLabelName('');
        setLabelErrorMessage('');
        setShowNewLabelInput(true);
      }}>
        <span className="labels-text">Labels</span>
        <span className="labels-plus">+</span>
      </button>

      {/* The component for composing a new email */}
      {showNewMailInput && (
        <NewMailComp 
        title={title} 
        content={content} 
        recipientsEmails={recipientsEmails} 
        setTitle={setTitle} 
        setContent={setContent} 
        setRecipientsEmails={setRecipientsEmails} 
        setToSend={setToSend} 
        setShowNewMailInput={setShowNewMailInput} 
        createMail={createMail} 
        errorMessage={mailErrorMessage} />
      )}

      {/* The component for creating a new label */}
      {showNewLabelInput && (
        <NewLabelComp 
        newLabelName={newLabelName} 
        setNewLabelName={setNewLabelName} 
        setShowNewLabelInput={setShowNewLabelInput} 
        createLabel={() => {
          if (editLabelId) {
            updateLabel(newLabelName, setNewLabelName, setShowNewLabelInput, editLabelId, refreshLabels, setLabelErrorMessage);
            setEditLabelId(null);
          } else {
            createLabel(newLabelName, setNewLabelName, setShowNewLabelInput, refreshLabels, setLabelErrorMessage);
          }
        }} errorMessage={labelErrorMessage} />
      )}

      {/* The component for managing the blacklist */}
      {urlMode && (
        <BlackListComp url={url} setUrl={setUrl} setShowUrlInput={() => setUrlMode(null)} onSubmit={urlMode === "add" ? () => addUrl(url, setUrl, () => setUrlMode(null), setBlacklistErrorMessage) : () => deleteUrl(url, setUrl, () => setUrlMode(null), setBlacklistErrorMessage)} mode={urlMode} errorMessage={blacklistErrorMessage} />
      )}

      {/* Custom labels section – renders user-defined labels */}
      <div className="custom-labels-wrapper">
        <ul className="list-group">
          {labels
            .filter(label => !['inbox', 'send', 'drafts', 'spam'].includes(label.name.toLowerCase()))
            .map(label => (
              <li
                key={label._id}
                className={`list-group-item ${currentBox === label.name ? 'active' : ''}`}
                onClick={() => setCurrentBox(label.name)}
                // Optionally highlight the label for drag.
                onDragOver={(e) => {
                  e.preventDefault();
                  setDragOverLabel(label.name); 
                }}
                onDrop={(e) => {
                  const mailId = e.dataTransfer.getData('mailId');
                  // Moving mail to the label.
                  if (mailId) {
                    updateMail(mailId, { box: label.name }); 
                    onMailContentChange();
                    fetchCounts(); 
                  }
                }}
              >
                {/* Label icon and name */}
                <span className="gmail-icon"><MdLabel /></span>
                <span className="item-name">{label.name}</span>

                {/* Dropdown for editing or deleting the label */}
                <div className="count-and-menu">
                  <div className="label-dropdown">
                    <FaEllipsisV className="label-menu-icon" onClick={(e) => {
                      e.stopPropagation();
                      setOpenDropdownId(openDropdownId === label._id ? null : label._id);
                    }} />

                    {openDropdownId === label._id && (
                      <div className="dropdown-menu">
                        <button className="dropdown-item" onClick={(e) => { e.stopPropagation(); handleEditLabel(label); }}>
                          <MdEdit className="dropdown-icon" /> Edit
                        </button>
                        <button className="dropdown-item delete" onClick={(e) => { e.stopPropagation(); handleDeleteLabel(label._id); }}>
                          <FaTrash className="dropdown-icon" /> Delete
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              </li>
            ))}
        </ul>
      </div>

    </div>
  );
}

export default LeftMenu;

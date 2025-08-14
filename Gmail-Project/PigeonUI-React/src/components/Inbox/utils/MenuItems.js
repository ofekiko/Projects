// Function that renders the menu items dynamically.
export const renderMenuItem = ({
  name,
  IconComponent,
  mailCount,
  currentBox,
  setCurrentBox,
  onMailContentChange,
  updateMail,
  fetchCounts,
  dragOverTarget,
  setDragOverTarget
}) => {

  // Define which boxes are allowed to receive dragged mails.
  const isAllowedTarget = !['Sent', 'Drafts'].includes(name);

  return (
    <li
      key={name}
      className={`list-group-item d-flex justify-content-between align-items-center ${currentBox === name ? 'active' : ''} ${dragOverTarget === name ? 'drop-allowed' : ''}`}

      // Set this box as the current one when clicked.
      onClick={() => setCurrentBox(name)}

      onDragOver=
      {
        (e) => {
          // When dragging over a valid target, allow drop behavior.
          if (isAllowedTarget) {
            e.preventDefault();
            setDragOverTarget(name);
          }
        }
      }

      // Remove highlight when dragging leaves the target.
      onDragLeave={() => { setDragOverTarget(null) }}
      onDrop={async (e) => {
        setDragOverTarget(null);

        if (isAllowedTarget) {
          const mailId = e.dataTransfer.getData("mailId");
          const toSend = e.dataTransfer.getData("toSend");
          const mailBox = e.dataTransfer.getData("box");
          const isSenderCopy = e.dataTransfer.getData("isSenderCopy") === 'true';

          if (isSenderCopy && name === 'Inbox') {
            return;
          }

          if (
            name === 'Spam' ||
            (mailBox === 'Sent' && (name === 'Inbox' || name === 'Drafts'))
          ) {
            return;
          }

          if (mailId) {
            const updated = await updateMail(mailId, { box: name });
            if (updated && onMailContentChange) {
              onMailContentChange();
              if (fetchCounts) fetchCounts();
            }
          }
        }
      }}

    >

      {/* Display icon and name for each box */}
      <IconComponent className="gmail-icon" />
      <span className="item-name">{name}</span>
      <div className="count-wrapper">
        <span className="item-count">{mailCount}</span>
      </div>
    </li>
  );
};

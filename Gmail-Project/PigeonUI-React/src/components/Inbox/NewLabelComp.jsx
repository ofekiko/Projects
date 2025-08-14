import ReactDOM from 'react-dom';

// Popup component that lets the user create or edit a label.
function NewLabelComp
  (
    {
      newLabelName,
      setNewLabelName,
      setShowNewLabelInput,
      createLabel,
      errorMessage
    }
  ) {
  const modal = (
    <div className="modal-overlay">
      <div className="modal-content">
        {/* Modal Header */}
        <h2 className="modal-title">New label</h2>

        <form onSubmit={(e) => {
          e.preventDefault();
          createLabel();
        }}>

          {/* Text box where the user enters the name of the label they want to create */}
          <label>Please enter a new label name:</label>
          <input
            type="text"
            value={newLabelName}
            onChange={(e) => setNewLabelName(e.target.value)}
            placeholder="Label name"
          />

          {/* Error message shown when label creation fails */}
          {errorMessage && (
            <div className="error-message">{errorMessage}</div>
          )}

          {/* Action buttons for creating the label or cancelling the action */}
          <div className="modal-actions">

            {/* Cancel button */}
            <button
              type="button"
              className="cancel-btn"
              // When clicked, the label creation modal is closed.
              onClick={() => setShowNewLabelInput(false)}
            >
              Cancel
            </button>

            {/* Sending button */}
            <button
              type="submit"
              // Show the creating button only when there is label name in the label name box.
              className={`create-btn ${newLabelName.trim() ? '' : 'disabled'}`}
              // Disable the button if no label name is provided.
              disabled={!newLabelName.trim()}
            >
              Create
            </button>

          </div>
        </form>
      </div>
    </div>
  );

  const portalTarget = document.querySelector('.page-wrapper') || document.body;
  return ReactDOM.createPortal(modal, portalTarget);
}

export default NewLabelComp;
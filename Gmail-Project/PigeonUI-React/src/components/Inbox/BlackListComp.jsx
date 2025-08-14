// Function that creates the component responsible for handling the blacklist.
function BlackListComp
(
    { 
    url,
    setUrl,
    setShowUrlInput,
    onSubmit, 
    mode, 
    errorMessage 
    }
) 
{
    return (
        <div className="modal-overlay">
            <div className="modal-content">
                {/* Header indicating whether the user is adding or deleting a URL from the blacklist */}
                <h2>{mode === "add" ? "Add" : "Delete"} URL {mode === "add" ? "to" : "from"} the Blacklist</h2>

                {/* Text box where the user enters the URL to be added to or removed from the blacklist */}
                <label>Please enter a URL:</label>
                <input
                    type="text"
                    value={url}
                    onChange={(e) => setUrl(e.target.value)}
                    placeholder="URL"
                />

                {/* Error message shown when blacklist operation fails */}
                {errorMessage && (
                    <p className="error-message">{errorMessage}</p>
                )}

                <div className="modal-actions">
                    {/* Cancel button */}
                    <button className="cancel-btn" onClick={setShowUrlInput}>Cancel</button>

                    {/* Submit button */}
                    <button
                        className={`create-btn ${url.trim() ? '' : 'disabled'}`}
                        onClick={onSubmit}
                        // Disable the button if URL is not provided.
                        disabled={!url.trim()}
                    >
                        {mode === "add" ? "Add" : "Delete"}
                    </button>
                </div>
            </div>
        </div>
    );
}


export default BlackListComp;

// Function that connects to the API server to fetch the user's labels.
export const fetchLabels = async (setLabels, setErrorMessage) => {
    const res = await fetch('http://localhost:8080/api/labels', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include'
    });

    // If the request was successful, update the labels.
    if (res.ok) {
      const data = await res.json();
      setLabels(data);
    }

    // If the request failed, show the error message.
    else {
      const err = await res.json();
      setErrorMessage('Error: ' + err.error);
    }
  };

// Function that connects to the API server to create a new label for the user.
export const createLabel = async (newLabelName, setNewLabelName, setShowNewLabelInput, fetchLabels, setErrorMessage) => {
    const res = await fetch('http://localhost:8080/api/labels', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify({ name: newLabelName })
    });


    // If the request was successful, update the labels and close the component.
    if (res.ok) {
      setShowNewLabelInput(false);
      setNewLabelName('');
      fetchLabels();
    }

    // If the request failed, show the error message.
    else {
      const err = await res.json();
      setErrorMessage('Error: ' + err.error);
    }
  };

// Function that connects to the API server to delete a label for the user.
export const deleteLabel = async (labelId, fetchLabels, setErrorMessage) => {
    const res = await fetch(`http://localhost:8080/api/labels/${labelId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    });

    // If the request was successful, update the labels.
    if (res.ok) {
      fetchLabels();
    }

    // If the request failed, show the error message.
    else {
      const err = await res.json();
      setErrorMessage('Error: ' + err.error);
    }
  };

// Function that connects to the API server to update a label name for the user.
export const updateLabel = async (newLabelName, setNewLabelName, setShowNewLabelInput, labelId, fetchLabels, setErrorMessage) => {
    const res = await fetch(`http://localhost:8080/api/labels/${labelId}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify({ name: newLabelName })
    });

    // If the request was successful, update the labels and close the component.
    if (res.ok) {
      setShowNewLabelInput(false);
      setNewLabelName('');
      fetchLabels();
    }

    // If the request failed, show the error message.
    else {
      const err = await res.json();
      setErrorMessage('Error: ' + err.error);
    }
  };
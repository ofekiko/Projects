// Function that connects to the API server to add a URL to the blacklist.
export const addUrl = async (url, setUrl, setShowUrlInput, setErrorMessage) => {
    const res = await fetch('http://localhost:8080/api/blacklist', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify({ url: url })
    });

    // If the request was successful, close the URL box.
    if (res.ok) {
      setShowUrlInput(false);
      setUrl('');
    }
  };

// Function that connects to the API server to delete a URL from the blacklist.
export const deleteUrl= async (url, setUrl, setShowUrlInput, setErrorMessage) => {
    const res = await fetch(`http://localhost:8080/api/blacklist/${encodeURIComponent(url)}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    });

    // If the request was successful, close the URL box.
    if (res.ok) {
    setShowUrlInput(false);
    setUrl('');
    }

  };
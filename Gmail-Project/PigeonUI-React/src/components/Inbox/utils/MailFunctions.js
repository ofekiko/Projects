// Function that connects to the API server to send a new mail.
export const createMail = async (
  title,
  setTitle,
  content,
  setContent,
  recipientsEmails,
  setRecipientsEmails,
  toSend,
  setToSend,
  onSuccess,
  setErrorMessage
) => {

  // Converts the mail recipients into array.
  const cleanedRecipients = recipientsEmails
    .split(/\s+/)
    .map(email => email.trim())
    .filter(email => email !== '');

  const res = await fetch('http://localhost:8080/api/mails', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify({
      title,
      content,
      recipientsEmails: cleanedRecipients,
      toSend
    })
  });

  // If the request was successful, update the mail box sent.
  if (res.ok) {
    setTitle('');
    setContent('');
    setRecipientsEmails('');
    setToSend(false);
    if (onSuccess) onSuccess();
    return true;
  }

  // If the request failed, show the error message.
  else {
    const err = await res.json();
    setErrorMessage('Error: ' + err.error);
    return false;
  }

};

// Function that connects to the API server to get the user's mails.
export const getUserMails = async () => {
  const res = await fetch('http://localhost:8080/api/mails', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
  });

  return await res.json();
};


// Function that for each box count the number of mails it contain.
export const countUnreadMailsMenu = async (box) => {
  const mails = await getUserMails();
  const count = mails.filter(mail => mail.box?.toLowerCase() === box.toLowerCase() && !mail.read).length;

  return count;
};

// Function that for each box return the mails it contain.
export const getMailsInBox = async (box) => {
  const allMails = await getUserMails();
  const filteredMails = allMails.filter(mail =>
    mail.box?.toLowerCase() === box.toLowerCase()
  );
  return filteredMails;
}

// Function that connects to the API server to delete a specific mail.
export const deleteMail = async (mailId) => {
  const res = await fetch(`http://localhost:8080/api/mails/${mailId}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
  });
  return res.ok;
}

// Function that connects to the API server to update a specific mail.
export const updateMail = async (mailId, updates) => {
  const res = await fetch(`http://localhost:8080/api/mails/${mailId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify(updates)
  });

  // If the request failed, show the error message.
  if (!res.ok) {
    console.error("Failed to update mail:", await res.json());
    return null;
  }
  
  // If the request was successful, update the mail box sent.
  return await res.json();
}

// Function that connects to the API server to edit a draft mail.
export const editDraft = async (
  title,
  setTitle,
  content,
  setContent,
  recipientsEmails,
  setRecipientsEmails,
  toSend,
  setToSend,
  onSuccess,
  setErrorMessage,
  draftId
) => {

  // Converts the mail recipients into array.
  const cleanedRecipients = recipientsEmails
    .split(/\s+/)
    .map(email => email.trim())
    .filter(email => email !== '');

  const res = await fetch(`http://localhost:8080/api/mails/${draftId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify({
      title,
      content,
      recipientsEmails: cleanedRecipients,
      toSend
    })
  });

  // If the request was successful, update the mail box sent.
  if (res.ok) {
    setTitle('');
    setContent('');
    setRecipientsEmails('');
    setToSend(false);
    if (onSuccess) onSuccess();
  }

  // If the request failed, show the error message.
  else {
    const err = await res.json();
    setErrorMessage('Error: ' + err.error);
  }
};
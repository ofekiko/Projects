const { connectBlackList, handleBlackList } = require('../services/blacklist');

const deleteUrl = async (url) => {
    // Create a socket client to communicate with the Blacklist server.
    const client = connectBlackList()
    const message = 'DELETE ' + url + '\n'
    
    // Send the given URL to the Blacklist server to remove it from the blacklist.
    const output = await handleBlackList(client, message)

    // Close the client socket.
    client.end()
    return output
}

const addUrl = async (url) => {
    // Create a socket client to communicate with the Blacklist server.
    const client = connectBlackList()
    const message = 'POST ' + url + '\n'

    // Send the URL to the Blacklist server to add it to the Blacklist.
    const output = await handleBlackList(client, message)

    // Close the client socket.
    client.end()
    return output
}

module.exports = { deleteUrl, addUrl }
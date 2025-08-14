const net = require('net')

// Create a socket client to communicate with the Blacklist server.
function connectBlackList() {

    const client = net.createConnection({ port: 5555, host: 'blacklistserver' }, () => { })

    return client
}

// Use the client socket to send a message to the Blacklist server and return its response.
function handleBlackList(client, message) {

    return new Promise((resolve, reject) => {

        // Send a message to the Blacklist server.
        client.write(message)

        // Receive the response from the server.
        client.once('data', (data) => {
            resolve(data.toString().trim())
        })

        // Handle any socket error.
        client.once('error', (err) => {
            reject(err)
        })

    })
}

module.exports = { connectBlackList, handleBlackList }








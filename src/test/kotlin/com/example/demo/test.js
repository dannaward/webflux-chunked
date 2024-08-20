const http = require('http');

// Function to send chunks dynamically
function sendChunks(req, chunks, interval) {
    chunks.forEach((chunk, index) => {
        setTimeout(() => {
            req.write(chunk);
            console.log(`Sent chunk: ${chunk}`);
        }, index * interval);
    });

    // End the request after the last chunk
    setTimeout(() => {
        req.end();
        console.log('Request ended');
    }, chunks.length * interval);
}

// Usage example
const options = {
    hostname: 'localhost',
    port: 8080,
    path: '/chunk/',
    method: 'POST',
    headers: {
        'Content-Type': 'application/octet-stream',
        'Transfer-Encoding': 'chunked',
    }
};

// Create the request
const req = http.request(options, (res) => {
    console.log(`STATUS: ${res.statusCode}`);
    console.log(`HEADERS: ${JSON.stringify(res.headers)}`);
    res.setEncoding('utf8');
    res.on('data', (chunk) => {
        console.log(`BODY: ${chunk}`);
    });
    res.on('end', () => {
        console.log('No more data in response.');
    });
});

// Handle request error
req.on('error', (e) => {
    console.error(`Problem with request: ${e.message}`);
});

// Define the chunks and interval (in milliseconds)
const chunks = ['Hell', 'o my ', 'name i', 's dann', 'a.x'];
const interval = 1000; // 1 second interval

// Send the chunks
sendChunks(req, chunks, interval);
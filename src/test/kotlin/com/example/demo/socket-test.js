const net = require('net');

async function test1() {
    var client = new net.Socket();
    client.connect(8080, '127.0.0.1', function () {
        // console.log('Connected');
        client.write('POST /chunk HTTP/1.1\r\n');
        client.write('Content-Type: text/plain\r\n');
        client.write('Content-Length: 5\r\n');
        client.write('\r\n');
        client.write('12345');
    });

    client.on('data', function (data) {
        // console.log('Received: ' + data);
        client.destroy(); // kill client after server's response
    });

    client.on('close', function () {
        // console.log('Connection closed');
    });
}

function writeChunk(client, payload) {
    client.write(`${payload.length.toString(16)}\r\n`);
    client.write(`${payload}\r\n`);
}

function test2() {
    var client = new net.Socket();
    client.connect(8080, '127.0.0.1', function () {
        // console.log('Connected');
        client.write('POST /chunk HTTP/1.1\r\n');
        client.write('Content-Type: text/plain\r\n');
        client.write('Transfer-Encoding: chunked\r\n');
        client.write('\r\n');
        writeChunk(client, '123');
        writeChunk(client, '45678');
        writeChunk(client, '90');
        writeChunk(client, '');
    });

    client.on('data', function (data) {
        console.log(`Received: ${data}!!`);
        client.destroy(); // kill client after server's response
    });

    client.on('close', function () {
        // console.log('Connection closed');
    });
}

function test3() {
    var client = new net.Socket();
    client.connect(8080, '127.0.0.1', function () {
        // console.log('Connected');
        // client.write('POST / HTTP/1.1\r\n');
        client.write('POST /chunk HTTP/1.1\r\n');
        client.write('Content-Type: text/plain\r\n');
        client.write('Transfer-Encoding: chunked\r\n');
        client.write('\r\n');
        setTimeout(() => writeChunk(client, 'abc'), 0);
        setTimeout(() => writeChunk(client, 'defgh'), 1000);
        setTimeout(() => writeChunk(client, 'ij'), 2000);
        setTimeout(() => writeChunk(client, ''), 3000);
    });

    client.on('data', function (data) {
        console.log(`Received: ${data}!!`);
        client.destroy(); // kill client after server's response
    });

    client.on('close', function () {
        // console.log('Connection closed');
    });
}

function test4() {
    var client = new net.Socket();
    client.connect(8080, '127.0.0.1', function () {
        // console.log('Connected');
        // client.write('POST / HTTP/1.1\r\n');
        client.write('POST /chunk HTTP/1.1\r\n');
        client.write('Content-Type: text/plain\r\n');
        client.write('Transfer-Encoding: chunked\r\n');
        client.write('\r\n');
        setTimeout(() => writeChunk(client, 'abc'), 0);

        // setTimeout(() => writeChunk(client, 'defgh'), 1000);

        // ========== 전체 chunk 가 다 안가고 분할되어서 가는 경우를 테스트 ==========
        setTimeout(() => client.write(`${'defgh'.length.toString(16)}\r\n`), 1000);
        setTimeout(() => client.write(`${'def'}`), 2000);      // TODO 이 부분 이슈
        setTimeout(() => client.write(`${'gh'}\r\n`), 3000);
        // ========== 전체 chunk 가 다 안가고 분할되어서 가는 경우를 테스트 ==========

        setTimeout(() => writeChunk(client, 'ij'), 4000);
        setTimeout(() => writeChunk(client, ''), 5000);
    });

    client.on('data', function (data) {
        console.log(`Received: ${data}!!`);
        client.destroy(); // kill client after server's response
    });

    client.on('close', function () {
        // console.log('Connection closed');
    });
}

// test1();
// test2();
// test3();
test4();

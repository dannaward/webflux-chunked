package com.example.demo

import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/chunk")
class ChunkController {

    @PostMapping
    fun handleChunkedRequest(exchange: ServerWebExchange): Mono<Void> {
        val dataBuffers: Flux<DataBuffer> = exchange.request.body

        return dataBuffers
            .doOnNext { dataBuffer ->
                val byteArray = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(byteArray)
                println("Received raw data: ${String(byteArray)}")

                DataBufferUtils.release(dataBuffer)
            }
            .then()
    }
}

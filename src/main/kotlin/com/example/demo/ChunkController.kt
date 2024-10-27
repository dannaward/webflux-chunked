package com.example.demo

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/chunk")
class ChunkController {

    @PostMapping
    fun postChunked(request: ServerHttpRequest): Mono<Void> {
        val dataBuffers: Flux<DataBuffer> = request.body

        return dataBuffers
            .doOnNext { dataBuffer ->
                val byteArray = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(byteArray)
                print(String(byteArray))

                DataBufferUtils.release(dataBuffer)
            }
            .then()
    }
}
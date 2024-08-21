package com.example.demo

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets


@Component
class ChunkedRequestHandler {

    fun handleChunkedRequest(request: ServerRequest): Mono<ServerResponse> {
        // 청크 전송을 위한 데이터가 원본 바이트 스트림으로 전달되기 때문에, 이를 직접 다루어야 합니다.
        println(request)
        return request.bodyToFlux(ByteBuffer::class.java)
            .doOnNext { byteBuffer ->
                // ByteBuffer를 바이트 배열로 변환
                val bytes = ByteArray(byteBuffer.remaining())
                byteBuffer.get(bytes)

                // 바이트 배열을 문자열로 변환 (ISO_8859_1은 바이트를 문자로 변환할 때 사용하는 인코딩)
                val chunkData = String(bytes, StandardCharsets.ISO_8859_1)

                // 원본 데이터 그대로 출력
                println("Received chunk: $chunkData")
            }
            .then(ServerResponse.ok().build())
    }
}
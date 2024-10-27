package com.example.demo

import io.netty.channel.CombinedChannelDuplexHandler
import io.netty.handler.codec.http.HttpObjectDecoder
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpServerCodec
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.netty.NettyPipeline

@Configuration
class NettyServerConfig {
    @Bean
    fun nettyServerCustomizer(): NettyServerCustomizer {
        // allowPartialChunks 필드를 접근 하기 위해 reflection 사용
        val handlerCls = CombinedChannelDuplexHandler::class.java
        val inboundHandlerField = handlerCls.getDeclaredField("inboundHandler")
        val decoderCls = HttpObjectDecoder::class.java
        val allowPartialChunksField = decoderCls.getDeclaredField("allowPartialChunks")

        // allowPartialChunks 필드를 접근 가능하도록 설정
        inboundHandlerField.isAccessible = true
        allowPartialChunksField.isAccessible = true

        return NettyServerCustomizer {
            it.doOnChannelInit { connectionObserver, channel, remoteAddress ->

                val codec = channel.pipeline().get(NettyPipeline.HttpCodec) as HttpServerCodec
                val inboundHandler = inboundHandlerField.get(codec) as HttpRequestDecoder

                // allowPartialChunks 필드를 false 로 변경
                allowPartialChunksField.set(inboundHandler, false)
            }
        }
    }
}
package com.example.demo

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOption
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.LastHttpContent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import reactor.netty.http.HttpProtocol
import reactor.netty.http.server.HttpServer

@Configuration
class NettyConfig {

    @Bean
    fun customNettyHttpServer(): HttpServer {
        return HttpServer.create()
            .tcpConfiguration { tcpServer ->
                tcpServer
                    .doOnConnection { conn ->
                        conn.addHandlerLast(HttpServerCodec()) // Handles HTTP/1.1 with chunked encoding
                        conn.addHandlerLast(object : SimpleChannelInboundHandler<Any>() {
                            override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
                                if (msg is FullHttpRequest) {
                                    // Handle request
                                    println("Received request: ${msg.uri()}")
                                } else if (msg is HttpContent) {
                                    // Handle chunked content
                                    println("Received chunked content: ${msg.content().toString(Charsets.UTF_8)}")
                                    if (msg is LastHttpContent) {
                                        println("End of content")
                                    }
                                }
                            }

                            override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
                                cause.printStackTrace()
                                ctx.close()
                            }
                        })
                    }
            }
    }
}
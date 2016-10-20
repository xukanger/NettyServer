package com.just.server.http.http;


import com.just.server.http.file.HttpStaticFileServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpStaticFileServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final static String WEBSOCKET_PATH="/websocket";

    public HttpStaticFileServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        pipeline.addLast(new HttpStaticFileServerHandler());
        pipeline.addLast(new WebSocketFrameHandler());
    }
}

package com.bilimili.buaa13.im;

import com.bilimili.buaa13.im.handler.TokenValidationHandler;
import com.bilimili.buaa13.im.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class IMServer {

    // 存储每个用户的全部连接
    public static Map<Integer, Set<Channel>> userChannel = new ConcurrentHashMap<>();
    // 使用线程池管理线程
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    public void start() throws InterruptedException {
        executorService.execute(()->{
            try{
                // 主从结构
                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup();

                // 绑定监听端口
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker)
                        .channel(NioServerSocketChannel.class)

                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline(); // 处理流

                                // 添加 Http 编码解码器
                                configurePipeline(pipeline);

                            }
                        });
                ChannelFuture future = bootstrap.bind(7071).sync();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // 关闭线程池
                shutdownAndAwaitTermination(executorService);
            }
        });
    }
    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // 禁止提交新任务
        try {
            // 等待现有任务完成
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // 取消正在执行的任务
                // 等待任务响应中断
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("线程池未能关闭");
            }
        } catch (InterruptedException ie) {
            // 重新中断当前线程
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(1024 * 64))
                .addLast(new TokenValidationHandler())
                .addLast(new WebSocketServerProtocolHandler("/im"))
                .addLast(new WebSocketHandler());
    }

    public void startIn() {
        Callable<Void> serverTask = () -> {
            try {
                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup();

                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                WebSocketPipelineFactory.configurePipeline(pipeline);
                                WebSocketChannelInitializer channelInitializer = new WebSocketChannelInitializer();
                                channelInitializer.initChannel(socketChannel);
                                pipeline.addLast(channelInitializer);
                                configurePipeline(pipeline);
                            }
                        });

                ChannelFuture future = bootstrap.bind(7_071).sync();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return null;
        };

        Future<Void> future = executorService.submit(serverTask);

        try {
            // 阻塞主线程，直到服务器任务完成
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}

class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        WebSocketPipelineFactory.configurePipeline(pipeline);
    }
}

class WebSocketPipelineFactory {
    static void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(1024 * 64))
                .addLast(new TokenValidationHandler())
                .addLast(new WebSocketServerProtocolHandler("/im"))
                .addLast(new WebSocketHandler());
    }
}

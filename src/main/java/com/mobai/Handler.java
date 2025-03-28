package com.mobai;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Handler implements Runnable{
  private static final ExecutorService POOL = Executors.newFixedThreadPool(10);
  private final SocketChannel channel;
  public Handler(SocketChannel channel) {
    this.channel = channel;
  }

  @Override
  public void run() {
    try {
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      channel.read(buffer);
      buffer.flip();
      POOL.submit(() -> {
        try {
          System.out.println("接收到客户端数据："+new String(buffer.array(), 0, buffer.remaining()));
          channel.write(ByteBuffer.wrap("已收到！".getBytes()));
        }catch (IOException e){
          e.printStackTrace();
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
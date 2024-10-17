package com.mobai;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable{

  private final ServerSocketChannel serverChannel;

  public Acceptor(ServerSocketChannel serverChannel) {
    this.serverChannel = serverChannel;
  }

  @Override
  public void run() {
    try{
      SocketChannel channel = serverChannel.accept();
      System.out.println(Thread.currentThread().getName()+" >> 客户端已连接，IP地址为："+channel.getRemoteAddress());
      channel.configureBlocking(false);
      Selector selector = SubReactor.nextSelector();
      selector.wakeup();
      channel.register(selector, SelectionKey.OP_READ, new Handler(channel));
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
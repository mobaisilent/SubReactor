package com.mobai;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubReactor implements Runnable, Closeable {
  private final Selector selector;

  private static final ExecutorService POOL = Executors.newFixedThreadPool(4);
  private static final SubReactor[] reactors = new SubReactor[4];
  private static int selectedIndex = 0;

  static {
    for (int i = 0; i < 4; i++) {
      try {
        reactors[i] = new SubReactor();
        POOL.submit(reactors[i]);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static Selector nextSelector() {
    Selector selector = reactors[selectedIndex].selector;
    selectedIndex = (selectedIndex + 1) % 4;
    return selector;
  }

  private SubReactor() throws IOException {
    selector = Selector.open();
  }

  @Override
  public void run() {
    try {
      while (true) {
        int count = selector.select();
        System.out.println(Thread.currentThread().getName() + " >> 监听到 " + count + " 个事件");
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
          this.dispatch(iterator.next());
          iterator.remove();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void dispatch(SelectionKey key) {
    Object att = key.attachment();
    if (att instanceof Runnable) {
      ((Runnable) att).run();
    }
  }

  @Override
  public void close() throws IOException {
    selector.close();
  }
}
package com.nowcoder.mycommunity;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {
    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

class Producer implements Runnable{

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for(int i = 0; i < 100; ++ i){
                queue.put(i);
                Thread.sleep(20);
                System.out.println(Thread.currentThread().getName() + "   producer" + queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable{

    public BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                queue.take();
                Thread.sleep(new Random().nextInt(1000));
                System.out.println(Thread.currentThread().getName() + "   consumer" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
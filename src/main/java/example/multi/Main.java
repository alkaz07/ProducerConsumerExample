package example.multi;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static void main(String[] args) {
        //    exampleConcurrentLinkedQueue();
        //   exampleArrayBlockingQueue1();
        exampleArrayBlockingQueue2();
    }

    private static void exampleArrayBlockingQueue1() {
        Queue<String> q = new ArrayBlockingQueue<>(4);
        Producer p = new Producer("Света", q, null);
        Consumer c = new Consumer("Оля", q, 2, null);
        new Thread(p).start();
        new Thread(c).start();
    }

    private static void exampleArrayBlockingQueue2() {
        Witness w = new Witness();
        ArrayBlockingQueue<String> q = new ArrayBlockingQueue<>(4);
        Producer p = new BlockProducer("Грета", q, w);
        Consumer c = new Consumer("Мартин", q, 2, w);
        new Thread(p).start();
        new Thread(c).start();
    }

    private static void exampleConcurrentLinkedQueue() {
        Queue<String> q = new ConcurrentLinkedQueue<>();
        Producer p = new Producer("Вася", q, null);
        Consumer c = new Consumer("Гриша", q, 2, null);
        new Thread(p).start();
        new Thread(c).start();
    }
}

class Producer implements Runnable {
    String name;
    Queue<String> queue;
    Witness w;

    public Producer(String name, Queue<String> queue, Witness w) {
        this.name = name;
        this.queue = queue;
        this.w = w;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        System.out.println("Producer " + name + " начал работать");
        try {
            for (int i = 0; i < 10; i++) {
                String message = "сообщение " + i;
                queue.add(message);
                System.out.println(name + " сделал " + message);
                Thread.sleep(300);
            }
            System.out.println("Producer " + name + " остановился");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class Consumer implements Runnable {
    String name;
    Queue<String> queue;
    int minAmount;
    int amount = 0;
    Witness w;

    public Consumer(String name, Queue<String> queue, int minAmount, Witness w) {
        this.name = name;
        this.queue = queue;
        this.minAmount = minAmount;
        this.w = w;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        System.out.println("Consumer " + name + " запустился");
        String mes;
        while (queue.size() > 0 || amount < minAmount || (w != null && w.getFlag() )) {
            if ((mes = queue.poll()) != null)
                System.out.println(name + " прочитал " + mes);
            amount++;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Consumer " + name + " остановился");
    }
}

class BlockProducer extends Producer {

    public BlockProducer(String name, ArrayBlockingQueue<String> queue, Witness w) {
        super(name, queue, w);
    }

    @Override
    public void run() {
        System.out.println("Producer " + name + " начал работать");
        try {
            for (int i = 0; i < 10; i++) {
                String message = "сообщение " + i;
                //queue.add(message);
                System.out.println(name + " сделал " + message);
                ((ArrayBlockingQueue<String>) queue).put(message);
                System.out.println(name + " отправил " + message);
                Thread.sleep(600);
            }
            System.out.println("Producer " + name + " остановился");
            w.swicthOff();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class Witness {
    private Boolean flag = true;

    public Boolean getFlag() {
        return flag;
    }

    public void swicthOff() {
        flag = false;
    }
}
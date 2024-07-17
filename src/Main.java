import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static final int QUEUE100 = 100;
    public static final int TEXTS10_000 = 10_000;
    public static final int LENGTHTEXT = 100_000;
    public static final BlockingQueue<String> dropA = new ArrayBlockingQueue<>(QUEUE100, true);
    public static final BlockingQueue<String> dropB = new ArrayBlockingQueue<>(QUEUE100, true);
    public static final BlockingQueue<String> dropC = new ArrayBlockingQueue<>(QUEUE100, true);
    public static int maxCountA = 0;
    public static int maxCountB = 0;
    public static int maxCountC = 0;

    public static void main(String[] args) throws InterruptedException {

        long startTs = System.currentTimeMillis(); // start time
        List<Thread> threads = new ArrayList<>();

        Thread thread = new Thread(() -> { //10_000 текстов --- 1ый поток
            String text;
            for (int i = 0; i < TEXTS10_000; i++) {
                text = generateText("abc", LENGTHTEXT);
                //System.out.println(text);

                synchronized (dropA) {
                    while (dropA.size() == QUEUE100) {
                        try {
                            dropA.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (dropA.isEmpty()) {
                        dropA.notifyAll();
                    }
                    dropA.add(text);
                }
                synchronized (dropB) {
                    while (dropB.size() == QUEUE100) {
                        try {
                            dropB.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (dropB.isEmpty()) {
                        dropB.notifyAll();
                    }
                    dropB.add(text);
                }
                synchronized (dropC) {
                    while (dropC.size() == QUEUE100) {
                        try {
                            dropC.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (dropC.isEmpty()) {
                        dropC.notifyAll();
                    }
                    dropC.add(text);
                }
            }
        });
        threads.add(thread);
        thread.start();

        thread = new Thread(() -> { //текст, в котором содержится максимальное количество символов 'a'
            char ch = 'a';
            String str;
            for (int i = 0; i < TEXTS10_000; i++) {
                synchronized (dropA) {
                    if (dropA.isEmpty()) {
                        try {
                            dropA.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (dropA.size() == QUEUE100) {
                        dropA.notifyAll();
                    }
                    try {
                        str = dropA.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                int count = str.length() - str.replace(String.valueOf(ch), "").length();
                if (maxCountA < count) {
                    maxCountA = count;
                }
            }
        });
        threads.add(thread);
        thread.start();

        thread = new Thread(() -> { //текст, в котором содержится максимальное количество символов 'b'
            char ch = 'b';
            String str;
            for (int i = 0; i < TEXTS10_000; i++) {
                synchronized (dropB) {
                    if (dropB.isEmpty()) {
                        try {
                            dropB.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (dropB.size() == QUEUE100) {
                        dropB.notifyAll();
                    }
                    try {
                        str = dropB.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                int count = str.length() - str.replace(String.valueOf(ch), "").length();
                if (maxCountB < count) {
                    maxCountB = count;
                }
            }
        });
        threads.add(thread);
        thread.start();

        thread = new Thread(() -> { //текст, в котором содержится максимальное количество символов 'c'
            char ch = 'c';
            String str;
            for (int i = 0; i < TEXTS10_000; i++) {
                synchronized (dropC) {
                    if (dropC.isEmpty()) {
                        try {
                            dropC.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (dropC.size() == QUEUE100) {
                        dropC.notifyAll();
                    }
                    try {
                        str = dropC.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                int count = str.length() - str.replace(String.valueOf(ch), "").length();
                if (maxCountC < count) {
                    maxCountC = count;
                }
            }
        });
        threads.add(thread);
        thread.start();

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Текст, в котором содержится максимальное количество символов 'a': "
                + maxCountA);
        System.out.println("Текст, в котором содержится максимальное количество символов 'b': "
                + maxCountB);
        System.out.println("Текст, в котором содержится максимальное количество символов 'c': "
                + maxCountC);


        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
/// Time: 5 370 ms
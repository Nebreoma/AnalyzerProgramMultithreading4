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

//10_000 текстов --- 1ый поток
        Thread thread = new Thread(() -> {
            String text;
            for (int i = 0; i < TEXTS10_000; i++) {
                text = generateText("abc", LENGTHTEXT);
                //System.out.println(text);

                try {
                    dropA.put(text);
                    dropB.put(text);
                    dropC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        threads.add(thread);
        thread.start();

//текст, в котором содержится максимальное количество символов 'a'
        thread = new Thread(() -> {
            char ch = 'a';
            String str;
            for (int i = 0; i < TEXTS10_000; i++) {
                try {
                    str = dropA.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int count = str.length() - str.replace(String.valueOf(ch), "").length();
                if (maxCountA < count) {
                    maxCountA = count;
                }
            }
        });
        threads.add(thread);
        thread.start();

//текст, в котором содержится максимальное количество символов 'b'
        thread = new Thread(() -> {
            char ch = 'b';
            String str;
            for (int i = 0; i < TEXTS10_000; i++) {
                try {
                    str = dropB.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int count = str.length() - str.replace(String.valueOf(ch), "").length();
                if (maxCountB < count) {
                    maxCountB = count;
                }
            }
        });
        threads.add(thread);
        thread.start();

//текст, в котором содержится максимальное количество символов 'c'
        thread = new Thread(() -> {
            char ch = 'c';
            String str;
            for (int i = 0; i < TEXTS10_000; i++) {
                try {
                    str = dropC.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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
/// Time: 5 315 ms
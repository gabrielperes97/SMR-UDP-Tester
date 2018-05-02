package gabrielleopoldino.sddl.sectests.csv;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by gabriel on 18/05/17.
 */
public class CSVLogger {

    private BlockingQueue<Long> queue;
    private File file;
    private FileWriter fileWriter;
    private PrintWriter printWriter;
    private long count;
    private Writer thread;
    private int limit;

    public CSVLogger(String logPath, int limit) {
        this.limit = limit;
        queue = new LinkedBlockingQueue<>();
        file = new File(logPath);
        try {
            file.createNewFile();
            fileWriter = new FileWriter(file);
            printWriter = new PrintWriter(fileWriter);
            count = 0;
            thread = new Writer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public long getCount() {
        return count;
    }

    public void add(long time)
    {
        queue.add(time);
    }

    public void stop()
    {
        printWriter.flush();
        System.out.println("Limit reached! Exiting...");
    }

    private class Writer extends Thread
    {

        private int countM=0; //taxa de mensagens
        private Double med=0d;

        public Writer() {
            super("CSV Writer");
            start();
        }

        @Override
        public void run() {
            while (true)
            {
                try {
                    long time = queue.take();
                    if (time > 0) {
                        printWriter.printf("%d,%d\n", count, time);
                        //System.out.println("Ping "+ count+": " + (double) time/1000000);
                        count++;
                        countM++;
                        /*if (count > 10000) {
                            med += (double) time / 1000000;
                            //System.out.println("Ping medio: " + med / count);
                        }*/
                        if (countM >= 100) {
                            printWriter.flush();
                            countM = 0;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}

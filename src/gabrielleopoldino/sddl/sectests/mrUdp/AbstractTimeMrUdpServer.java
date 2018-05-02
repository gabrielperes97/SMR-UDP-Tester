package gabrielleopoldino.sddl.sectests.mrUdp;


import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Created by gabriel on 17/05/17.
 */
public abstract class AbstractTimeMrUdpServer {

    private ReliableServerSocket server;

    public AbstractTimeMrUdpServer(ReliableServerSocket server)
    {
        this.server = server;
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        new ConnectThread(server);

    }

    public class ConnectThread extends Thread
    {
        public ReliableServerSocket server;

        public ConnectThread(ReliableServerSocket server)
        {
            this.server = server;
            start();
        }

        public void run()
        {
            while (true) {
                try {
                    System.out.println("waiting");
                    new ClientThread((ReliableSocket) server.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ClientThread extends Thread
    {
        private ReliableSocket client;
        private PrintStream out;
        private BufferedReader in;
        private long startTime;
        private long finalTime;

        public ClientThread (ReliableSocket client)
        {
            this.client = client;
            try {
                out = new PrintStream(this.client.getOutputStream());
                in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            start();
        }

        public void run()
        {
            while (true)
            {
                Long msg = System.nanoTime();
                out.println(msg.toString());
                out.flush();

                try {
                    String rcv = in.readLine();
                    long time = System.nanoTime() - Long.parseLong(rcv);
                    System.out.println("Ping "+time/1000000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

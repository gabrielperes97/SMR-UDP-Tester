package gabrielleopoldino.sddl.sectests.handshakeTime;

import gabrielleopoldino.sddl.sectests.DtlsClient;
import gabrielleopoldino.sddl.sectests.DtlsServer;
import gabrielleopoldino.sddl.sectests.csv.CSVLogger;
import leopoldino.smrudp.SecureReliableServerSocket;
import leopoldino.smrudp.SecureReliableSocket;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

public class AbstractConnectTest {

    private ReliableServerSocket server;
    private ServerThread serverThread;

    private DtlsClient dtlsClient = new DtlsClient();

    private ReliableSocket client;

    private boolean secure;

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5500;
    private static final SocketAddress ENDPOINT = new InetSocketAddress(HOST, PORT);

    private CSVLogger csv;

    public AbstractConnectTest(boolean secure, String logName) throws IOException {
        this.secure = secure;
        if (secure) {
            this.server = new SecureReliableServerSocket(PORT, new DtlsServer());
        }
        else {
            this.server = new ReliableServerSocket(PORT);
        }

        csv = new CSVLogger(logName);

        this.serverThread = new ServerThread();
        this.serverThread.start();
    }

    private class ServerThread extends Thread
    {
        @Override
        public void run() {
            try {
                while (true) {
                    Socket c = server.accept();
                    c.getInputStream().read();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    long test() throws IOException {
        if (secure)
            client = new SecureReliableSocket(dtlsClient);
        else
            client = new ReliableSocket();
        long startTime = System.nanoTime();
        client.connect(ENDPOINT);
        long stopTime = System.nanoTime();
        client.getOutputStream().write(2);
        client.close();
        return stopTime-startTime;
    }

    void multipleTests(int samples)
    {
        LinkedList<Long> list = new LinkedList<Long>();
        for (int i=0; i<samples; i++)
        {
            if (server.isClosed())
            {
                try {
                    if (secure) {
                        this.server = new SecureReliableServerSocket(PORT, new DtlsServer());
                    } else {
                        this.server = new ReliableServerSocket(PORT);
                    }
                }catch(IOException e)
                {
                    e.printStackTrace();
                }
            }

            long l;
            try {
                l = test();
                csv.add(l);
                System.out.println(l);
            } catch (IOException e) {
                System.out.println("Erro ao medir handshake");
                e.printStackTrace();
            }
        }
    }

    void close()
    {
        csv.stop();
        this.serverThread.interrupt();
        this.server.close();
    }

    public static void main(String[] args)
    {
        boolean secure = false;
        int samples = 10000;
        boolean hasOut = false;
        String output = "handshakeTimerLog.csv";

        for (int i = 0; i < args.length; i++)
        {
            String s = args[i];
            if (s.equals("-s"))
                secure = true;
            else if (s.matches("\\d+\\b"))
            {
                samples = Integer.parseInt(s);
            }
            else if(s.equals("-out"))
            {
                hasOut = true;
                output = args[++i];
            }
        }


        AbstractConnectTest test = null;
        try {
            test = new AbstractConnectTest(secure, output);
        } catch (IOException e) {
            System.out.println("Erro ao carregar certificados");
            e.printStackTrace();
        }


        System.out.println("Iniciando testes");
        if (secure)
            System.out.println("Modo seguro");
        System.out.println(samples + " samples");
        test.multipleTests(samples);


        System.out.println("Testes realizados");
        System.exit(0);

    }
}

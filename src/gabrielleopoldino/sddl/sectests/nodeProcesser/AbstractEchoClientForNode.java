package gabrielleopoldino.sddl.sectests.nodeProcesser;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by gabriel on 11/05/17.
 */
public abstract class AbstractEchoClientForNode implements NodeConnectionListener{

    private MrUdpNodeConnection connection;
    private InetSocketAddress address;
    private long startTime;
    private long finalTime;
    private ExecutorService pool;
    private EchoThread echoThread;
    private BlockingQueue<Message> receivedSegments = new ArrayBlockingQueue<Message>(1024*10);

    public AbstractEchoClientForNode(MrUdpNodeConnection client, String host, int port)
    {
        System.out.println("Iniciando cliente no host "+host+":"+port);
        connection = client;
        this.address = new InetSocketAddress(host, port);
        try {
            connection.addNodeConnectionListener(this);
            //pool = new ThreadPoolExecutor(16, 16, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new ScheduleFactory("Receive-Scheduler"));
            pool = Executors.newCachedThreadPool(new ScheduleFactory("Receive-Scheduler"));
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String s, NodeConnection remoteCon)
    {
        ApplicationMessage msg = new ApplicationMessage();
        msg.setContentObject(s);
        try {
            remoteCon.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connected(NodeConnection remoteCon) {
        /*echoThread = new EchoThread(remoteCon);
        echoThread.start();*/
        sendMessage("S", remoteCon);
        System.out.println("Connectado");
    }

    @Override
    public void reconnected(NodeConnection remoteCon, SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {

    }

    @Override
    public void disconnected(NodeConnection remoteCon) {

    }

    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        pool.submit(new Echo(remoteCon, message));

        /*message.setRecipientGatewayID(null);
        message.setSenderGatewayID(null);
        message.setSenderID(null);
        message.setRecipientID(null);
        try {
            remoteCon.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();

        }*/
        /*try {
            receivedSegments.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {

    }

    @Override
    public void internalException(NodeConnection remoteCon, Exception e) {

    }

    protected class EchoThread extends Thread
    {

        private NodeConnection remoteCon;
        Message message = null;

        public EchoThread(NodeConnection remoteCon) {
            this.remoteCon = remoteCon;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    message = receivedSegments.take();
                    message.setRecipientGatewayID(null);
                    message.setSenderGatewayID(null);
                    message.setSenderID(null);
                    message.setRecipientID(null);
                    remoteCon.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected class Echo implements Runnable {
        private NodeConnection remoteCon;
        private Message        message;

        public Echo(NodeConnection remoteCon, Message message) {
            this.remoteCon = remoteCon;
            this.message = message;
        }

        @Override
        public void run() {
                Long timeIni = System.nanoTime();
                message.setRecipientGatewayID(null);
                message.setSenderGatewayID(null);
                message.setSenderID(null);
                message.setRecipientID(null);
                try {
                    remoteCon.sendMessage(message);
                    Wrapper obj = (Wrapper) message.getContentObject();
                    System.out.println((double)(timeIni-obj.getTime())/1000000);
                    if (obj.isEndOfCommunication()) {
                        this.remoteCon.disconnect();
                        System.exit(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
        }
    }

    public static class ScheduleFactory implements ThreadFactory {
        private String name;
        private int counter;

        public ScheduleFactory(String name) {
            this.counter = 1;
            this.name    = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, name + "-" + counter++);
        }
    }
}

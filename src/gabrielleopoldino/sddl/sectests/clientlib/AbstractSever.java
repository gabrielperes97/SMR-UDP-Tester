package gabrielleopoldino.sddl.sectests.clientlib;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.NodeConnectionServerListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnectionServer;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by gabriel on 12/05/17.
 */
public abstract class AbstractSever implements NodeConnectionServerListener,
        NodeConnectionListener {

    public MrUdpNodeConnectionServer server;
    private final static Logger LOGGER = Logger.getLogger(AbstractSever.class.getCanonicalName());
    private Instant startTime;
    private Instant finalTime;
    private UUID client;

    public AbstractSever(MrUdpNodeConnectionServer server)
    {
        this.server = server;
        this.server.addListener(this);
        this.server.start();
        System.out.println("Wainting");
    }

    @Override
    public void newNodeConnection(NodeConnection remoteCon) {
        remoteCon.addNodeConnectionListener(this);
    }

    @Override
    public void connected(NodeConnection remoteCon) {

    }

    @Override
    public void reconnected(NodeConnection remoteCon, SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {

    }

    @Override
    public void disconnected(NodeConnection remoteCon) {

    }

    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        Long time = System.nanoTime();
        if (message.getContentObject() != null) {
            Long msg = (Long) message.getContentObject();
            time = time - msg;

            System.out.println("Ping " + (double) time / 1000000);

            sendTime(remoteCon);
        }
        else
        {
            client = remoteCon.getUuid();
            sendTime(remoteCon);
        }

    }

    @Override
    public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {

    }

    @Override
    public void internalException(NodeConnection remoteCon, Exception e) {

    }

    private void sendTime(NodeConnection remoteCon)
    {
        Long nano = System.nanoTime();
        sendMessage(client, nano, remoteCon);
    }

    protected static void sendMessage(UUID uuid, Serializable content, NodeConnection remoteCon)
    {
        ApplicationMessage msg = new ApplicationMessage();
        msg.setRecipientID(uuid);
        msg.setContentObject(content);
        try {
            remoteCon.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

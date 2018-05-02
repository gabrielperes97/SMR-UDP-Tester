package gabrielleopoldino.sddl.sectests.clientlib;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

/**
 * Created by gabriel on 11/05/17.
 */
public abstract class AbstractEchoClient implements NodeConnectionListener{

    private MrUdpNodeConnection connection;
    private InetSocketAddress address;
    private long startTime;
    private long finalTime;

    public AbstractEchoClient(MrUdpNodeConnection client, InetSocketAddress address)
    {
        connection = client;
        this.address = address;
        try {
            connection.addNodeConnectionListener(this);
            connection.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connected(NodeConnection remoteCon) {
        System.out.println("Connected");
        AbstractSever.sendMessage(remoteCon.getUuid(), null, remoteCon);
    }

    @Override
    public void reconnected(NodeConnection remoteCon, SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {

    }

    @Override
    public void disconnected(NodeConnection remoteCon) {

    }

    @Override
    public void newMessageReceived(NodeConnection remoteCon, Message message) {
        UUID temp = message.getRecipientID();
        message.setRecipientID(message.getSenderID());
        message.setSenderID(temp);
        message.setSenderGatewayID(null);
        message.setRecipientGatewayID(null);

        try {
            remoteCon.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {

    }

    @Override
    public void internalException(NodeConnection remoteCon, Exception e) {

    }
}

package gabrielleopoldino.sddl.sectests.nodeProcesser;

import lac.cnclib.net.mrudp.MrUdpNodeConnection;

import java.io.IOException;

/**
 * Created by gabriel on 11/05/17.
 */
public class EchoClientForNode extends AbstractEchoClientForNode {


    private static String gatewayIP = "127.0.0.1";
    private static int gatewayPort = 5500;

    public static void main(String[] args)
    {
        try {
            new EchoClientForNode(gatewayIP, gatewayPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EchoClientForNode(String host, int port) throws IOException {
        super(new MrUdpNodeConnection(), host, port);
    }
}

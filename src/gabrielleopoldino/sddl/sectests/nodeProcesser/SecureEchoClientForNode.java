package gabrielleopoldino.sddl.sectests.nodeProcesser;

import gabrielleopoldino.sddl.sectests.DtlsClient;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import leopoldino.secureclientlib.net.mrudp.SmrUdpNodeConnection;

import java.io.IOException;

/**
 * Created by gabriel on 11/05/17.
 */
public class SecureEchoClientForNode extends AbstractEchoClientForNode {

    private static String gatewayIP = "127.0.0.1";
    private static int gatewayPort = 5500;

    public static void main(String[] args)
    {
        try {
            new SecureEchoClientForNode(gatewayIP, gatewayPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SecureEchoClientForNode(String host, int port) throws IOException {
        super((MrUdpNodeConnection) new SmrUdpNodeConnection(new DtlsClient()), host, port);
    }
}

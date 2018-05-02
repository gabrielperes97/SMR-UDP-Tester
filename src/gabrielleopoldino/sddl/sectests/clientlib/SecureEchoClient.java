package gabrielleopoldino.sddl.sectests.clientlib;

import gabrielleopoldino.sddl.sectests.DtlsClient;
import leopoldino.secureclientlib.net.mrudp.SmrUdpNodeConnection;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by gabriel on 11/05/17.
 */
public class SecureEchoClient extends AbstractEchoClient {

    private static String gatewayIP = "127.0.0.1";
    private static int gatewayPort = 5501;

    public static void main(String[] args)
    {
        try {
            new SecureEchoClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SecureEchoClient() throws IOException {
        super(new SmrUdpNodeConnection(new DtlsClient()), new InetSocketAddress(gatewayIP, gatewayPort));
    }
}

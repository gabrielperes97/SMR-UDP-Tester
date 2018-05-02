package gabrielleopoldino.sddl.sectests.clientlib;

import lac.cnclib.net.mrudp.MrUdpNodeConnection;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by gabriel on 11/05/17.
 */
public class EchoClient extends AbstractEchoClient {


    private static String gatewayIP = "127.0.0.1";
    private static int gatewayPort = 5500;

    public static void main(String[] args)
    {
        try {
            new EchoClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EchoClient() throws IOException {
        super(new MrUdpNodeConnection(), new InetSocketAddress(gatewayIP, gatewayPort));
    }
}

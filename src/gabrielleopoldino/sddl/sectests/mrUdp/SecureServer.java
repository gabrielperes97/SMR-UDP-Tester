package gabrielleopoldino.sddl.sectests.mrUdp;

import gabrielleopoldino.sddl.sectests.DtlsServer;
import leopoldino.smrudp.SecureReliableServerSocket;

import java.io.IOException;

/**
 * Created by gabriel on 17/05/17.
 */
public class SecureServer extends AbstractTimeMrUdpServer{

    public SecureServer() throws IOException {
        super(new SecureReliableServerSocket(5500, new DtlsServer()));
    }

    public static void main(String[] args)
    {
        try {
            new SecureServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

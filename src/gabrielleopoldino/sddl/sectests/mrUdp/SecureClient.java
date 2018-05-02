package gabrielleopoldino.sddl.sectests.mrUdp;

import gabrielleopoldino.sddl.sectests.DtlsClient;
import leopoldino.smrudp.SecureReliableSocket;

import java.io.IOException;

/**
 * Created by gabriel on 17/05/17.
 */
public class SecureClient extends AbstractEchoMrUdpClient {
    public SecureClient() throws IOException {
        super(new SecureReliableSocket("127.0.0.1", 5500, new DtlsClient()));
    }

    public static void main (String[] args)
    {
        try {
            new SecureClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

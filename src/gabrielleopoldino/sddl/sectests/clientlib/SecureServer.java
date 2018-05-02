package gabrielleopoldino.sddl.sectests.clientlib;

import gabrielleopoldino.sddl.sectests.DtlsServer;
import leopoldino.secureclientlib.net.mrudp.SmrUdpNodeConnectionServer;

import java.io.IOException;

/**
 * Created by gabriel on 13/05/17.
 */
public class SecureServer extends AbstractSever {
    public static void main(String[] args)
    {
        try {
            new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SecureServer() throws IOException {
        super(new SmrUdpNodeConnectionServer(5501, new DtlsServer()));
    }
}

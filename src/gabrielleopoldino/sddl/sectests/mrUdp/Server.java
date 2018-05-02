package gabrielleopoldino.sddl.sectests.mrUdp;

import net.rudp.ReliableServerSocket;

import java.io.IOException;

/**
 * Created by gabriel on 17/05/17.
 */
public class Server extends AbstractTimeMrUdpServer{

    public Server() throws IOException {
        super(new ReliableServerSocket(5500));
    }

    public static void main(String[] args)
    {
        try {
            new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package gabrielleopoldino.sddl.sectests.mrUdp;

import net.rudp.ReliableSocket;

import java.io.IOException;

/**
 * Created by gabriel on 17/05/17.
 */
public class Client extends AbstractEchoMrUdpClient {
    public Client() throws IOException {
        super(new ReliableSocket("127.0.0.1", 5500));
    }

    public static void main (String[] args)
    {
        try {
            new Client();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

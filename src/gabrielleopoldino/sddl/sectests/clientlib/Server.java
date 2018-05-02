package gabrielleopoldino.sddl.sectests.clientlib;

import lac.cnclib.net.mrudp.MrUdpNodeConnectionServer;

import java.io.IOException;

/**
 * Created by gabriel on 12/05/17.
 */
public class Server extends AbstractSever {

    public static void main(String[] args)
    {
        try {
            new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server() throws IOException {
        super(new MrUdpNodeConnectionServer(5500));
    }
}

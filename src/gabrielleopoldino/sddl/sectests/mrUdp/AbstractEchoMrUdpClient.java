package gabrielleopoldino.sddl.sectests.mrUdp;

import net.rudp.ReliableSocket;

import java.io.*;

/**
 * Created by gabriel on 17/05/17.
 */
public abstract class AbstractEchoMrUdpClient {

    private ReliableSocket client;
    private OutputStream out;
    private InputStream in;
    private byte[] buffer;
    private int tamRcv;

    public AbstractEchoMrUdpClient(ReliableSocket client) {
        this.client = client;
        try {
            out = this.client.getOutputStream();
            in = this.client.getInputStream();
            buffer = new byte[65535];
        } catch (IOException e) {
            e.printStackTrace();
        }
        new EchoThread();
    }

    private void echo()
    {
        try {
            tamRcv = in.read(buffer);
            out.write(buffer, 0, tamRcv);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class EchoThread extends Thread
    {
        public EchoThread()
        {
            start();
        }

        public void run()
        {
            while (true)
            {
                echo();
            }
        }
    }


}

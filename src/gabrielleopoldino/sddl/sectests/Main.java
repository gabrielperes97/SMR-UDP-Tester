package gabrielleopoldino.sddl.sectests;

import gabrielleopoldino.sddl.sectests.nodeProcesser.EchoClientForNode;
import gabrielleopoldino.sddl.sectests.nodeProcesser.SecureEchoClientForNode;
import gabrielleopoldino.sddl.sectests.nodeProcesser.TimerProcessingNode;
import lac.cnet.gateway.tool.GatewayTest;

import java.io.IOException;

/**
 * Created by gabriel on 13/05/17.
 */
public class Main {
    public static void main (String[] args) throws IOException {
        boolean isServer =false;
        boolean isClient = false;
        boolean isSecure = false;
        boolean isGateway = false;
        int port=5500;
        String host="127.0.0.1";
        int tx=0;
        int packageLength=1;
        int pingsLength=0;
        String logName = "log.csv";

        for (int i = 0; i < args.length; i++) {
            switch (args[i])
            {
                case "-S":
                    isServer = true;
                    break;
                case "-C":
                    isClient = true;
                    break;
                case "-G":
                    isGateway = true;
                    break;
                case "-s":
                    isSecure = true;
                    break;
                case "-h":
                    i++;
                    host = args[i];
                    break;
                case "-p":
                    i++;
                    port = Integer.parseInt(args[i]);
                    break;
                case "-tx":
                    i++;
                    tx = Integer.parseInt(args[i]);
                    break;
                case "-l":
                    i++;
                    packageLength = Integer.parseInt(args[i]);
                    break;
                case "-o":
                    i++;
                    logName = args[i];
                    break;
                case "-pl":
                    i++;
                    pingsLength = Integer.parseInt(args[i]);
                    break;
                default:
                    System.out.println("Argumento inválido: "+args[i]);
            }
        }
        int countTrues=0;
        if (isClient) countTrues++;
        if (isGateway) countTrues++;
        if (isServer) countTrues++;
        if (countTrues != 1)
        {
            System.out.println("Cliente:");
            System.out.println(" -C [-s -h host -p port]");
            System.out.println("Servidor:");
            System.out.println(" -S [-tx taxa_de_envio -l tamanho_do_blooper -pl quantidade_de_pings -o nome_do_log.csv]");
            System.out.println("Gateway:");
            System.out.println(" -G [-s -h host -p port]");
        }

        if (isServer)
        {
            new TimerProcessingNode(tx, packageLength, logName, pingsLength);
        }else
        {
            if (isClient) {
                if (isSecure) {
                    System.out.println("Iniciando client seguro");
                    new SecureEchoClientForNode(host, port);
                } else {
                    new EchoClientForNode(host, port);
                }
            }else
            {
                if (isGateway){
                    if (isSecure)
                    {
                        SecureGatewayTest.main(new String[]{host, String.valueOf(port), "ospl"});
                    }
                    else
                    {
                        GatewayTest.main(new String[]{host, String.valueOf(port), "ospl"});
                    }
                }
            }
        }
    }
}

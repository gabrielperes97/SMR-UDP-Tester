package gabrielleopoldino.sddl.sectests;

import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import leopoldino.securegateway.SecureGateway;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a simple version of a SecureGateway. I don't put this class in the source tree because the
 * secure configurations (TlsServer) vary according to the use and the test configurations are not many secure.
 *
 * @author Gabriel Leopoldino
 */
public class SecureGatewayTest {
    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.OFF);
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        if (args.length < 3) {
            System.out.println("Call syntax: $SecureGatewayTest <gateway-public-ip> <gateway-RUDP-port> <dds-vendor>");
            System.exit(-1);
        }

        UUID id = UUID.randomUUID();

        String strDDSVendor = args[2];
        UniversalDDSLayerFactory.SupportedDDSVendors ddsVendor = UniversalDDSLayerFactory.convertStrToSupportedDDSVendor(strDDSVendor);

        try {
            new SecureGateway(Integer.parseInt(args[1]), args[0], id, false, ddsVendor, new DtlsServer());

            System.out.println("Secure Gateway started...");
            System.out.println("Secure Gateway MR-UDP IP: " + args[0] + ":" + args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

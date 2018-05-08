import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.*;

import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        // create connection information
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost("localhost");
        ci.setDomain("");
        ci.setUser("opc");
        ci.setPassword("opc");
        //ci.setProgId("Matrikon.OPC.Simulation");
        ci.setClsid("f8582cf2-88fb-11d0-b850-00c0f0104305"); // if ProgId is not working, try it using the Clsid instead
        final String itemId = "Random.UInt4";
        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            // connect to server
            server.connect();
            // add sync access, poll every 500 ms
            final AccessBase access = new SyncAccess(server, 500);
            access.addItem(itemId, new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    try {
                        System.out.println(item.getGroup().getName() + ": " + state.getValue().getObjectAsUnsigned().getValue());
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                }
            });
            // start reading
            access.bind();
            // wait a little bit
            Thread.sleep(2 * 1000);
            // stop reading
            access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }

}

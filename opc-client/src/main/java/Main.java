import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.*;

import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws Exception {

        // 关闭初始化日志信息
        Logger.getLogger ( "org.jinterop" ).setLevel(Level.WARNING);

        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost("localhost");
        ci.setDomain("");
        ci.setUser("opc");
        ci.setPassword("opc");

        //ci.setProgId("Matrikon.OPC.Simulation");
        ci.setClsid("f8582cf2-88fb-11d0-b850-00c0f0104305"); // if ProgId is not working, try it using the Clsid instead

        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
        try {
            server.connect();

            // add sync access, poll every 500 ms
            final AccessBase access = new SyncAccess(server, 500);

            access.addItem("Random.UInt4", new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    try {
                        System.out.println(state.getTimestamp().getTime() + " ==> " + item.getGroup().getName() + ", " + item.getId() + "]: " +  state.getValue().getObjectAsUnsigned().getValue());
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                }
            });

            access.addItem("Random.String", new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    try {
                        System.out.println(state.getTimestamp().getTime() + " ==> [" + item.getGroup().getName() + ", " + item.getId() + "]: " + state.getValue().getObjectAsString2());
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                }
            });

            // start reading
            access.bind();
            // wait a little bit
            Thread.sleep(100 * 1000);
            // stop reading
            access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }

}

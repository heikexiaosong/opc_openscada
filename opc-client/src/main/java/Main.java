import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.common.JISystem;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.*;

import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OPC server tag 数据读取
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // 关闭初始化日志信息
        Logger.getLogger ( "org.jinterop" ).setLevel(Level.WARNING);

        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost(Env.HOST);
        ci.setDomain(Env.DOMAIN);
        ci.setUser(Env.USER);
        ci.setPassword(Env.PASSWORD);

        //ci.setProgId(Env.PROGID);
        ci.setClsid(Env.CLSID); // if ProgId is not working, try it using the Clsid instead

        final Server server = new Server(ci, Executors.newScheduledThreadPool(10));
        try {
            server.connect();

            // add sync access, poll every 500 ms
            final AccessBase access = new SyncAccess(server, 200);

            access.addItem("Random.UInt4", new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " - " + state.getTimestamp().getTime() + " ==> " + item.getGroup().getName() + ", " + item.getId() + "]: " +  state.getValue().getObjectAsUnsigned().getValue());
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                }
            });

            access.addItem("Random.String", new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " - " + state.getTimestamp().getTime() + " ==> [" + item.getGroup().getName() + ", " + item.getId() + "]: " + state.getValue().getObjectAsString2());
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

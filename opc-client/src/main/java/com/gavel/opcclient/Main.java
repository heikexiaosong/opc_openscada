package com.gavel.opcclient;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.IOPCDataCallback;
import org.openscada.opc.dcom.da.ValueData;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OPC server tag 数据读取
 *
 */
public class Main {

    private static List<String> getItemIds(){
        List<String> result = new ArrayList<>();

        result.add("Random.UInt4");

        result.add("Random.String");

        result.add("Random.Boolean");

        result.add("Square Waves.UInt2");

        return result;
    }

    public static void main(String[] args) throws Exception {

        // 关闭初始化日志信息
        Logger.getLogger ( "org.jinterop" ).setLevel(Level.WARNING);

        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost(Env.HOST);
        ci.setDomain(Env.DOMAIN);
        ci.setUser(Env.USER);
        ci.setPassword(Env.PASSWORD);

        //ci.setProgId(com.gavel.opcclient.Env.PROGID);
        ci.setClsid(Env.CLSID); // if ProgId is not working, try it using the Clsid instead

        final Server server = new Server(ci, Executors.newScheduledThreadPool(10));
        try {
            server.connect();

            Group group = server.addGroup ( "group1" );
            group.setActive(true);

            System.out.println("item: " + group.addItem ( "Random.UInt4" ));

            for (Map.Entry<String, Item> stringItemEntry : group.addItems(new String[]{"Random.String", "Random.Boolean"}).entrySet()) {
                System.out.println(stringItemEntry.getKey() + " ===============  " + stringItemEntry.getValue().read(true).getValue().getType());
            }

//            // Items are initially active ... just for demonstration
//            item.setActive ( true );
//            item.write(new JIVariant("121"));
//
//            System.out.println(item.read(true).getValue().getObjectAsUnsigned().getValue() + "  <------==");
            //========================================

            System.out.println("AccessBase");

            // add sync access, poll every 500 ms
            final AccessBase access = new SyncAccess(server, 200);

            for ( String itemId : getItemIds()) {
                try {
                    access.addItem(itemId, new DataCallback() {
                        @Override
                        public void changed(Item item, ItemState state) {
                            try {
                                Object value;
                                switch ( state.getValue().getType() ) {
                                    case 8:
                                        value = state.getValue().getObjectAsString().getString();
                                        break;
                                    case 11:
                                        value = state.getValue().getObjectAsBoolean();
                                        break;
                                    case 18:
                                        value = state.getValue().getObjectAsUnsigned().getValue();
                                        break;
                                    case 19:
                                        value = state.getValue().getObjectAsUnsigned().getValue();
                                        break;
                                    default:
                                        value = state.getValue().getObject();
                                }

                                System.out.println(Thread.currentThread().getName() + " - "
                                        + state.getTimestamp().getTime() + " ==> [Group: " + item.getGroup().getName() + ", ItemId: "
                                        + item.getId() + "]: " +  state.getValue().getType() + " - " + value);
                            } catch (JIException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }

            // start reading
            access.bind();
            // wait a little bit
            Thread.sleep(100 * 1000);
            // stop reading
            access.unbind();
        } catch (final JIException e) {
            e.printStackTrace();
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }

}

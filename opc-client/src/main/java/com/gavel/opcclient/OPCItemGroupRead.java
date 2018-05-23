package com.gavel.opcclient;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.opc.lib.da.browser.Leaf;
import org.openscada.opc.lib.da.browser.TreeBrowser;

/**
 * OPC server Item 分组读取
 *
 */
public class OPCItemGroupRead {

    public static void main(String[] args) throws Exception {

        // 关闭初始化日志信息
        Logger.getLogger ( "org.jinterop" ).setLevel(Level.WARNING);

        OpcClient opcClient = OpcClient.buildOpcClient();
        try {
            opcClient.connect();

            final Group group = opcClient.getServer().addGroup( "group_test" );
            group.setActive(true);

            List<String> itemids = opcClient.getItemids("Channel1.Device1.Tag*");
            Map<String, Item> itemMap =  group.addItems(itemids.toArray(new String[itemids.size()]));

            final Item[] items = new Item[itemMap.size()];
            int i = 0;
            for (Item item : itemMap.values()) {
                items[i++] = item;
            }

            final Calendar calendar = Calendar.getInstance();
            // add a thread for writing a value every 3 seconds
            ScheduledExecutorService writeThread = Executors.newSingleThreadScheduledExecutor();
            writeThread.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    StringBuilder msg = new StringBuilder();
                    try {
                        long timestamp = System.currentTimeMillis();
                        msg.append(calendar.getTime()).append("(").append(timestamp%1000).append("): ");
                        Map<Item, ItemState> itemItemStateMap = group.read(false, items);
                        msg.append("[Item: ").append(itemItemStateMap.size()).append("]Read: " + (System.currentTimeMillis() - timestamp) + " ms, ");
                        timestamp = System.currentTimeMillis();
                        for (Item item : itemItemStateMap.keySet()) {
                            ItemState state = itemItemStateMap.get(item);
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

//                    System.out.println(Thread.currentThread().getName() + " - "
//                        + state.getTimestamp().getTime() + " ==> [Group: " + item.getGroup().getName() + ", ItemId: "
//                        + item.getId() + "][Quality" + state.getQuality() + "]: " +  state.getValue().getType() + " - " + value);
                            } catch (JIException e) {
                                e.printStackTrace();
                            }
                        }

                        msg.append("Show: " + (System.currentTimeMillis() - timestamp) + " ms\n");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    System.out.println(msg);
                }
            }, 5000, 800, TimeUnit.MILLISECONDS);

            // wait a little bit
            Thread.sleep(15 * 1000);
            writeThread.shutdownNow();


        } catch (final JIException e) {
            e.printStackTrace();
            System.out.println(String.format("%08X: %s", e.getErrorCode())); //, server.getErrorMessage(e.getErrorCode())
        }
    }
}

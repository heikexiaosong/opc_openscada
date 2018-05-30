package com.gavel.opcclient;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
 */
public class OPCItemGroupRead {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss (SSS)");

    private static final Lock LOCK = new ReentrantLock();
    private static final Condition STOP = LOCK.newCondition();

    private static final Map<Group, Item[]> groupItemMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {

        // 关闭初始化日志信息
        Logger.getLogger("org.jinterop").setLevel(Level.WARNING);

        OpcClient opcClient = OpcClient.buildOpcClient();
        try {
            opcClient.connect();

            System.out.print("获取OPC Sercer Tag 列表 ....... ");
            List<String> itemids = opcClient.getItemids("Channel1.Device1.Tag*");
            System.out.println(  itemids.size() + " 个. [SUCCESS]\n");
            int i = 0;

            final Group group = opcClient.getServer().addGroup("group_test");
            group.setActive(true);
            Map<String, Item> itemMap = group.addItems(itemids.toArray(new String[itemids.size()]));
            final Item[] items = new Item[itemMap.size()];
            i = 0;
            for (Item item : itemMap.values()) {
                items[i++] = item;
            }
            groupItemMap.put(group, items);


            final Group group_half = opcClient.getServer().addGroup("group_half");
            group_half.setActive(true);
            List<String> itemids_half = itemids.subList(0, itemids.size()/2);
            Map<String, Item> itemHalfMap = group_half.addItems(itemids_half.toArray(new String[itemids_half.size()]));
            final Item[] items_half = new Item[itemHalfMap.size()];
            i = 0;
            for (Item item : itemHalfMap.values()) {
                items_half[i++] = item;
            }
            groupItemMap.put(group_half, items_half);

            final Calendar calendar = Calendar.getInstance();
            final StringBuilder ouput = new StringBuilder();
            // add a thread for writing a value every 3 seconds
            ScheduledExecutorService writeThread = Executors.newSingleThreadScheduledExecutor();
            writeThread.scheduleWithFixedDelay(new Runnable() {

                private void readGroupTags(Group group,  final Item[] items, final boolean sync, StringBuilder msg){
                    Calendar calendar = Calendar.getInstance();
                    try {
                        long timestamp = System.currentTimeMillis();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        msg.append("[Group: ").append(group.getName()).append(", Tags: ").append(items==null ? 0 : items.length);
                        if ( sync ){
                            msg.append("]同步读取 -- 实时数据\n");
                        } else {
                            msg.append("]异步读取 -- 使用缓存\n");
                        }
                        msg.append(DATE_FORMAT.format(calendar.getTime())).append("]");
                        Map<Item, ItemState> itemItemStateMap = group.read(sync, items);
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        msg.append("[服务端响应: ").append(DATE_FORMAT.format(calendar.getTime())).append("]");
                        msg.append("[Item: ").append(itemItemStateMap.size()).append("]Read Cost Time: " + (System.currentTimeMillis() - timestamp) + " ms");
                        timestamp = System.currentTimeMillis();
                        ouput.delete(0, ouput.length());
                        for (Item item : itemItemStateMap.keySet()) {
                            ItemState state = itemItemStateMap.get(item);
                            try {
                                Object value;
                                switch (state.getValue().getType()) {
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

                                if ( ouput.length() == 0 ) {

                                    //Wed May 30 10:07:20 CST 2018 ==> [Group: group_test, ItemId: Channel1.Device1.Tag6397][Quality192]: 18 - 2265
                                    ouput.append("[Example][Timestamp: ").append(DATE_FORMAT.format(state.getTimestamp().getTime())).append("][Group: " + item
                                        .getGroup().getName() + ", ItemId: "
                                        + item.getId() + "][Quality: " + state.getQuality() + "]Value: " + value);
                                }


                            } catch (JIException e) {
                                e.printStackTrace();
                            }
                        }

                        msg.append("Parse Value: " + (System.currentTimeMillis() - timestamp) + " ms");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(msg);
                    System.out.println(ouput);
                }

                @Override
                public void run() {
                    StringBuilder msg = new StringBuilder();
                    for (Entry<Group, Item[]> entry : groupItemMap.entrySet()) {
                        msg.delete(0, msg.length());
                        readGroupTags(entry.getKey(), entry.getValue(), false, msg);

                        msg.delete(0, msg.length());
                        readGroupTags(entry.getKey(), entry.getValue(), true, msg);

                        System.out.println("");
                    }
                }
            }, 1000, 800, TimeUnit.MILLISECONDS);

            //主线程阻塞等待，守护线程释放锁后退出
            try {
                LOCK.lock();
                STOP.await();
            } catch (InterruptedException e) {
                System.out
                    .println(" service   stopped, interrupted by other thread: " + e.getMessage());
            } finally {
                LOCK.unlock();
                writeThread.shutdownNow();
            }

        } catch (final JIException e) {
            e.printStackTrace();
            System.out.println(String
                .format("%08X: %s", e.getErrorCode())); //, server.getErrorMessage(e.getErrorCode())
        }
    }
}

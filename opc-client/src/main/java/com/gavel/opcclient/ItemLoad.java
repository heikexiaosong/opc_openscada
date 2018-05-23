package com.gavel.opcclient;

import com.gavel.database.h2.DBWriter;
import com.gavel.database.h2.H2Connection;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  获取OPC 服务器中的 特定的 itemid 并存储到 H2 数据库文件中
 */
public class ItemLoad {

  public static void main(String[] args) {

    // 关闭初始化日志信息
    Logger.getLogger ( "org.jinterop" ).setLevel(Level.WARNING);

    OpcClient opcClient = OpcClient.buildOpcClient();
    try {
      opcClient.connect();

      List<String> itemids = opcClient.getItemids("Channel1.Device1.Tag*");

      System.out.println("Get Itemids: " + itemids.size());
      Connection conn = H2Connection.getConnection();
      for (String itemid : itemids) {
        DBWriter.saveItemGroup(conn, itemid, "group1");
      }
      conn.commit();
      conn.close();
      System.out.println("Get Itemids finish.");
    } catch (Exception e){
      e.printStackTrace();
    }

  }
}

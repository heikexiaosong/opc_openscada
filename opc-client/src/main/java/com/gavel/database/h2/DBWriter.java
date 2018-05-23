package com.gavel.database.h2;

import java.sql.Connection;
import java.sql.Statement;

public class DBWriter {

  public static void saveItemGroup(Connection conn, String itemid, String groupname) throws Exception {
    Statement stmt = conn.createStatement();
    stmt.execute("CREATE TABLE IF NOT EXISTS GROUPINFO(ITEMID VARCHAR(128) PRIMARY KEY, GROUPNAME VARCHAR(255));");
    stmt.execute("insert into GROUPINFO (ITEMID, GROUPNAME) values ('" + itemid + "', '" + groupname + "');");
    stmt.close();
  }

}

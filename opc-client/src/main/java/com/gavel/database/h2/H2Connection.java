package com.gavel.database.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class H2Connection {

  static {
    try {
      Class.forName("org.h2.Driver");
      Connection conn = DriverManager.getConnection("jdbc:h2:./db/test", "sa", "");
      // add application code here
      Statement stmt = conn.createStatement();
      stmt.execute("CREATE TABLE IF NOT EXISTS GROUPINFO(ITEMID VARCHAR(128) PRIMARY KEY, GROUPNAME VARCHAR(255));");
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection() throws Exception {
    Connection conn = DriverManager.getConnection("jdbc:h2:./db/test", "sa", "");
    return conn;
  }

}

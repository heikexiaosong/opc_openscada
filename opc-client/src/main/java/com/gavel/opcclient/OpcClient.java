package com.gavel.opcclient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.browser.BaseBrowser;

public class OpcClient {

  private Server server;

  private List<String> itemids = new ArrayList<>();

  private OpcClient() {
  }

  private OpcClient(Server server) {
    this.server = server;
  }

  public static OpcClient buildOpcClient() {
    final ConnectionInformation ci = new ConnectionInformation();
    ci.setHost(Env.HOST);
    ci.setDomain(Env.DOMAIN);
    ci.setUser(Env.USER);
    ci.setPassword(Env.PASSWORD);
    ci.setClsid(Env.CLSID);
    final Server _server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

    return new OpcClient(_server);
  }

  /**
   * 登陆 OPC Server
   */
  public void connect() throws Exception {
    if (server == null) {
      throw new Exception("OPC Server Info not init.");
    }
    server.connect();

  }

  public List<String> getItemids(final String filterCriteria) throws Exception {
    if (server == null) {
      throw new Exception("OPC Server Info not init.");
    }

    final BaseBrowser flatBrowser = server.getFlatBrowser();
    itemids.clear();
    if (flatBrowser != null) {
      for (final String item : server.getFlatBrowser().browse(filterCriteria)) {
        itemids.add(item);
      }
    }

    return itemids;
  }

  public Server getServer() {
    return server;
  }
}

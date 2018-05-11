package com.gavel.opcclient;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.dcom.list.ClassDetails;
import org.openscada.opc.lib.list.Categories;
import org.openscada.opc.lib.list.Category;
import org.openscada.opc.lib.list.ServerList;

/**
 * OPC server 接口列表信息
 *
 */
public class ClassDetailShow {

    protected static void showDetails(final ServerList serverList, final String clsid) throws JIException {
        final ClassDetails cd = serverList.getDetails(clsid);
        if (cd != null) {
            System.out.println(cd.getProgId() + " = " + cd.getDescription());
        } else {
            System.out.println("unknown");
        }
    }

    public static void main(final String[] args) throws Throwable {
        // 关闭初始化日志信息
        Logger.getLogger("org.jinterop").setLevel(Level.WARNING);

        final ServerList serverList = new ServerList(Env.HOST, Env.USER, Env.PASSWORD, Env.DOMAIN);

        final String cls = serverList.getClsIdFromProgId("Matrikon.OPC.Simulation.1");
        System.out.println("Matrikon OPC Simulation Server: " + cls);
        showDetails(serverList, cls);

        final Collection<ClassDetails> detailsList = serverList.listServersWithDetails(new Category[]{Categories.OPCDAServer20}, new Category[]{});

        for (final ClassDetails details : detailsList) {
            System.out.println(String.format("Found: %s", details.getClsId()));
            System.out.println(String.format("\tProgID: %s", details.getProgId()));
            System.out.println(String.format("\tDescription: %s", details.getDescription()));
        }
    }

}

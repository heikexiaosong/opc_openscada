package com.gavel.opcclient;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.browser.BaseBrowser;
import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.opc.lib.da.browser.Leaf;
import org.openscada.opc.lib.da.browser.TreeBrowser;

/**
 * OPC server 接口类型树信息
 *
 */
public class OPCTreeBrowser {

    private static void dumpLeaf(final String prefix, final Leaf leaf) {
        //System.out.println(prefix + "Leaf: " + leaf.getName() + " [" + leaf.getItemId() + "]");
    }

    private static void dumpBranch(final String prefix, final Branch branch) {
        System.out.println(prefix + "Branch: " + branch.getName());
    }

    public static void dumpTree(final Branch branch, final int level) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }

        StringBuilder msg = new StringBuilder("################################ Branch: ").append(branch.getName());
        Branch curBranch = branch.getParent();
        while ( curBranch!=null){
            msg.append(" --> ").append(curBranch.getName());
            curBranch = curBranch.getParent();
        }
        System.out.println(msg);

        final String indent = sb.toString();


        for (final Leaf leaf : branch.getLeaves()) {
            dumpLeaf(indent, leaf);
        }
        for (final Branch subBranch : branch.getBranches()) {
            dumpBranch(indent, subBranch);
            dumpTree(subBranch, level + 1);
        }
    }

    private static void browseTree(final String prefix, final TreeBrowser treeBrowser, final Branch branch) throws IllegalArgumentException, UnknownHostException, JIException {
        treeBrowser.fillLeaves(branch);
        treeBrowser.fillBranches(branch);

        for (final Leaf leaf : branch.getLeaves()) {
            dumpLeaf("M - " + prefix + " ", leaf);
        }
        for (final Branch subBranch : branch.getBranches()) {
            dumpBranch("M - " + prefix + " ", subBranch);
            browseTree(prefix + " ", treeBrowser, subBranch);
        }
    }

    public static void main(final String[] args) throws Throwable {
        // 关闭初始化日志信息
        Logger.getLogger("org.jinterop").setLevel(Level.WARNING);

        OpcClient opcClient = OpcClient.buildOpcClient();
        try {
            opcClient.connect();

            List<String> itemids = opcClient.getItemids("Channel1.Device1.Tag*");

            for (int i = 0; i < itemids.size(); i++) {
                System.out.println("[" + (i+1) + "]item: " + itemids.get(i));
            }
//            // browse flat
//            final BaseBrowser flatBrowser = server.getFlatBrowser();
//            if (flatBrowser != null) {
//                for (final String item : server.getFlatBrowser().browse("")) {
//                    System.out.println("item: " + item);
//                }
//            }

//            // browse tree
//            final TreeBrowser treeBrowser = server.getTreeBrowser();
//            if (treeBrowser != null) {
//                dumpTree(treeBrowser.browse(), 0);
//            }

            // browse tree manually
            //browseTree("", treeBrowser, new Branch());
        } catch (final JIException e) {
            e.printStackTrace();
            System.out.println(String.format("%08X: %s", e.getErrorCode()));// server.getErrorMessage(e.getErrorCode()))
        }
    }
}

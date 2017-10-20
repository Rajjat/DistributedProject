/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Scanner;

public class ClientThread extends Thread {

    private synchronized void computeSelfIp() {
        try {
            // compute Self Ip and update list
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i.isSiteLocalAddress()) {
                        Globals.selfIpAddress = i.getHostAddress();
//                        System.out.println(i.getHostAddress());
                        if (!Globals.ipList.contains(i.getHostAddress())) {
                            Globals.ipList.add(i.getHostAddress());
                            Globals.idMap.put(i.getHostAddress(), Globals.ipToLong(i.getHostAddress()));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // TODO code application logic here
            computeSelfIp();

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("\n Enter Operation to be performed :");
            System.out.println("1. Join (Press Key 'j')");
            System.out.println("2. Sign Off (Press Key 'q')");
            System.out.println("3. Master Node Selection (Press Key 'm')");
            System.out.println("4. Distributed R/W Operation (Press Key 'd')");

            while (true) {

                switch (input.readLine().charAt(0)) {

                    case 'j':
                        joinRequest();
                        break;

                    case 'q':
                        signOff();
                        break;

                    case 'm':
                        masterNodeSel();
                        break;

                    case 'd':
                        readWrite();
                        break;

                    default:
                        System.out.println("Wrong Option Selected");
                        break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void joinRequest() {

        try {
            System.out.println("Enter the Ip Address of the node :");

            Scanner input = new Scanner(System.in);
            String ip = input.nextLine();

            RPCClient myClient = new RPCClient();
            myClient.sendJoinReq(ip);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void signOff() {
        RPCClient myClient = new RPCClient();
        if (myClient.sendSignOff() >= 0) {
//            System.out.println("Sign off Operation completed");
        } else {
            System.out.println("No host in the network");
        }
    }

    public void masterNodeSel() {

        Globals.SELF_MASTER_STATE = true;
        System.out.println("ID Mapping " + Globals.idMap);
        Globals.START_MASTER_NODE_SEL = true;

    }

    public void readWrite(){

        if(Globals.masterIpAddress == null){
            System.out.println("Master Node not selected. Run Election");
        }else{
            //send start message to all others for starting Dist read Write
            RPCClient newClient = new RPCClient();
            newClient.sendStart();
            //starting Distributed read Write Process

            LampartClockTimer tm = new LampartClockTimer();
            ReadWriteWaitTimer timerObj = new ReadWriteWaitTimer();
            Globals.START_DIST_WRITE_FLAG = true;
        }
    }
}

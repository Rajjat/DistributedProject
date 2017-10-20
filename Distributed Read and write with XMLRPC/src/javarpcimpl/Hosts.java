/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Hosts {

    public Object[] join(String host) {

        List<String> tmp = new ArrayList<>();
        if (!Globals.ipList.contains(host)) {
            return Globals.ipList.toArray();
        } else {
            return tmp.toArray();
        }
    }

    public int update(Object[] host_List) {

        String[] hostList = Arrays.copyOf(host_List, host_List.length, String[].class);
        if (hostList.length == 0) {
            return -1;
        } else {
            for (String hostList1 : hostList) {
                boolean match = false;
                for (String ipList : Globals.ipList) {
                    if (hostList1.equalsIgnoreCase(ipList)) {
                        match = true;
                    }
                }
                if (match == false) {
                    System.out.println(hostList1 + " has joined the Network..." + Globals.ipList);
                    synchronized (Globals.ipList) {
                        Globals.ipList.add(hostList1);
                        Globals.idMap.put(hostList1, Globals.ipToLong(hostList1));
                    }
                }
            }
            return 0;
        }
    }

    public int signOut(String ip) {

        if (Globals.ipList.contains(ip)) {

            if(Globals.masterIpAddress == ip){
                synchronized(Globals.masterIpAddress){
                    Globals.masterIpAddress = null;
                }
            }

            synchronized (Globals.ipList) {
                if (Globals.ipList.remove(ip)) {
                    if (Globals.counter.containsKey(ip)) {
                        synchronized (Globals.counter) {
                            Globals.counter.remove(ip);
                        }
                    }
                    System.out.println(ip + " has left the Network" + Globals.ipList);
                    return 0;
                } else {
                    System.out.println("Unable to Sign Out " + ip);
                    return -1;
                }
            }
        } else {
            return 0;
        }
    }

    public int poll(String arg) {
        return 1;
    }

    public int election(String ip) {

        Globals.SELF_MASTER_STATE = true;
        if (!Globals.START_MASTER_NODE_SEL) {
            //starting Thread for Election procedure
            Globals.START_MASTER_NODE_SEL = true;
        }

        if (Globals.ipToLong(Globals.selfIpAddress) > Globals.ipToLong(ip)) {
            return 1;
        } else {
            return 0;
        }
    }

    public int coordinator(String ip) {
        Globals.masterIpAddress = ip;
        System.out.println("Master node is " + Globals.masterIpAddress);
        System.out.println("Ip to Id Map : " + Globals.idMap);

        return 1;
    }

    public int start(String ip){
        //starting Distributed read Write Process
        LampartClockTimer tm = new LampartClockTimer();
        ReadWriteWaitTimer timerObj = new ReadWriteWaitTimer();
        Globals.START_DIST_WRITE_FLAG = true;

        return 1;
    }

    public String read(String ip){
        return Globals.masterString;
    }

    public int write(String newString){

        synchronized(Globals.masterString){
            Globals.masterString=newString;
        }
        return 1;
    }

    public int requestPermission(String ip, Integer clock){

        if(clock > Globals.lampartClock){
            synchronized(Globals.lampartClock){
                Globals.lampartClock = clock + 1;
            }
        }

        boolean allow=true;
        Globals.writeRequestMap.put(ip, clock);
        Iterator it = Globals.writeRequestMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if((Integer) pair.getValue() < clock){ // if any request has lower Time stamp
                allow = false;
            }
        }

        if(allow){
            synchronized(Globals.writeRequestMap){
                Globals.writeRequestMap.remove(ip);
            }
            return 1;
        }
        else
            return 0;
    }
}

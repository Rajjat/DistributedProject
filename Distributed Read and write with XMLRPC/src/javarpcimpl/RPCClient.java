/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class RPCClient {

    public synchronized void sendJoinReq(String ip) {

        try {
            if (!Globals.ipList.contains(ip)) { // If entered Ip is already in Nw
                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                XmlRpcClient client = new XmlRpcClient();
                config.setServerURL(new URL("http://" + ip + ":8082/"));
                config.setConnectionTimeout(Globals.TIME_OUT * 1000);
                client.setConfig(config);
                Object[] params = new Object[]{null};

                Object[] result;

                params[0] = Globals.selfIpAddress;
                System.out.println("executing join" + Globals.ipList);

                result = (Object[]) client.execute("join", params);

                if (result != null) {
                    for (Object value : result) {
                        if (!Globals.ipList.contains((String) value)) {
                            Globals.ipList.add((String) value);
                            Globals.idMap.put((String) value, Globals.ipToLong((String) value));
                        }
                    }
                    System.out.println("Join Successful: New network consists of " + Globals.ipList);
                    sendUpdate();
                } else {
                    System.out.println("Join Unsuccessful ");
                }
            } else {    //if Ip is already in the Network
                System.out.println(ip + " is already in Network");
            }
        } catch (MalformedURLException urlEx) {
            System.out.println("Wrong URL enetred");
        } catch (XmlRpcException conn) {
            System.out.println("Join Request Time out");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void sendUpdate() {

        try {
            for (String li : Globals.ipList) {
                if (!li.equals(Globals.selfIpAddress)) {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    XmlRpcClient client = new XmlRpcClient();
                    config.setServerURL(new URL("http://" + li + ":8082/"));
                    config.setConnectionTimeout(Globals.TIME_OUT * 1000);
                    client.setConfig(config);
                    Object[] params = new Object[]{null};

                    Integer result;

                    params[0] = Globals.ipList;

                    result = (int) client.execute("update", params);
                    if (result == 0) {
                        System.out.println("List updated successfully... ");
                    } else {
                        System.out.println("List not updated ------");
                    }
                }
            }
        } catch (MalformedURLException urlEx) {
            System.out.println("Wrong URL enetred");
        } catch (XmlRpcException conn) {
            System.out.println("Update operation Time out");
            conn.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int sendSignOff() {

        int rtVal = 0;

        if (Globals.ipList.size() == 1) {
            return -1;
        }

        for (Iterator<String> ite = Globals.ipList.iterator(); ite.hasNext();) {
            String localIp = ite.next();
            if (!localIp.equals(Globals.selfIpAddress)) {
                try {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    XmlRpcClient client = new XmlRpcClient();
                    config.setServerURL(new URL("http://" + localIp + ":8082/"));
                    config.setConnectionTimeout(Globals.TIME_OUT * 1000);
                    client.setConfig(config);
                    Object[] params = new Object[]{null};

                    Integer result;

                    synchronized (Globals.masterIpAddress) {
                        Globals.masterIpAddress = null;
                    }

                    params[0] = Globals.selfIpAddress;

                    result = (int) client.execute("signOut", params);

                    if (result == 0) {
                        synchronized (Globals.ipList) {
                            ite.remove();
                        }
                        System.out.println("Sign Off from " + localIp + " successful");
                    } else {
                        System.out.println("Sign Off from " + localIp + " Failed");
                    }
                } catch (MalformedURLException urlEx) {
                    System.out.println("Wrong URL enetred");
                } catch (XmlRpcException conn) {
                    System.out.println("SignOff Time out");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return rtVal;
    }

    public void sendVote() {
        System.out.println("Master Node Selection started");
        for (Iterator<String> ite = Globals.ipList.iterator(); ite.hasNext();) {
            String localIp = ite.next();
            if ((!localIp.equals(Globals.selfIpAddress))
                    && (Globals.idMap.get(localIp) > Globals.idMap.get(Globals.selfIpAddress))) {
                try {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    XmlRpcClient client = new XmlRpcClient();
                    config.setServerURL(new URL("http://" + localIp + ":8082/"));
                    config.setConnectionTimeout(Globals.VOTE_TIME_OUT * 1000);
                    config.setEnabledForExtensions(true);
                    client.setConfig(config);
                    Object[] params = new Object[]{null};

                    Integer result;

                    params[0] = Globals.selfIpAddress;

                    System.out.println("Voting send to " + localIp);

                    result = (int) client.execute("election", params);

                    if (result == 1) {
                        Globals.SELF_MASTER_STATE = false;
                        System.out.println("Voting from " + localIp + " Successful");
                    } else {
                        System.out.println("Voting from " + localIp + " Failed");
                    }
                } catch (MalformedURLException urlEx) {
                    System.out.println("Wrong URL enetred");
                } catch (XmlRpcException conn) {
                    System.out.println("Vote Time out");
                    conn.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void broadcastMaster() {

        for (Iterator<String> ite = Globals.ipList.iterator(); ite.hasNext();) {
            String localIp = ite.next();
            if (!localIp.equals(Globals.selfIpAddress)) {
                try {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    XmlRpcClient client = new XmlRpcClient();
                    config.setServerURL(new URL("http://" + localIp + ":8082/"));
                    config.setConnectionTimeout(Globals.TIME_OUT * 1000);
                    client.setConfig(config);
                    Object[] params = new Object[]{null};

                    Integer result;

                    params[0] = Globals.masterIpAddress;

                    result = (int) client.execute("coordinator", params);

                    if (result == 1) {
                        System.out.println("Master Ip Multicasted");
                    } else {
                        System.out.println("Broadcast Failed");
                    }
                } catch (MalformedURLException urlEx) {
                    System.out.println("Wrong URL enetred");
                } catch (XmlRpcException conn) {
                    System.out.println("Cordinator Request Time out");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    public boolean reqForPermission() {

        boolean allreqComplete = true;
        for (Iterator<String> ite = Globals.ipList.iterator(); ite.hasNext();) {
            String localIp = ite.next();

            if (!localIp.equals(Globals.selfIpAddress)) {
                try {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    XmlRpcClient client = new XmlRpcClient();
                    config.setServerURL(new URL("http://" + localIp + ":8082/"));
                    config.setConnectionTimeout(Globals.REQ_TIME_OUT * 1000);
                    client.setConfig(config);
                    Object[] params = new Object[]{localIp,
                        Globals.writeRequestMap.get(Globals.selfIpAddress)};

                    Integer result;

//                    params[0] = localIp;
//                    params[1] = Globals.writeRequestMap.get(Globals.selfIpAddress);

                    result = (int) client.execute("requestPermission", params);

                    if (result == 1) {
//                        System.out.println("Permission OK from " + localIp);
                    } else {
                        allreqComplete = false;
                        System.out.println("Permission Failed from " + localIp);
                    }
                } catch (MalformedURLException urlEx) {
                    System.out.println("Wrong URL enetred");
                } catch (XmlRpcException conn) {
                    allreqComplete = false;
                    System.out.println("Permission Request Time out");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return allreqComplete;
    }

    public void readFromMaster() throws MalformedURLException {

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            XmlRpcClient client = new XmlRpcClient();
            config.setServerURL(new URL("http://" + Globals.masterIpAddress + ":8082/"));
            config.setConnectionTimeout(Globals.WRITE_TIME_OUT * 1000);
            client.setConfig(config);
            Object[] params = new Object[]{null};


            params[0] = Globals.selfIpAddress;
            Globals.tmpMasterString = (String) client.execute("read", params);

//            System.out.println("Temporary Master String " + Globals.tmpMasterString);
        } catch (XmlRpcException ex) {
            System.out.println("Write  Connection Time out ");
        }
    }

    public void writetoMaster(String word) throws MalformedURLException {

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            XmlRpcClient client = new XmlRpcClient();
            config.setServerURL(new URL("http://" + Globals.masterIpAddress + ":8082/"));
            config.setConnectionTimeout(Globals.WRITE_TIME_OUT * 1000);
            client.setConfig(config);
            Object[] params = new Object[]{null};

            Integer result;
            params[0] = Globals.tmpMasterString + ":" + word;
            result = (int) client.execute("write", params);

            if (result == 1) {
                System.out.println("Written  Succesfull : " + word);
                Globals.writtenStringList.add(word);
            } else {
                System.out.println("Written  Failed :  " + word);
            }
        } catch (XmlRpcException ex) {
            System.out.println("Write  Connection Time out ");
        }
    }

      public void sendStart() {


        for (Iterator<String> ite = Globals.ipList.iterator(); ite.hasNext();) {
            String localIp = ite.next();

            if (!localIp.equals(Globals.selfIpAddress)) {
                try {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    XmlRpcClient client = new XmlRpcClient();
                    config.setServerURL(new URL("http://" + localIp + ":8082/"));
                    config.setConnectionTimeout(Globals.REQ_TIME_OUT * 1000);
                    client.setConfig(config);
                    Object[] params = new Object[]{null};

                    Integer result;

                    params[0] = localIp;

                    result = (int) client.execute("start", params);

                    if (result == 1) {
                        System.out.println("Start Success : " + localIp);
                    } else {

                        System.out.println("Start Failed : " + localIp);
                    }
                } catch (MalformedURLException urlEx) {
                    System.out.println("Wrong URL enetred");
                } catch (XmlRpcException conn) {
                    System.out.println("Start Request Time out");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    public void printMasterString() throws MalformedURLException {

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            XmlRpcClient client = new XmlRpcClient();
            config.setServerURL(new URL("http://" + Globals.masterIpAddress + ":8082/"));
            config.setConnectionTimeout(Globals.TIME_OUT * 1000);
            client.setConfig(config);
            Object[] params = new Object[]{null};

            String result;
            params[0] = Globals.selfIpAddress;
            result = (String) client.execute("read", params);

            System.out.println("Master String : " + result);

            List<String> tmp = Arrays.asList(result.split(":"));
            for (String str : Globals.writtenStringList) {
                if (tmp.contains(str)) {
                    System.out.println("Matched " + str);
                } else {
                    System.out.println("Not Matched " + str);
                }
            }

        } catch (XmlRpcException ex) {
            System.out.println("Write  Connection Time out ");
        }
    }
}

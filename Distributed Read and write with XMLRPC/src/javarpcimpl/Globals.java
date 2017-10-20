/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Globals {

    public static List<String> ipList = new ArrayList<String>();
    public static HashMap<String, Integer> counter = new HashMap<String, Integer>();
    public static HashMap<String, Long> idMap = new HashMap<String, Long>();

    public static HashMap<String, Integer> writeRequestMap = new HashMap<String, Integer>();

    public static String selfIpAddress;
    public static String masterIpAddress;

    public static String masterString = "Start";
    public static String tmpMasterString;
    public static List<String> writtenStringList = new ArrayList<String>();
    public static List<String> globalStringList = new ArrayList<String>
        (Arrays.asList("USA","Nepal","India","Pakistan",
                "Germany","Russia","France","Italy","Denmark","Switzerland",
                "Austria", "Australia", "Morocco", "Canada", "Japan", "China",
                "Malaysia", "Bhutan", "Bangladesh", "Africa", "Cameroon",
                "Argentia", "Cuba", "Iran", "Iraq", "Syria", "Maldives",
                "Thailand", "Brazil", "Mexico", "Zambia", "Kenya","Vietnam",
                "Spain", "Egypt", "Turkey", "Israel", "Tibet", "Singapore",
                "Iceland"));

    public static final int REQ_TIME_OUT = 1;
    public static final int WRITE_TIME_OUT = 1;
    public static final int TIME_OUT = 5;
    public static final int VOTE_TIME_OUT = 2;
    public static final int POLLING_TIME_OUT = 2;
    public static final int POLLING_PERIOD = 1;
    public static final int MAX_ATTEMPT = 3;
    public static final int ElECTION_TIME_OUT = 5;
    public static final int READ_WRITE_WAIT_TIMER = 20;

    public static Integer lampartClock = 0;
    public static boolean LAMPART_CLOCK_FLAG = true;//to cancel stop the counter set false

    public static boolean SELF_MASTER_STATE;
    public static boolean START_MASTER_NODE_SEL = false;

    public static boolean START_DIST_WRITE_FLAG = false;// to start distributed write Thread

    public static long ipToLong(String ipAddress) {

	String[] ipAddressInArray = ipAddress.split("\\.");

	long result = 0;
	for (int i = 0; i < ipAddressInArray.length; i++) {
		int power = 3 - i;
		int ip = Integer.parseInt(ipAddressInArray[i]);
		result += ip * Math.pow(256, power);
	}
	return result;
  }
}

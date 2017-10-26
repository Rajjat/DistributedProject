/*
 * 
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package javarpcimpl;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadWriteOperationThread extends Thread {

    @Override
    public void run() {

        while (true) {
            try {
                if (Globals.START_DIST_WRITE_FLAG) {
                    int random = (int) (Math.random() * 5 + 1);
                    if (random < (Globals.READ_WRITE_WAIT_TIMER - Globals.lampartClock)) {
                        try {
                            // Wait time is less than avail Read write
                            Thread.sleep(random * 1000);//wait for random time
                            //add own time stamp in map
                            synchronized (Globals.writeRequestMap) {
                                Globals.writeRequestMap.put(Globals.selfIpAddress, Globals.lampartClock);
                            }
                            //request for permission
                            RPCClient local = new RPCClient();
                            while (true) {
                                if (local.reqForPermission()) {
                                    int rnd = (int) (Math.random() * Globals.globalStringList.size());//for random word
                                    try {
//                                        System.out.println("Lampart Clock "+ Globals.lampartClock);
                                        local.readFromMaster();
                                        local.writetoMaster(Globals.globalStringList.get(rnd));
                                        synchronized (Globals.writeRequestMap) {
                                            Globals.writeRequestMap.remove(Globals.selfIpAddress);
                                        }
                                    } catch (MalformedURLException ex) {
                                        ex.printStackTrace();
                                    }
                                    break;
                                }
                                Thread.sleep(1 * 1000);
                            }

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                Thread.sleep(1 * 1000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

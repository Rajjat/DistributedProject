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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashu
 */
public class ReadWriteWaitTimer {
    
    Timer timer;

     ReadWriteWaitTimer() {
        timer = new Timer();  //At this line a new Thread will be created
        timer.schedule(new RemindTask(), Globals.READ_WRITE_WAIT_TIMER * 1000); //delay in milliseconds
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
         
            try {
                // To Do task after read write operation is completed
                // Stop Read Write Thread
                Globals.START_DIST_WRITE_FLAG = false;
                // read the string from master and print the String
                // check whether String written by you is there and print the check
                RPCClient local = new RPCClient();
                local.printMasterString();
                
                Globals.LAMPART_CLOCK_FLAG = false;
                //  stop the Lampart Clock, it will automatically reset it.
 
                timer.cancel();
            } catch (MalformedURLException ex) {
                Logger.getLogger(ReadWriteWaitTimer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

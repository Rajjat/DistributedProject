/*
 * Copyright (C) 2016 ashu
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

import java.util.Timer;
import java.util.TimerTask;
import static javafx.util.Duration.seconds;

/**
 *
 * @author ashu
 */
public class MasterSelWaitTimer {

    Timer timer;

     MasterSelWaitTimer(int sec) {
        timer = new Timer();  //At this line a new Thread will be created
        timer.schedule(new RemindTask(), sec * 1000); //delay in milliseconds
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
            if(Globals.SELF_MASTER_STATE){
                System.out.println("I am Master Chefu. Announcing ....");
                Globals.masterIpAddress = Globals.selfIpAddress;
                RPCClient localClient = new RPCClient();
                localClient.broadcastMaster();   
            }else{
                //do nothing
            }
            timer.cancel(); 
        }
    }

}

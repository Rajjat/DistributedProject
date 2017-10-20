package javarpcimpl;

import java.util.Timer;
import java.util.TimerTask;

public class LampartClockTimer {

    Timer timer;

    LampartClockTimer() {
        timer = new Timer();  //At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(),0, 1 * 1000); //delay in milliseconds
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            synchronized (Globals.lampartClock) {
                Globals.lampartClock++;
//                System.out.println(Globals.lampartClock);
            }

            if (!Globals.LAMPART_CLOCK_FLAG) {
                synchronized (Globals.lampartClock) {
                    Globals.lampartClock = 0;
                }
                timer.cancel();
            }
        }
    }

}

package timers;

/**
 * Timer class that executes a runnable once after n-time milliseconds
 */
public class DelayTimer extends BaseTimer {

    public DelayTimer(int time, Runnable runnable) {
        super(time, runnable);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(time);
            runnable.run();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
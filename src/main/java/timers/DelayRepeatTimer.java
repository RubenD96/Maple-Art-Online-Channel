package timers;

/**
 * Timer class that waits x-time milliseconds to execute a runnable.
 * Repeats till server shutdown.
 */
public class DelayRepeatTimer extends BaseTimer {

    public DelayRepeatTimer(int time, Runnable runnable) {
        super(time, runnable);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(time);
            runnable.run();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            run();
        }
    }
}

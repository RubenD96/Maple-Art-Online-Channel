package timers;

/**
 * Timer class that immediately executes a runnable and then waits x-time in milliseconds to repeat it.
 * Repeats till server shutdown.
 */
@Deprecated
public class RepeatDelayTimer extends BaseTimer {

    public RepeatDelayTimer(int time, Runnable runnable) {
        super(time, runnable);
    }

    @Override
    public void run() {
        try {
            runnable.run();
            Thread.sleep(time);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            run();
        }
    }
}
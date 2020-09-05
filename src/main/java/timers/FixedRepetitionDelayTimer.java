package timers;

/**
 * Immediately executes a Runnable, repeats it n amount of times with a delay of x-time milliseconds.
 */
@Deprecated
public class FixedRepetitionDelayTimer extends BaseTimer {

    private int n;

    public FixedRepetitionDelayTimer(int time, Runnable runnable, int n) {
        super(time, runnable);
        if (n < 2) {
            this.n = 0; // run() will be called, but it won't execute the Runnable
            System.err.println("[FixedRepetitionDelayTimer] N(" + n + ") should be 2 or higher, use DelayTimer instead.");
            return;
        }
        this.n = n;
    }

    @Override
    public void run() {
        try {
            if (n != 0) {
                runnable.run();
                Thread.sleep(time);
            }
            n--;
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            if (n <= 0) {
                run();
            }
        }
    }
}

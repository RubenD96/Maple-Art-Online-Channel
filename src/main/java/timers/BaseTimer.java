package timers;

@Deprecated
public abstract class BaseTimer implements Runnable {

    protected final int time;
    protected final Runnable runnable;

    public BaseTimer(int time, Runnable runnable) {
        this.time = time;
        this.runnable = runnable;
        new Thread(this).start();
    }
}

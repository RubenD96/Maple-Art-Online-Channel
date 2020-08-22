package timers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseTimer implements Runnable {

    protected final int time;
    protected final Runnable runnable;
}

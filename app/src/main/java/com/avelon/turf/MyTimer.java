package com.avelon.turf;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimer {
    private Timer timer;

    public MyTimer() {
        timer = new Timer();
    }

    public void schedule(Task task, long delay, long period) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                task.onGo();
            }
        }, delay, period);
    }

    public void fireAndForget(Task task, long delay) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.onGo();
            }
        }, delay);
    }

    public interface Task {
        void onGo();
    }
}

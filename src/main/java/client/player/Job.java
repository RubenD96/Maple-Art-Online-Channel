package client.player;

import util.packet.IntegerValue;

public enum Job implements IntegerValue {

    BEGINNER(0);

    final int jobid;

    Job(int id) {
        jobid = id;
    }

    public int getId() {
        return jobid;
    }

    public static Job getById(int id) {
        for (Job job : Job.values()) {
            if (job.getId() == id) {
                return job;
            }
        }
        return null;
    }

    @Override
    public int getValue() {
        return jobid;
    }

    @Override
    public void setValue(int value) {
        // ...
    }
}
package zad1;

import java.awt.image.BufferedImage;
import java.util.concurrent.FutureTask;

public class TasksContainer {
    private final FutureTask<BufferedImage> task;

    public TasksContainer(FutureTask<BufferedImage> task) {
        this.task = task;
    }

    public FutureTask<BufferedImage> getTask() {
        return task;
    }
    @Override
    public String toString() {
        String status;
        if (task.isCancelled()) {
            status = "Cancelled";
        } else if (task.isDone()) {
            status = "Completed";
        } else {
            status = "Running";
        }
        return String.format("[Task - %s]", status);
    }
}

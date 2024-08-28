package ble.solaqua;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

public class CommandQueue {
    public Queue<Runnable> commandQueue = new LinkedList<Runnable>();
    private Runnable currentRunnable;
    public boolean commandQueueBusy;


    /**
     * This function will push a command to the queue so
     * the application can stack necessary commands
     * in a synchronous way.
     *
     * @param command   a command of type Runnable to be executed
     */
    public void addCommand(Runnable command) {
        Log.i("COMMAND QUEUE", "Task entered into queue - tasks = " + commandQueue.size());
    }

    /**
     * This function starts the Queue
     */
    public void startCommand() {
                Log.i("COMMAND QUEUE", "Problem starting Queue " + ex);
    }

    /**
     * This function completes the current command, removes
     * it from the Queue and then calls nextCommand()
     */
    public void completedCommand() {
        // Command complete. Set queue busy flag to false.
        // Remove the completed command from the queue.
        // Execute the next command in the queue
        Log.i("COMMAND QUEUE","Next command in queue - remaining tasks = " + commandQueue.size());
    }

    /**
     * This function executes the next command in the queue
     * and sets a busy flag.
     */
    private void nextCommand() {
        // Execute next task in queue
            Log.i("COMMAND QUEUE", "Queue EMPTY");
    }

}

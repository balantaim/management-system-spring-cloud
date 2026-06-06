package com.martinatanasov.requests;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class AsyncExecutor {

    private static final int TIMEOUT_SECONDS = 30;
    private static final int SCHEDULER_TERMINATION_DELAY = 5;

    /* This utility class should not be instantiated */
    private AsyncExecutor() {
    }

    // Single shared scheduler for all timeout tasks - no leaking on each run()
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "async-executor-timeout");
        t.setDaemon(true);
        return t;
    });

    public static <T> void run(
            JComponent focusComponent,
            Runnable spinnerToggle,
            Supplier<T> backgroundTask,
            Consumer<T> onSuccess) {
        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() {
                return backgroundTask.get();
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        T result = get();
                        onSuccess.accept(result);
                    }
                } catch (InterruptedException interruptedException) {
                    // Restore interrupt flag
                    Thread.currentThread().interrupt();
                    log.warn("Async task was interrupted: {}", interruptedException.getMessage());
                } catch (Exception exception) {
                    log.error("Async executor error: {}", exception.getMessage());
                } finally {
                    SwingUtilities.invokeLater(spinnerToggle);
                }
            }
        };

        //Enable or disable load status
        spinnerToggle.run();
        worker.execute();

        ScheduledFuture<?> timeoutFuture = SCHEDULER.schedule(() -> {
            if (!worker.isDone()) {
                worker.cancel(true);
                SwingUtilities.invokeLater(() -> {
                    spinnerToggle.run();
                    JOptionPane.showMessageDialog(
                            focusComponent,
                            "Request timed out after " + TIMEOUT_SECONDS + " seconds.",
                            "Timeout",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }, TIMEOUT_SECONDS, TimeUnit.SECONDS);

        //Cancel the timeout task as soon as the worker finishes
        worker.addPropertyChangeListener(event -> {
            if ("state".equals(event.getPropertyName()) && SwingWorker.StateValue.DONE == event.getNewValue()) {
                //false = don't interrupt if already running
                timeoutFuture.cancel(false);
            }
        });
    }

    //Shut down the SCHEDULER before stop the application
    public static void shutdown() {
        SCHEDULER.shutdown();
        try {
            if (!SCHEDULER.awaitTermination(SCHEDULER_TERMINATION_DELAY, TimeUnit.SECONDS)) {
                SCHEDULER.shutdownNow();
            }
        } catch (InterruptedException e) {
            SCHEDULER.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            if (SCHEDULER.isTerminated()) {
                log.info("AsyncExecutor scheduler shut down successfully");
            } else {
                log.warn("AsyncExecutor scheduler did not terminate cleanly");
            }
        }
    }

}
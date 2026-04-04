package com.martinatanasov.requests;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncExecutor {

    private AsyncExecutor() {
        /* This utility class should not be instantiated */
    }

    private static final int TIMEOUT_SECONDS = 30;

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
                } catch (Exception ignored) {
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        spinnerToggle.run();
                    });
                }
            }
        };

        //Enable or disable load status
        spinnerToggle.run();

        worker.execute();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(() -> {
            if (!worker.isDone()) {
                worker.cancel(true);

                SwingUtilities.invokeLater(() -> {
                    spinnerToggle.run();

                    JOptionPane.showMessageDialog(focusComponent,
                            "Request timed out after 30 seconds.",
                            "Timeout",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
            scheduler.shutdown();

        }, TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

}
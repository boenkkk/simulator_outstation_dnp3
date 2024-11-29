package dev.boenkkk.simulator_outstation_dnp3.scheduler;

import dev.boenkkk.simulator_outstation_dnp3.service.DatabaseService;
import dev.boenkkk.simulator_outstation_dnp3.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class SchedulerTask {

    @Autowired
    private RandomUtil randomUtil;

    @Autowired
    private DatabaseService databaseService;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final AtomicBoolean isSchedulerMeasurementEnable = new AtomicBoolean(false);
    public synchronized void toggleSchedulerMeasurement(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerMeasurementEnable.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerMeasurementEnable.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                1,
                                randomUtil.getRandomDouble(210.0, 220.0)
                            );
                        } catch (Exception e) {
                            log.error("Error executing task: {}", e.getMessage(), e);
                        }
                    }
                }, 0, interval, TimeUnit.SECONDS);
            } else {
                log.info("Scheduler is already enabled.");
            }
        } else {
            if (isSchedulerMeasurementEnable.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerTapChangerEnable = new AtomicBoolean(false);
    public synchronized void toggleSchedulerTapChanger(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerTapChangerEnable.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerTapChangerEnable.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                0,
                                randomUtil.getRandomDouble(0.0, 32.0)
                            );
                        } catch (Exception e) {
                            log.error("Error executing task: {}", e.getMessage(), e);
                        }
                    }
                }, 0, interval, TimeUnit.SECONDS);
            } else {
                log.info("Scheduler is already enabled.");
            }
        } else {
            if (isSchedulerTapChangerEnable.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }
}

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

    private final AtomicBoolean isSchedulerEnableVR = new AtomicBoolean(false);
    public synchronized void toggleSchedulerVR(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnableVR.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnableVR.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                1,
                                randomUtil.getRandomDouble(309.0, 330.0)
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
            if (isSchedulerEnableVR.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerEnableP = new AtomicBoolean(false);
    public synchronized void toggleSchedulerP(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnableP.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnableP.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                2,
                                randomUtil.getRandomDouble(300.0, 400.0)
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
            if (isSchedulerEnableP.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerEnableQ = new AtomicBoolean(false);
    public synchronized void toggleSchedulerQ(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnableQ.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnableQ.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                3,
                                randomUtil.getRandomDouble(500.0, 600.0)
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
            if (isSchedulerEnableQ.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerEnablePF = new AtomicBoolean(false);
    public synchronized void toggleSchedulerPF(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnablePF.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnablePF.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                4,
                                randomUtil.getRandomDouble(0.0, 1.0)
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
            if (isSchedulerEnablePF.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerEnableVS = new AtomicBoolean(false);
    public synchronized void toggleSchedulerVS(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnableVS.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnableVS.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                5,
                                randomUtil.getRandomDouble(309.0, 330.0)
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
            if (isSchedulerEnableVS.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerEnableVT = new AtomicBoolean(false);
    public synchronized void toggleSchedulerVT(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnableVT.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnableVT.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                6,
                                randomUtil.getRandomDouble(309.0, 330.0)
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
            if (isSchedulerEnableVT.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerEnableF = new AtomicBoolean(false);
    public synchronized void toggleSchedulerF(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnableF.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnableF.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                7,
                                randomUtil.getRandomDouble(49.5, 51.5)
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
            if (isSchedulerEnableF.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerEnableIR = new AtomicBoolean(false);
    public synchronized void toggleSchedulerIR(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnableIR.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnableIR.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                8,
                                randomUtil.getRandomDouble(270.0, 289.0)
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
            if (isSchedulerEnableIR.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerEnableIS = new AtomicBoolean(false);
    public synchronized void toggleSchedulerIS(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnableIS.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnableIS.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                9,
                                randomUtil.getRandomDouble(270.0, 289.0)
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
            if (isSchedulerEnableIS.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }

    private final AtomicBoolean isSchedulerEnableIT = new AtomicBoolean(false);
    public synchronized void toggleSchedulerIT(boolean enable, int interval) {
        if (enable) {
            if (isSchedulerEnableIT.compareAndSet(false, true)) {
                log.info("Scheduler enabled with interval: {} seconds.", interval);
                scheduler.scheduleAtFixedRate(() -> {
                    if (isSchedulerEnableIT.get()) {
                        try {
                            // do task
                            databaseService.updateValueAnalogInput(
                                "0.0.0.0",
                                1,
                                randomUtil.getRandomDouble(270.0, 289.0)
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
            if (isSchedulerEnableIT.compareAndSet(true, false)) {
                log.info("Scheduler disabled.");
            } else {
                log.info("Scheduler is already disabled.");
            }
        }
    }
}

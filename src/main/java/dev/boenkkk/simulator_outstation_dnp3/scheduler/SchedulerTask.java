package dev.boenkkk.simulator_outstation_dnp3.scheduler;

import dev.boenkkk.simulator_outstation_dnp3.util.RandomUtil;
import dev.boenkkk.simulator_outstation_dnp3.util.TimeUtil;
import io.stepfunc.dnp3.*;
import lombok.extern.slf4j.Slf4j;
import org.joou.UShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SchedulerTask {

    @Autowired
    private RandomUtil randomUtil;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> scheduledTask; // To track the scheduled task

    private boolean isEnabled = true;  // To control whether updates run

    private void generateRandomUpdates(Outstation outstation) {
        if (!isEnabled) return;  // Skip execution if the outstation is disabled

        final Flags onlineFlags = new Flags(Flag.ONLINE);
        final UpdateOptions detectEvent = UpdateOptions.detectEvent();

        outstation.transaction(db -> {
            // measurements value
            db.updateAnalogInput(new AnalogInput(UShort.valueOf(1), randomUtil.getRandomDouble(210.0, 220.0), onlineFlags, TimeUtil.now()), detectEvent);
        });
    }

    // Start the scheduled task to generate random updates every second
    public void startScheduledTask(Outstation outstation) {
        scheduledTask = scheduler.scheduleAtFixedRate(
            () -> generateRandomUpdates(outstation), 0, 1, TimeUnit.SECONDS
        );
    }
}

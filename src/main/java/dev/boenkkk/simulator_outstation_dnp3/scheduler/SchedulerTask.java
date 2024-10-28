package dev.boenkkk.simulator_outstation_dnp3.scheduler;

import dev.boenkkk.simulator_outstation_dnp3.util.TimeUtil;
import io.stepfunc.dnp3.*;
import lombok.extern.slf4j.Slf4j;
import org.joou.UByte;
import org.joou.UShort;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.joou.Unsigned.*;

@Component
@Slf4j
public class SchedulerTask {

    private final Random random = new Random();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> scheduledTask; // To track the scheduled task

    private boolean isEnabled = true;  // To control whether updates run

    private UShort getRandomIndex() {
        return ushort(random.nextInt(3));
    }

    private boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    private double getRandomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private long getRandomLong(long min, long max) {
        return min + (long) (random.nextDouble() * (max - min));
    }

    private void generateRandomUpdates(Outstation outstation) {
        if (!isEnabled) return;  // Skip execution if the outstation is disabled

        final Flags onlineFlags = new Flags(Flag.ONLINE);
        final UpdateOptions detectEvent = UpdateOptions.detectEvent();
        UShort index = getRandomIndex();

        outstation.transaction(db -> {
            db.updateBinaryInput(new BinaryInput(index, getRandomBoolean(), onlineFlags, TimeUtil.now()), detectEvent);
            db.updateDoubleBitBinaryInput(new DoubleBitBinaryInput(
                index, getRandomBoolean() ? DoubleBit.DETERMINED_ON : DoubleBit.DETERMINED_OFF, onlineFlags, TimeUtil.now()), detectEvent);
            // db.updateBinaryOutputStatus(new BinaryOutputStatus(index, getRandomBoolean(), onlineFlags, TimeUtil.now()), detectEvent);
            db.updateCounter(new Counter(index, uint(getRandomLong(0, 1000)), onlineFlags, TimeUtil.now()), detectEvent);
            db.updateFrozenCounter(new FrozenCounter(index, uint(getRandomLong(0, 1000)), onlineFlags, TimeUtil.now()), detectEvent);
            db.updateAnalogInput(new AnalogInput(index, getRandomDouble(0.0, 100.0), onlineFlags, TimeUtil.now()), detectEvent);
            db.updateAnalogOutputStatus(new AnalogOutputStatus(index, getRandomDouble(0.0, 100.0), onlineFlags, TimeUtil.now()), detectEvent);

            List<UByte> octetString = new ArrayList<>();
            for (byte octet : "Hello".getBytes(StandardCharsets.US_ASCII)) {
                octetString.add(ubyte(octet));
            }
            db.updateOctetString(index, octetString, detectEvent);
        });
    }

    // Start the scheduled task to generate random updates every second
    public void startScheduledTask(Outstation outstation) {
        scheduledTask = scheduler.scheduleAtFixedRate(
            () -> generateRandomUpdates(outstation), 0, 1, TimeUnit.SECONDS
        );
    }
}

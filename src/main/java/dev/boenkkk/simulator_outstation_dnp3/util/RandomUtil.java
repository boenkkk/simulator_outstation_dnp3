package dev.boenkkk.simulator_outstation_dnp3.util;

import org.joou.UShort;
import org.springframework.stereotype.Component;

import java.util.Random;

import static org.joou.Unsigned.ushort;

@Component
public class RandomUtil {

    private final Random random = new Random();

    public UShort getRandomIndex() {
        return ushort(random.nextInt(3));
    }

    public boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    public long getRandomLong(long min, long max) {
        return min + (long) (random.nextDouble() * (max - min));
    }

    public double getRandomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}

package dev.boenkkk.simulator_outstation_dnp3.util;

import io.stepfunc.dnp3.Timestamp;

import static org.joou.Unsigned.ulong;

public class TimeUtil {

    public static Timestamp now() {
        return Timestamp.synchronizedTimestamp(ulong(System.currentTimeMillis()));
    }
}

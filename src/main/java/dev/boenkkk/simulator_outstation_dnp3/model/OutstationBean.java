package dev.boenkkk.simulator_outstation_dnp3.model;

import io.stepfunc.dnp3.Outstation;
import io.stepfunc.dnp3.OutstationServer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class OutstationBean {

    @Getter
    private final Map<String, OutstationData> data = new HashMap<>();

    @Getter
    @Builder
    public static class OutstationData {

        private final OutstationServer outstationServer;
        private final Outstation outstation;
    }
}

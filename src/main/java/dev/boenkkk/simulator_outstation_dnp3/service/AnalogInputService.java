package dev.boenkkk.simulator_outstation_dnp3.service;

import dev.boenkkk.simulator_outstation_dnp3.util.TimeUtil;
import io.stepfunc.dnp3.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.joou.Unsigned.*;

@Service
@Slf4j
public class AnalogInputService {

    @Autowired
    private OutstationsService outstationsService;

    public void add(String endpoint, Integer index){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    log.info("outstationData:{}", outstationData);
                    outstationData.getOutstation().transaction(db -> {
                        AnalogInputConfig analogInputConfig = new AnalogInputConfig();
                        analogInputConfig.withStaticVariation(StaticAnalogInputVariation.GROUP30_VAR4);
                        analogInputConfig.withEventVariation(EventAnalogInputVariation.GROUP32_VAR4);

                        boolean retVal = db.addAnalogInput(
                            ushort(index),
                            EventClass.CLASS1,
                            analogInputConfig
                        );
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, error:{}", endpoint, index, e.getMessage());
                }
            });
    }

    public void update(String endpoint, Integer index, Double value){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try{
                    outstationData.getOutstation().transaction(db ->
                        db.updateAnalogInput(new AnalogInput(ushort(index), value, new Flags(Flag.ONLINE), TimeUtil.now()), UpdateOptions.detectEvent())
                    );
                } catch (Exception e){
                    log.error(e.getMessage());
                }
            });
    }
}

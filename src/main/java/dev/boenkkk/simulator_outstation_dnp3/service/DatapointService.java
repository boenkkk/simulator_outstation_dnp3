package dev.boenkkk.simulator_outstation_dnp3.service;

import dev.boenkkk.simulator_outstation_dnp3.util.TimeUtil;
import io.stepfunc.dnp3.BinaryInput;
import io.stepfunc.dnp3.Flag;
import io.stepfunc.dnp3.Flags;
import io.stepfunc.dnp3.UpdateOptions;
import lombok.extern.slf4j.Slf4j;
import org.joou.UShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class DatapointService {

    @Autowired
    private OutstationsService outstationsService;

    public Boolean getBinaryInput(String endpoint, Integer index){
        AtomicReference<Boolean> returnValue = new AtomicReference<>(false);
        outstationsService.getoOutstationData(endpoint).ifPresent(
            outstationData -> {
                outstationData.getOutstation().transaction(
                db -> {
                    returnValue.set(db.getBinaryInput(UShort.valueOf(index)).value);
                }
            );
        });
        return returnValue.get();
    }

    public void updateValueBinaryInput(String endpoint, Integer index, Boolean value){
        outstationsService.getoOutstationData(endpoint).ifPresent(
            outstationData -> {
                outstationData.getOutstation().transaction(
                    db -> {
                        BinaryInput binaryInput = new BinaryInput(UShort.valueOf(index), value, new Flags(Flag.ONLINE), TimeUtil.now());
                        db.updateBinaryInput(binaryInput, UpdateOptions.detectEvent());
                    }
                );
            });
    }
}

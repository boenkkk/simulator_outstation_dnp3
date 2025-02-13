package dev.boenkkk.simulator_outstation_dnp3.service;

import dev.boenkkk.simulator_outstation_dnp3.util.TimeUtil;
import io.stepfunc.dnp3.*;
import lombok.extern.slf4j.Slf4j;
import org.joou.UShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class DatabaseService {

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
            }
        );
    }

    public void updateValueBinaryOutput(String endpoint, Integer index, Boolean value){
        outstationsService.getoOutstationData(endpoint).ifPresent(
            outstationData -> {
                outstationData.getOutstation().transaction(
                    db -> {
                        BinaryOutputStatus binaryOutputStatus = new BinaryOutputStatus(UShort.valueOf(index), value, new Flags(Flag.ONLINE), TimeUtil.now());
                        db.updateBinaryOutputStatus(binaryOutputStatus, UpdateOptions.detectEvent());
                    }
                );
            }
        );
    }

    public Double getAnalogInput(String enpoint, Integer index){
        AtomicReference<Double> returnValue = new AtomicReference<>(0.0);
        outstationsService.getoOutstationData(enpoint).ifPresent(
            outstationData -> {
                outstationData.getOutstation().transaction(
                    db -> {
                        returnValue.set(db.getAnalogInput(UShort.valueOf(index)).value);
                    }
                );
            }
        );
        return returnValue.get();
    }

    public void updateValueAnalogInput(String endpoint, Integer index, Double value){
        outstationsService.getoOutstationData(endpoint).ifPresent(
            outstationData -> {
                outstationData.getOutstation().transaction(
                    db -> {
                        AnalogInput analogInput = new AnalogInput(UShort.valueOf(index), value, new Flags(Flag.ONLINE), TimeUtil.now());
                        db.updateAnalogInput(analogInput, UpdateOptions.detectEvent());
                    }
                );
            }
        );
    }

    public Integer getDoubleBitBinaryInput(String enpoint, Integer index){
        AtomicReference<Integer> returnValue = new AtomicReference<>(0);
        outstationsService.getoOutstationData(enpoint).ifPresent(
            outstationData -> {
                outstationData.getOutstation().transaction(
                    db -> {
                        returnValue.set(db.getDoubleBitBinaryInput(UShort.valueOf(index)).value.ordinal());
                    }
                );
            }
        );
        return returnValue.get();
    }

    public void updateValueDoubleBitBinaryInput(String outstationId, Integer index, DoubleBit value) {
        outstationsService.getoOutstationData(outstationId).ifPresent(
            outstationData -> {
                outstationData.getOutstation().transaction(
                    db -> {
                        DoubleBitBinaryInput doubleBitBinaryInput = new DoubleBitBinaryInput(UShort.valueOf(index), value, new Flags(Flag.ONLINE), TimeUtil.now());
                        db.updateDoubleBitBinaryInput(doubleBitBinaryInput, UpdateOptions.detectEvent());
                    }
                );
            }
        );
    }
}

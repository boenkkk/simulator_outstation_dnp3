package dev.boenkkk.simulator_outstation_dnp3.service;

import dev.boenkkk.simulator_outstation_dnp3.model.MeasurementRequestModel;
import dev.boenkkk.simulator_outstation_dnp3.scheduler.SchedulerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DatapointService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private SchedulerTask schedulerTask;

    public Boolean getValueLocalRemote() throws Exception {
        try {
            String endpoint = "0.0.0.0";
            Integer index = 0;

            return databaseService.getBinaryInput(endpoint, index);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Boolean updateLocalRemote(Boolean value) throws Exception{
        try {
            String endpoint = "0.0.0.0";
            Integer index = 0;

            databaseService.updateValueBinaryInput(endpoint, index, value);
            return databaseService.getBinaryInput(endpoint, index);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Boolean getValueCBOpenClose() throws Exception {
        try {
            String endpoint = "0.0.0.0";
            Integer index = 1;

            return databaseService.getBinaryInput(endpoint, index);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Boolean updateCBOpenClose(Boolean value) throws Exception{
        try {
            String endpoint = "0.0.0.0";
            Integer indexBO = 0;
            databaseService.updateValueBinaryOutput(endpoint, indexBO, value);

            Integer indexBI = 1;
            databaseService.updateValueBinaryInput(endpoint, indexBI, value);
            return databaseService.getBinaryInput(endpoint, indexBI);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Map<String, Object> getDataTapChanger() throws Exception {
        try {
            String endpoint = "0.0.0.0";

            Map<String, Object> mapReturn = new HashMap<>();
            double roundValue = Math.ceil(databaseService.getAnalogInput(endpoint, 0));
            mapReturn.put("valueTapChanger", roundValue);
            mapReturn.put("valueTapChangerAutoManual", databaseService.getBinaryInput(endpoint, 2));
            mapReturn.put("valueTapChangerLocalRemote", databaseService.getBinaryInput(endpoint, 3));

            return mapReturn;
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Double updateValueTapChanger(Integer index) throws Exception {
        try {
            String endpoint = "0.0.0.0";

            databaseService.updateValueBinaryOutput(endpoint, index, true);
            Double analogInput = databaseService.getAnalogInput(endpoint, 0);
            double updateValue = 0.0;
            if (index == 1) {
                updateValue = analogInput + 1.0;
                updateValue = updateValue >= 32 ? 32 : updateValue;
                databaseService.updateValueAnalogInput(endpoint, 0, updateValue);
            } else if (index == 2) {
                updateValue = analogInput - 1.0;
                updateValue = updateValue <= 0 ? 0 : updateValue;
                databaseService.updateValueAnalogInput(endpoint, 0, updateValue);
            }

            return updateValue;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Boolean updateTapChangerAutoManual(Boolean value) throws Exception{
        try {
            // switch boolean for toggle scheduler
            boolean switchedBoolean = !value;
            log.info("boolean:{}, switchedBoolean:{}", value,switchedBoolean);
            schedulerTask.toggleScheduler("SCHEDULER_TAP_CHANGER", switchedBoolean, 1, 0, 0, 32);

            String endpoint = "0.0.0.0";
            Integer indexBO = 3;
            databaseService.updateValueBinaryOutput(endpoint, indexBO, value);

            Integer indexBI = 2;
            databaseService.updateValueBinaryInput(endpoint, indexBI, value);
            return databaseService.getBinaryInput(endpoint, indexBI);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Boolean updateTapChangerLocalRemote(Boolean value) throws Exception{
        try {
            String endpoint = "0.0.0.0";
            Integer indexBO = 4;
            databaseService.updateValueBinaryOutput(endpoint, indexBO, value);

            Integer indexBI = 3;
            databaseService.updateValueBinaryInput(endpoint, indexBI, value);
            return databaseService.getBinaryInput(endpoint, indexBI);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Map<String, Object> getValueMeasurement(String type, Integer indexValue, Integer indexAutoManual) throws Exception {
        try {
            String endpoint = "0.0.0.0";

            Map<String, Object> mapReturn = new HashMap<>();
            mapReturn.put("type", type);
            mapReturn.put("value", databaseService.getAnalogInput(endpoint, indexValue));
            mapReturn.put("autoManual", databaseService.getBinaryInput(endpoint, indexAutoManual));

            return mapReturn;
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Boolean updateMeasurementAutoManual(MeasurementRequestModel param) throws Exception{
        try {
            // switch boolean for toggle scheduler
            boolean switchedBoolean = !param.getUpdateValueAutoManual();
            log.info("boolean:{}, switchedBoolean:{}", param.getUpdateValueAutoManual(), switchedBoolean);
            schedulerTask.toggleScheduler("SCHEDULER_"+param.getType(), switchedBoolean, 1, param.getIndexValue(), param.getMinValue(), param.getMaxValue());

            String endpoint = "0.0.0.0";
            databaseService.updateValueBinaryOutput(endpoint, param.getIndexCmdAutoManual(), param.getUpdateValueAutoManual());

            databaseService.updateValueBinaryInput(endpoint, param.getIndexAutoManual(), param.getUpdateValueAutoManual());
            return databaseService.getBinaryInput(endpoint, param.getIndexAutoManual());
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Double updateValueMeasurement(MeasurementRequestModel param) throws Exception {
        log.info("param:{}", param);
        try {
            String endpoint = "0.0.0.0";

            databaseService.updateValueBinaryOutput(endpoint, param.getIndexCmdRaiseLower(), param.getIsRaiseValue());
            Double analogInputValue = databaseService.getAnalogInput(endpoint, param.getIndexValue());
            if (param.getIsRaiseValue()) {
                analogInputValue = analogInputValue + 1.0;
                databaseService.updateValueAnalogInput(endpoint, param.getIndexValue(), analogInputValue);
            } else {
                analogInputValue = analogInputValue - 1.0;
                databaseService.updateValueAnalogInput(endpoint, param.getIndexValue(), analogInputValue);
            }

            return analogInputValue;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}

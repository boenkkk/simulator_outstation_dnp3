package dev.boenkkk.simulator_outstation_dnp3.service;

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
            Integer index = 1;

            databaseService.updateValueBinaryInput(endpoint, index, value);
            return databaseService.getBinaryInput(endpoint, index);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public Map<String, Object> getDataTapChanger() throws Exception {
        try {
            String endpoint = "0.0.0.0";

            Map<String, Object> mapReturn = new HashMap<>();
            mapReturn.put("valueTapChanger", databaseService.getAnalogInput(endpoint, 0));
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
                databaseService.updateValueAnalogInput(endpoint, 0, updateValue);
            } else if (index == 2) {
                updateValue = analogInput - 1.0;
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

    public Double getValueMeasurement() throws Exception {
        try {
            String endpoint = "0.0.0.0";
            return databaseService.getAnalogInput(endpoint, 1);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}

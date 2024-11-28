package dev.boenkkk.simulator_outstation_dnp3.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

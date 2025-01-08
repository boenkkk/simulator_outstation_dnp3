package dev.boenkkk.simulator_outstation_dnp3.controller;

import dev.boenkkk.simulator_outstation_dnp3.model.MeasurementRequestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/config")
@Slf4j
public class ConfigurationController {

    @GetMapping("data-measurement")
    public ResponseEntity<List<MeasurementRequestModel>> getDataMeasurement() {
        List<MeasurementRequestModel> measurementRequestModels = MeasurementRequestModel.paramMeasurement();
        return ResponseEntity.ok(measurementRequestModels);
    }
}

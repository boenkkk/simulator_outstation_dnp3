package dev.boenkkk.simulator_outstation_dnp3.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.boenkkk.simulator_outstation_dnp3.service.AnalogInputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/analog-input")
@Slf4j
public class AnalogInputController {

    @Autowired
    private AnalogInputService analogInputService;

    @GetMapping(value = "")
    public ModelAndView index(ModelMap modelMap) {
        return new ModelAndView("app/analog-input", modelMap);
    }

    @PostMapping(value = "/add-analog-input")
    public ResponseEntity<Map<String, Object>> addAnalogInput(@RequestBody String requestBody) {
        log.info("requestBody: {}", requestBody);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            Integer dataIndex = jsonNode.get("data_index").asInt();

            analogInputService.add("0.0.0.0", dataIndex);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Success Add");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/update-analog-input")
    public ResponseEntity<Map<String, Object>> updateAnalogInput(@RequestBody String requestBody) {
        log.info("requestBody: {}", requestBody);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            Integer dataIndex = jsonNode.get("data_index").asInt();
            Double dataValue = jsonNode.get("data_value").asDouble();

            analogInputService.update("0.0.0.0", dataIndex, dataValue);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Success Update");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

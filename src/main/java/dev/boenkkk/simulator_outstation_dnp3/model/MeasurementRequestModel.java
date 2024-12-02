package dev.boenkkk.simulator_outstation_dnp3.model;

import lombok.Data;

@Data
public class MeasurementRequestModel {

    private String type;
    private Double value;
    private Double minValue;
    private Double maxValue;
    private Boolean autoManual;
    private Boolean updateValueAutoManual;
    private Integer indexValue;
    private Integer indexAutoManual;
    private Integer indexCmdAutoManual;
    private Integer indexCmdRaiseLower;
}

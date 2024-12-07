package dev.boenkkk.simulator_outstation_dnp3.model;

import lombok.Data;

import java.util.List;

@Data
public class MeasurementRequestModel {

    private String type;
    private Double value;
    private Double minValue;
    private Double maxValue;
    private Boolean autoManual;
    private Boolean updateValueAutoManual;
    private Boolean isRaiseValue;
    private Integer indexValue;
    private Integer indexAutoManual;
    private Integer indexCmdAutoManual;
    private Integer indexCmdRaiseLower;

    public static MeasurementRequestModel createParamMeasurement(String type,
                                                     int indexValue,
                                                     int indexAutoManual,
                                                     int indexCmdAutoManual,
                                                     int indexCmdRaiseLower,
                                                     double minValue,
                                                     double maxValue
    ) {
        MeasurementRequestModel model = new MeasurementRequestModel();
        model.setType(type);
        model.setIndexValue(indexValue);
        model.setIndexAutoManual(indexAutoManual);
        model.setIndexCmdAutoManual(indexCmdAutoManual);
        model.setIndexCmdRaiseLower(indexCmdRaiseLower);
        model.setMinValue(minValue);
        model.setMaxValue(maxValue);
        return model;
    }

    public static List<MeasurementRequestModel> paramMeasurement(){
        return List.of(
            createParamMeasurement("VR", 1, 4, 5, 6, 309, 330),
            createParamMeasurement("P", 2, 5, 7, 8, 300, 400),
            createParamMeasurement("Q", 3, 6, 9, 10, 500, 600),
            createParamMeasurement("PF", 4, 7, 11, 12, 0, 1),
            createParamMeasurement("VS", 5, 8, 13, 14, 309, 330),
            createParamMeasurement("VT", 6, 9, 15, 16, 309, 330),
            createParamMeasurement("F", 7, 10, 17, 18, 49.5, 51.5),
            createParamMeasurement("IR", 8, 11, 19, 20, 270, 289),
            createParamMeasurement("IS", 9, 12, 21, 22, 270, 289),
            createParamMeasurement("IT", 10, 13, 23, 24, 270, 289)
        );
    }
}

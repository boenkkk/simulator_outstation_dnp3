package dev.boenkkk.simulator_outstation_dnp3.service;

import dev.boenkkk.simulator_outstation_dnp3.util.ConvertUtil;
import dev.boenkkk.simulator_outstation_dnp3.util.TimeUtil;
import io.stepfunc.dnp3.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.joou.Unsigned.ushort;

@Service
@Slf4j
public class DatabaseService {

    @Autowired
    private OutstationsService outstationsService;

    // Setup initial points
    final Flags onlineFlags = new Flags(Flag.ONLINE);

    public void parseIoa(String operation, int ioa, String endpoint, String eventClassName, String staticVariationName, String eventVariationName, Object value){
        try {
            int type = Integer.parseInt(String.valueOf(ioa).substring(0, 1));
            int index = Integer.parseInt(String.valueOf(ioa).substring(1));
            switch (operation) {
                case "ADD" -> {
                    switch (type) {
                        case 1 -> addBinaryInput(endpoint, index, eventClassName, staticVariationName, eventVariationName);
                        case 2 -> addBinaryOutput(endpoint, index, eventClassName, staticVariationName, eventVariationName);
                        case 3 -> addAnalogInput(endpoint, index, eventClassName, staticVariationName, eventVariationName);
                        case 4 -> addAnalogOutput(endpoint, index, eventClassName, staticVariationName, eventVariationName);
                        default -> log.info("Unknown type:{}, ioa:{}", operation, ioa);
                    }
                }
                case "REMOVE" -> {
                    switch (type) {
                        case 1 -> removeBinaryInput(endpoint, index);
                        case 2 -> removeBinaryOutput(endpoint, index);
                        case 3 -> removeAnalogInput(endpoint, index);
                        case 4 -> removeAnalogOutput(endpoint, index);
                        default -> log.info("Unknown type:{}, ioa:{}", operation, ioa);
                    }
                }
                case "UPDATE" -> {
                    switch (type) {
                        case 1 -> {
                            removeBinaryInput(endpoint, index);
                            addBinaryInput(endpoint, index, eventClassName, staticVariationName, eventVariationName);
                        }
                        case 2 -> {
                            removeBinaryOutput(endpoint, index);
                            addBinaryOutput(endpoint, index, eventClassName, staticVariationName, eventVariationName);
                        }
                        case 3 -> {
                            removeAnalogInput(endpoint, index);
                            addAnalogInput(endpoint, index, eventClassName, staticVariationName, eventVariationName);
                        }
                        case 4 -> {
                            removeAnalogOutput(endpoint, index);
                            addAnalogOutput(endpoint, index, eventClassName, staticVariationName, eventVariationName);
                        }
                        default -> log.info("Unknown type:{}, ioa:{}", operation, ioa);
                    }
                }
                case "UPDATE_VALUE" -> {
                    switch (type) {
                        case 1 -> updateBinaryInput(endpoint, index, value);
                        case 2 -> updateBinaryOutput(endpoint, index, value);
                        case 3 -> updateAnalogInput(endpoint, index, value);
                        case 4 -> updateAnalogOutput(endpoint, index, value);
                        default -> log.info("Unknown type:{}, ioa:{}", operation, ioa);
                    }
                }
                default -> log.info("Unknown operation:{}", operation);
            }
        } catch (Exception e) {
            log.info("error:{}, operation:{}, ioa:{}, endpoint:{}, eventClassName:{}, staticVariationName:{}, eventVariationName:{}, value:{}", e.getMessage(), operation, ioa, endpoint, eventClassName, staticVariationName, eventVariationName, value);
        }
    }

    private void addAnalogInput(String endpoint, int index, String eventClassName, String staticAnalogInputVariationName, String eventAnalogInputVariationName){
        log.info("param endpoint:{}, index:{}, eventClassName:{}, staticAnalogInputVariationName:{}, eventAnalogInputVariationName:{}", endpoint, index, eventClassName, staticAnalogInputVariationName, eventAnalogInputVariationName);
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    log.info("outstationData:{}", outstationData);
                    outstationData.getOutstation().transaction(db -> {
                        AnalogInputConfig analogInputConfig = new AnalogInputConfig();
                        analogInputConfig.withStaticVariation(ConvertUtil.getStaticAnalogInputVariationByName(staticAnalogInputVariationName));
                        analogInputConfig.withEventVariation(ConvertUtil.getEventAnalogInputVariationByName(eventAnalogInputVariationName));

                        boolean retVal = db.addAnalogInput(
                            ushort(index),
                            ConvertUtil.getEventClassByName(eventClassName),
                            analogInputConfig
                        );
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, error:{}", endpoint, index, e.getMessage());
                }
            });
    }

    private void removeAnalogInput(String endpoint, int index){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        AnalogInput analogInput = db.getAnalogInput(ushort(index));
                        analogInput.withFlags(new Flags(Flag.DISCONTINUITY));
                        db.updateAnalogInput(analogInput, UpdateOptions.detectEvent());

                        boolean retVal = db.removeAnalogInput(ushort(index));
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, error:{}", endpoint, index, e.getMessage());
                }
            });
    }

    private void updateAnalogInput(String endpoint, int index, Object value){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        AnalogInput analogInput = new AnalogInput(
                            ushort(index),
                            Double.parseDouble(value.toString()),
                            onlineFlags,
                            TimeUtil.now());

                        boolean retVal = db.updateAnalogInput(analogInput, UpdateOptions.detectEvent());
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, value:{}, error:{}", endpoint, index, value, e.getMessage());
                }
            });
    }

    private void addAnalogOutput(String endpoint, int index, String eventClassName, String staticAnalogOutputStatusVariationName, String eventAnalogOutputStatusVariationName){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        AnalogOutputStatusConfig analogOutputStatusConfig = new AnalogOutputStatusConfig();
                        analogOutputStatusConfig.withStaticVariation(ConvertUtil.getStaticAnalogOutputStatusVariationByName(staticAnalogOutputStatusVariationName));
                        analogOutputStatusConfig.withEventVariation(ConvertUtil.getEventAnalogOutputStatusVariationByName(eventAnalogOutputStatusVariationName));

                        boolean retVal = db.addAnalogOutputStatus(
                            ushort(index),
                            ConvertUtil.getEventClassByName(eventClassName),
                            analogOutputStatusConfig
                        );
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e){
                    log.error("endpoint:{}, index:{}, error:{}", endpoint, index, e.getMessage());
                }
            });
    }

    private void removeAnalogOutput(String endpoint, int index){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        AnalogOutputStatus analogOutputStatus = db.getAnalogOutputStatus(ushort(index));
                        analogOutputStatus.withFlags(new Flags(Flag.DISCONTINUITY));
                        db.updateAnalogOutputStatus(analogOutputStatus, UpdateOptions.detectEvent());

                        boolean retVal = db.removeAnalogInput(ushort(index));
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e){
                    log.error("endpoint:{}, index:{}, error:{}", endpoint, index, e.getMessage());
                }
            });
    }

    private void updateAnalogOutput(String endpoint, int index, Object value){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        AnalogOutputStatus analogOutputStatus = new AnalogOutputStatus(
                            ushort(index),
                            Double.parseDouble(value.toString()),
                            onlineFlags,
                            TimeUtil.now()
                        );

                        boolean retVal = db.updateAnalogOutputStatus(analogOutputStatus, UpdateOptions.detectEvent());
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e){
                    log.error("endpoint:{}, index:{}, value:{}, error:{}", endpoint, index, value, e.getMessage());
                }
            });
    }

    private void addBinaryInput(String endpoint, int index, String eventClassName, String staticBinaryInputVariationName, String eventBinaryInputVariationName){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        BinaryInputConfig binaryInputConfig = new BinaryInputConfig();
                        binaryInputConfig.withStaticVariation(ConvertUtil.getStaticBinaryInputVariationByName(staticBinaryInputVariationName));
                        binaryInputConfig.withEventVariation(ConvertUtil.getEventBinaryInputVariationByName(eventBinaryInputVariationName));

                        boolean retVal = db.addBinaryInput(
                            ushort(index),
                            ConvertUtil.getEventClassByName(eventClassName),
                            binaryInputConfig
                        );
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, error:{}", endpoint, index, e.getMessage());
                }
            });
    }

    private void removeBinaryInput(String endpoint, int index){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        BinaryInput binaryInput = db.getBinaryInput(ushort(index));
                        binaryInput.withFlags(new Flags(Flag.DISCONTINUITY));
                        db.updateBinaryInput(binaryInput, UpdateOptions.detectEvent());

                        boolean retVal = db.removeBinaryInput(ushort(index));
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, error:{}", endpoint, index, e.getMessage());
                }
            });
    }

    private void updateBinaryInput(String endpoint, int index, Object value){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        BinaryInput binaryInput = new BinaryInput(
                            ushort(index),
                            (Boolean) value,
                            onlineFlags,
                            TimeUtil.now());

                        boolean retVal = db.updateBinaryInput(binaryInput, UpdateOptions.detectEvent());
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, value:{}, error:{}", endpoint, index, value, e.getMessage());
                }
            }
        );
    }

    private void addBinaryOutput(String endpoint, int index, String eventClassName, String staticBinaryOutputStatusVariationName, String eventBinaryOutputStatusVariationName){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        BinaryOutputStatusConfig binaryOutputStatusConfig = new BinaryOutputStatusConfig();
                        binaryOutputStatusConfig.withStaticVariation(ConvertUtil.getStaticBinaryOutputStatusVariationByName(staticBinaryOutputStatusVariationName));
                        binaryOutputStatusConfig.withEventVariation(ConvertUtil.getEventBinaryOutputStatusVariationByName(eventBinaryOutputStatusVariationName));

                        boolean retVal = db.addBinaryOutputStatus(
                            ushort(index),
                            ConvertUtil.getEventClassByName(eventClassName),
                            binaryOutputStatusConfig
                        );
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, error:{}", endpoint, index, e.getMessage());
                }
            });
    }

    private void removeBinaryOutput(String endpoint, int index){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        BinaryOutputStatus binaryOutputStatus = db.getBinaryOutputStatus(ushort(index));
                        binaryOutputStatus.withFlags(new Flags(Flag.DISCONTINUITY));
                        db.updateBinaryOutputStatus(binaryOutputStatus, UpdateOptions.detectEvent());

                        boolean retVal = db.removeBinaryOutputStatus(ushort(index));
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, error:{}", endpoint, index, e.getMessage());
                }
            });
    }

    private void updateBinaryOutput(String endpoint, int index, Object value){
        outstationsService.getoOutstationData(endpoint)
            .ifPresent(outstationData -> {
                try {
                    outstationData.getOutstation().transaction(db -> {
                        BinaryOutputStatus binaryOutputStatus = new BinaryOutputStatus(
                            ushort(index),
                            (Boolean) value,
                            onlineFlags,
                            TimeUtil.now());

                        boolean retVal = db.updateBinaryOutputStatus(binaryOutputStatus, UpdateOptions.detectEvent());
                        log.info("return:{}", retVal);
                    });
                } catch (Exception e) {
                    log.error("endpoint:{}, index:{}, value:{}, error:{}", endpoint, index, value, e.getMessage());
                }
            }
        );
    }
}

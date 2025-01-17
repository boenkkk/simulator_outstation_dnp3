package dev.boenkkk.simulator_outstation_dnp3.model.callback;

import dev.boenkkk.simulator_outstation_dnp3.model.MeasurementRequestModel;
import dev.boenkkk.simulator_outstation_dnp3.service.DatapointService;
import dev.boenkkk.simulator_outstation_dnp3.service.SocketIOService;
import dev.boenkkk.simulator_outstation_dnp3.util.TimeUtil;
import io.stepfunc.dnp3.*;
import lombok.extern.slf4j.Slf4j;
import org.joou.UShort;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.joou.Unsigned.ushort;

// ANCHOR: control_handler
@Slf4j
public class ControlHandlerImpl implements ControlHandler {

    // TODO:
    // NOTE: it use constructor injection coz cant be using DI
    private final DatapointService datapointService;
    private final SocketIOService socketIOService;

    public ControlHandlerImpl(DatapointService datapointService, SocketIOService socketIOService) {
        this.datapointService = datapointService;
        this.socketIOService = socketIOService;
    }

    @Override
    public void beginFragment() {
        String message = "beginFragment";
        log.info(message);
    }

    @Override
    public void endFragment(DatabaseHandle databaseHandle) {
        String message = "endFragment databaseHandle:" + databaseHandle;
        log.info(message);
    }

    @Override
    public CommandStatus selectG12v1(Group12Var1 group12Var1, UShort index, DatabaseHandle databaseHandle) {
        CommandStatus commandStatus;
        if (index.compareTo(ushort(10)) < 0 && (group12Var1.code.opType == OpType.LATCH_ON || group12Var1.code.opType == OpType.LATCH_OFF)) {
            commandStatus = CommandStatus.SUCCESS;
        } else {
            commandStatus = CommandStatus.NOT_SUPPORTED;
        }

        String message = "selectG12v1 group12Var1:" + group12Var1 + ", index:" + index + ", databaseHandle:" + databaseHandle + ", commandStatus:" + commandStatus;
        log.info(message);

        return commandStatus;
    }

    @Override
    public CommandStatus operateG12v1(Group12Var1 group12Var1, UShort index, OperateType opType, DatabaseHandle databaseHandle) {
        AtomicReference<CommandStatus> commandStatus = new AtomicReference<>();
        try {
            if (group12Var1.code.opType == OpType.LATCH_ON || group12Var1.code.opType == OpType.LATCH_OFF) {
                boolean value = group12Var1.code.opType == OpType.LATCH_ON;
                switch (index.intValue()) {
                    case 0 -> {
                        Boolean valueLocalRemote = datapointService.getValueLocalRemote();
                        if (valueLocalRemote) {
                            Boolean valueCBOpenClose = datapointService.updateCBOpenClose(value);
                            socketIOService.broadcastToDefaultRoom("/cb-open-close", "listen", valueCBOpenClose);
                            commandStatus.set(CommandStatus.SUCCESS);
                        } else {
                            commandStatus.set(CommandStatus.LOCAL);
                        }
                    }
                    case 1 -> {
                        if (value) {
                            datapointService.updateValueTapChanger(index.intValue());
                            Map<String, Object> dataTapChanger = datapointService.getDataTapChanger();
                            socketIOService.broadcastToDefaultRoom("/tap-changer", "listen", dataTapChanger);
                            commandStatus.set(CommandStatus.SUCCESS);
                        } else {
                            commandStatus.set(CommandStatus.NOT_SUPPORTED);
                        }
                    }
                    case 2 -> {
                        log.info("2");
                        if (value) {
                            datapointService.updateValueTapChanger(index.intValue());
                            Map<String, Object> dataTapChanger = datapointService.getDataTapChanger();
                            socketIOService.broadcastToDefaultRoom("/tap-changer", "listen", dataTapChanger);
                            commandStatus.set(CommandStatus.SUCCESS);
                        } else {
                            commandStatus.set(CommandStatus.NOT_SUPPORTED);
                        }
                    }
                    case 3 -> {
                        datapointService.updateTapChangerAutoManual(value);
                        Map<String, Object> dataTapChangerUpdated = datapointService.getDataTapChanger();
                        socketIOService.broadcastToDefaultRoom("/tap-changer", "listen", dataTapChangerUpdated);
                        commandStatus.set(CommandStatus.SUCCESS);
                    }
                    case 4 -> {
                        datapointService.updateTapChangerLocalRemote(value);
                        Map<String, Object> dataTapChangerUpdated = datapointService.getDataTapChanger();
                        socketIOService.broadcastToDefaultRoom("/tap-changer", "listen", dataTapChangerUpdated);
                        commandStatus.set(CommandStatus.SUCCESS);
                    }
                    // measurement auto / manual
                    case 5, 7, 9, 11, 13, 15, 17, 19, 21, 23 -> {
                        List<MeasurementRequestModel> measurementRequestModels = MeasurementRequestModel.paramMeasurement();
                        measurementRequestModels.stream()
                            .filter(data -> index.intValue() == data.getIndexCmdAutoManual())
                            .forEach(data -> {
                                    try {
                                        data.setUpdateValueAutoManual(value);
                                        datapointService.updateMeasurementAutoManual(data);

                                        Map<String, Object> dataMeasurement = datapointService.getValueMeasurement(data.getType(), data.getIndexValue(), data.getIndexAutoManual());
                                        socketIOService.broadcastToDefaultRoom("/measurement", "listen", dataMeasurement);
                                        commandStatus.set(CommandStatus.SUCCESS);
                                    } catch (Exception e) {
                                        log.error(e.getMessage());
                                        commandStatus.set(CommandStatus.UNKNOWN);
                                        throw new RuntimeException(e.getMessage());
                                    }
                                }
                            );
                    }
                    // measurement raise / lower
                    case 6, 8, 10, 12, 14, 16, 18, 20, 22, 24 -> {
                        List<MeasurementRequestModel> measurementRequestModels = MeasurementRequestModel.paramMeasurement();
                        measurementRequestModels.stream()
                            .filter(data -> index.intValue() == data.getIndexCmdRaiseLower())
                            .forEach(data -> {
                                    try {
                                        data.setIsRaiseValue(value);
                                        datapointService.updateValueMeasurement(data);

                                        Map<String, Object> dataMeasurement = datapointService.getValueMeasurement(data.getType(), data.getIndexValue(), data.getIndexAutoManual());
                                        socketIOService.broadcastToDefaultRoom("/measurement", "listen", dataMeasurement);
                                        commandStatus.set(CommandStatus.SUCCESS);
                                    } catch (Exception e) {
                                        log.error(e.getMessage());
                                        commandStatus.set(CommandStatus.UNKNOWN);
                                        throw new RuntimeException(e.getMessage());
                                    }
                                }
                            );
                    }
                    default -> commandStatus.set(CommandStatus.OUT_OF_RANGE);
                }
            } else {
                commandStatus.set(CommandStatus.NOT_SUPPORTED);
            }

            return commandStatus.get();
        } catch (Exception e) {
            log.error(e.getMessage());
            commandStatus.set(CommandStatus.HARDWARE_ERROR);
            throw new RuntimeException(e.getMessage());
        } finally {
            String message = "operateG12v1 group12Var1:" + group12Var1 + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + databaseHandle + ", commandStatus:" + commandStatus;
            log.info(message);
        }
    }

    @Override
    public CommandStatus selectG41v1(int value, UShort index, DatabaseHandle databaseHandle) {
        String message = "selectG41v1 value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v1(int value, UShort index, OperateType opType, DatabaseHandle databaseHandle) {
        String message = "operateG41v1 value:" + value + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return operateAnalogOutput(value, index, databaseHandle);
    }

    @Override
    public CommandStatus selectG41v2(short value, UShort index, DatabaseHandle databaseHandle) {
        String message = "selectG41v2 value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v2(short value, UShort index, OperateType opType, DatabaseHandle databaseHandle) {
        String message = "operateG41v2 value:" + value + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return operateAnalogOutput(value, index, databaseHandle);
    }

    @Override
    public CommandStatus selectG41v3(float value, UShort index, DatabaseHandle databaseHandle) {
        String message = "selectG41v3 value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v3(float value, UShort index, OperateType opType, DatabaseHandle databaseHandle) {
        String message = "operateG41v3 value:" + value + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return operateAnalogOutput(value, index, databaseHandle);
    }

    @Override
    public CommandStatus selectG41v4(double value, UShort index, DatabaseHandle databaseHandle) {
        String message = "selectG41v4 value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v4(double value, UShort index, OperateType opType, DatabaseHandle databaseHandle) {
        String message = "operateG41v4 value:" + value + ", index:" + index + ", opType:" + opType + ", databaseHandle:" + databaseHandle;
        log.info(message);

        return operateAnalogOutput(value, index, databaseHandle);
    }

    private CommandStatus selectAnalogOutput(UShort index) {
        CommandStatus commandStatus = index.compareTo(ushort(10)) < 0 ? CommandStatus.SUCCESS : CommandStatus.NOT_SUPPORTED;

        String message = "selectAnalogOutput index:" + index + ", commandStatus:" + commandStatus;
        log.info(message);

        return commandStatus;
    }

    private CommandStatus operateAnalogOutput(double value, UShort index, DatabaseHandle databaseHandle) {
        CommandStatus commandStatus;
        if (index.compareTo(ushort(10)) < 0) {
            databaseHandle.transaction(
                db -> db.updateAnalogOutputStatus(
                    new AnalogOutputStatus(index, value, new Flags(Flag.ONLINE),
                        TimeUtil.now()), UpdateOptions.detectEvent()
                )
            );
            commandStatus = CommandStatus.SUCCESS;
        } else {
            commandStatus = CommandStatus.NOT_SUPPORTED;
        }

        String message = "operateAnalogOutput value:" + value + ", index:" + index + ", databaseHandle:" + databaseHandle + ", commandStatus:" + commandStatus;
        log.info(message);

        return commandStatus;
    }
}
// ANCHOR_END: control_handler

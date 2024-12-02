package dev.boenkkk.simulator_outstation_dnp3.socket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import dev.boenkkk.simulator_outstation_dnp3.model.MeasurementRequestModel;
import dev.boenkkk.simulator_outstation_dnp3.service.DatapointService;
import dev.boenkkk.simulator_outstation_dnp3.service.SocketIOService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class MeasurementSocket {

    @Autowired
    private SocketIONamespace measurementNamespace;

    @Autowired
    private SocketIOService socketIOService;

    @Autowired
    private DatapointService datapointService;

    @PostConstruct
    public void init() {
        measurementNamespace.addConnectListener(onConnected());
        measurementNamespace.addDisconnectListener(onDisconnected());
        measurementNamespace.addEventListener("get-data", MeasurementRequestModel.class, getData());
        measurementNamespace.addEventListener("update-auto-manual", MeasurementRequestModel.class, updateAutoManual());
    }

    @OnConnect
    private ConnectListener onConnected() {
        return client -> {
            try {
                client.joinRoom("DEFAULT");

                String message = "Connected to socket";
                log.info("message:{}, client:{}", message, client.getSessionId().toString());
            } catch (Exception e) {
                log.error("error:{}", e.getMessage());
            }
        };
    }

    @OnDisconnect
    private DisconnectListener onDisconnected() {
        return client -> {
            try {
                String message = "Disconnected to socket";
                log.info("message:{}, client:{}", message, client.getSessionId().toString());
            } catch (Exception e) {
                log.error("error:{}", e.getMessage());
            }
        };
    }

    private DataListener<MeasurementRequestModel> getData(){
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();
                log.info("namespace: {}, data: {}", nameSpace, data);

                Map<String, Object> dataMeasurement = datapointService.getValueMeasurement(data.getType(), data.getIndexValue(), data.getIndexAutoManual());
                log.info("dataMeasurement: {}", dataMeasurement);

                socketIOService.sendMessageSelf(client, "listen", dataMeasurement);

                log.info("namespace:{}, dataMeasurement:{}", nameSpace, dataMeasurement);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }

    private DataListener<MeasurementRequestModel> updateAutoManual(){
        return (client, data, ackSender) -> {
            try {
                log.info("data: {}", data);
                String nameSpace = client.getNamespace().getName();

                datapointService.updateMeasurementAutoManual(data);
                Map<String, Object> dataTapChanger = datapointService.getDataTapChanger();
                socketIOService.sendMessageSelf(client, "listen", dataTapChanger);

                log.info("namepace:{}, data:{}, valueTapChanger:{}", nameSpace, data, dataTapChanger);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }
}

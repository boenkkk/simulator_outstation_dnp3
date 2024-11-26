package dev.boenkkk.simulator_outstation_dnp3.socket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import dev.boenkkk.simulator_outstation_dnp3.service.DatapointService;
import dev.boenkkk.simulator_outstation_dnp3.service.SocketIOService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LocalRemoteSocket {

    @Autowired
    private SocketIONamespace localRemoteNamespace;

    @Autowired
    private SocketIOService socketIOService;

    @Autowired
    private DatapointService datapointService;

    @PostConstruct
    public void init() {
        localRemoteNamespace.addConnectListener(onConnected());
        localRemoteNamespace.addDisconnectListener(onDisconnected());
        localRemoteNamespace.addEventListener("get-data", Void.class, getData());
        localRemoteNamespace.addEventListener("update-data", Boolean.class, updateData());

    }

    @OnConnect
    private ConnectListener onConnected() {
        return client -> {
            try {
                client.joinRoom("DEFAULT");

                String message = "Connected to socket";
                log.info("{}|{}", message, client.getSessionId().toString());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        };
    }

    @OnDisconnect
    private DisconnectListener onDisconnected() {
        return client -> {
            try {
                String message = "Disconnected to socket";
                log.info("{}|{}", message, client.getSessionId().toString());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        };
    }

    private DataListener<Void> getData(){
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();
                String endpoint = "0.0.0.0";
                Integer index = 0;

                Boolean binaryInput = datapointService.getBinaryInput(endpoint, index);
                socketIOService.sendMessageSelf(client, "listen", binaryInput);

                log.info("Message: {}", nameSpace);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error(e.getMessage());
            }
        };
    }

    private DataListener<Boolean> updateData(){
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();
                String endpoint = "0.0.0.0";
                Integer index = 0;

                datapointService.updateValueBinaryInput(endpoint, index, data);
                Boolean binaryInput = datapointService.getBinaryInput(endpoint, index);
                socketIOService.sendMessageSelf(client, "listen", binaryInput);

                log.info("Message: {}", nameSpace);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error(e.getMessage());
            }
        };
    }
}

package dev.boenkkk.simulator_outstation_dnp3.socket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import dev.boenkkk.simulator_outstation_dnp3.service.DatabaseService;
import dev.boenkkk.simulator_outstation_dnp3.service.SocketIOService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CBOpenCloseSocket {

    @Autowired
    private SocketIONamespace cbOpenCloseNamespace;

    @Autowired
    private SocketIOService socketIOService;

    @Autowired
    private DatabaseService databaseService;

    @PostConstruct
    public void init() {
        cbOpenCloseNamespace.addConnectListener(onConnected());
        cbOpenCloseNamespace.addDisconnectListener(onDisconnected());
        cbOpenCloseNamespace.addEventListener("get-data", Void.class, getData());
        cbOpenCloseNamespace.addEventListener("update-data", Boolean.class, updateData());

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

    private DataListener<Void> getData(){
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();
                String endpoint = "0.0.0.0";
                Integer index = 1;

                Boolean binaryInput = databaseService.getBinaryInput(endpoint, index);
                socketIOService.sendMessageSelf(client, "listen", binaryInput);

                log.info("namespace:{}, index:{}, binaryInput:{}", nameSpace, index, binaryInput);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }

    private DataListener<Boolean> updateData(){
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();
                String endpoint = "0.0.0.0";
                Integer index = 1;

                databaseService.updateValueBinaryInput(endpoint, index, data);
                Boolean binaryInput = databaseService.getBinaryInput(endpoint, index);
                socketIOService.sendMessageSelf(client, "listen", binaryInput);

                log.info("namepace:{}, index:{}, data:{}, binaryInput:{}", nameSpace, index, data, binaryInput);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }
}

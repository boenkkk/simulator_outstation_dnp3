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
public class CBOpenCloseSocket {

    @Autowired
    private SocketIONamespace cbOpenCloseNamespace;

    @Autowired
    private SocketIOService socketIOService;

    @Autowired
    private DatapointService datapointService;

    @PostConstruct
    public void init() {
        cbOpenCloseNamespace.addConnectListener(onConnected());
        cbOpenCloseNamespace.addDisconnectListener(onDisconnected());
        cbOpenCloseNamespace.addEventListener("get-data", Void.class, getData());
        cbOpenCloseNamespace.addEventListener("update-data", Boolean.class, updateData());
        cbOpenCloseNamespace.addEventListener("set-cb-invalid", Integer.class, setCBInvalid());
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

                Boolean valueCBOpenClose = datapointService.getValueCBOpenClose();
                socketIOService.sendMessageSelf(client, "listen", valueCBOpenClose);

                log.info("namespace:{}, valueCBOpenClose:{}", nameSpace, valueCBOpenClose);
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

                Boolean valueCBOpenClose = datapointService.updateCBOpenClose(data);
                socketIOService.sendMessageSelf(client, "listen", valueCBOpenClose);

                log.info("namepace:{}, index:{}, valueCBOpenClose:{}", nameSpace, data, valueCBOpenClose);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }

    private DataListener<Integer> setCBInvalid(){
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();

                Integer valueCBInvalid = datapointService.updateCBInvalid(data);
                socketIOService.sendMessageSelf(client, "listen", valueCBInvalid);

                log.info("namepace:{}, index:{}, valueCBInvalid:{}", nameSpace, data, valueCBInvalid);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }
}

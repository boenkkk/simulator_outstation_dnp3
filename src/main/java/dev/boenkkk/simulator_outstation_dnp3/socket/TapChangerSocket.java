package dev.boenkkk.simulator_outstation_dnp3.socket;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import dev.boenkkk.simulator_outstation_dnp3.service.DatabaseService;
import dev.boenkkk.simulator_outstation_dnp3.service.DatapointService;
import dev.boenkkk.simulator_outstation_dnp3.service.SocketIOService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TapChangerSocket {

    @Autowired
    private SocketIONamespace tapChangerNamespace;

    @Autowired
    private SocketIOService socketIOService;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private DatapointService datapointService;

    @PostConstruct
    public void init() {
        tapChangerNamespace.addConnectListener(onConnected());
        tapChangerNamespace.addDisconnectListener(onDisconnected());
        tapChangerNamespace.addEventListener("get-data", Void.class, getData());
        tapChangerNamespace.addEventListener("update-data", Integer.class, updateData());

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

                Double valueTapChanger = datapointService.getValueTapChanger();
                socketIOService.sendMessageSelf(client, "listen", valueTapChanger);

                log.info("namespace:{}, valueTapChanger:{}", nameSpace, valueTapChanger);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }

    private DataListener<Integer> updateData(){
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();

                Double valueTapChanger = datapointService.updateValueTapChanger(data);
                socketIOService.sendMessageSelf(client, "listen", valueTapChanger);

                log.info("namepace:{}, data:{}, valueTapChanger:{}", nameSpace, data, valueTapChanger);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }
}

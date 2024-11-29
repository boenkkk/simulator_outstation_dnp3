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

import java.util.Map;

@Component
@Slf4j
public class TapChangerSocket {

    @Autowired
    private SocketIONamespace tapChangerNamespace;

    @Autowired
    private SocketIOService socketIOService;

    @Autowired
    private DatapointService datapointService;

    @PostConstruct
    public void init() {
        tapChangerNamespace.addConnectListener(onConnected());
        tapChangerNamespace.addDisconnectListener(onDisconnected());
        tapChangerNamespace.addEventListener("get-data", Void.class, getData());
        tapChangerNamespace.addEventListener("update-data", Integer.class, updateData());
        tapChangerNamespace.addEventListener("update-auto-manual", Boolean.class, updateAutoManual());
        tapChangerNamespace.addEventListener("update-local-remote", Boolean.class, updateLocalRemote());
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

                Map<String, Object> dataTapChanger = datapointService.getDataTapChanger();
                socketIOService.sendMessageSelf(client, "listen", dataTapChanger);

                log.info("namespace:{}, dataTapChanger:{}", nameSpace, dataTapChanger);
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

                datapointService.updateValueTapChanger(data);
                Map<String, Object> dataTapChanger = datapointService.getDataTapChanger();
                socketIOService.sendMessageSelf(client, "listen", dataTapChanger);

                log.info("namepace:{}, data:{}, dataTapChanger:{}", nameSpace, data, dataTapChanger);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }

    private DataListener<Boolean> updateAutoManual(){
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();

                datapointService.updateTapChangerAutoManual(data);
                Map<String, Object> dataTapChanger = datapointService.getDataTapChanger();
                socketIOService.sendMessageSelf(client, "listen", dataTapChanger);

                log.info("namepace:{}, data:{}, valueTapChanger:{}", nameSpace, data, dataTapChanger);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }

    private DataListener<Boolean> updateLocalRemote(){
        return (client, data, ackSender) -> {
            try {
                String nameSpace = client.getNamespace().getName();

                datapointService.updateTapChangerLocalRemote(data);
                Map<String, Object> dataTapChanger = datapointService.getDataTapChanger();
                socketIOService.sendMessageSelf(client, "listen", dataTapChanger);

                log.info("namepace:{}, data:{}, dataTapChanger:{}", nameSpace, data, dataTapChanger);
            } catch (Exception e) {
                socketIOService.sendMessageSelf(client, "listen", e.getMessage());
                log.error("error:{}", e.getMessage());
            }
        };
    }
}

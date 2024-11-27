package dev.boenkkk.simulator_outstation_dnp3.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SocketIOService {

    public final String DEFAULT_ROOM = "DEFAULT_ROOM";

    @Autowired
    private SocketIONamespace localRemoteNamespace;

    @Autowired
    private SocketIONamespace cbOpenCloseNamespace;

    @Autowired
    private SocketIONamespace tapChangerNamespace;

    private SocketIONamespace getNamespace(String namespace) {
        switch (namespace) {
            case "/local-remote" -> {
                return localRemoteNamespace;
            }
            case "/cb-open-close" -> {
                return cbOpenCloseNamespace;
            }
            case "/tap-changer" -> {
                return tapChangerNamespace;
            }
            default -> {
                return null;
            }
        }
    }

    public void sendMessageSelf(SocketIOClient client, String eventName, Object message) {
        if (client != null) {
            client.sendEvent(eventName, message);
        }
    }

    public void broadcastToAllRoomsInNamespace(String namespace, String eventName, Object message) {
        SocketIONamespace ns = getNamespace(namespace);
        if (ns != null) {
            ns.getBroadcastOperations().sendEvent(eventName, message);
            log.info("Broadcasting to all rooms in namespace: {}|{}", eventName, namespace);
        }
    }

    public void broadcastToNamespace(String namespace, String room, String eventName, Object message) {
        SocketIONamespace ns = getNamespace(namespace);
        if (ns != null) {
            ns.getRoomOperations(room).sendEvent(eventName, message);
            log.info("Broadcasting: {}|{}|{}|{}", eventName, room, namespace, message);
        }
    }

    public void broadcastToDefaultRoom(String namespace, String eventName, Object message) {
        SocketIONamespace ns = getNamespace(namespace);
        if (ns != null) {
            ns.getRoomOperations(DEFAULT_ROOM).sendEvent(eventName, message);
            log.info("Broadcasting: {}|{}|{}|{}", eventName, DEFAULT_ROOM, namespace, message);
        }
    }
}

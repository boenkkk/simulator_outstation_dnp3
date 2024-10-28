package dev.boenkkk.simulator_outstation_dnp3.model.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.boenkkk.simulator_outstation_dnp3.properties.Dnp3Properties;
import dev.boenkkk.simulator_outstation_dnp3.util.HttpUtil;
import io.stepfunc.dnp3.ConnectionState;
import io.stepfunc.dnp3.ConnectionStateListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionStateListenerImpl implements ConnectionStateListener {

    private final Dnp3Properties dnp3Properties;

    public ConnectionStateListenerImpl(Dnp3Properties dnp3Properties) {
        this.dnp3Properties = dnp3Properties;
    }

    @Override
    public void onChange(ConnectionState connectionState) {
        try {
            int stateInt = switch (connectionState.name().toUpperCase()) {
                case "CONNECTED" -> 1;
                case "DISCONNECTED" -> 0;
                default -> 2;
            };

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("server_name", dnp3Properties.getOutstationName());
            jsonNode.put("ip_address", HttpUtil.getIpAddress(dnp3Properties.getOutstationNetworkInterface()));
            jsonNode.put("id_server", 6666666); // TODO: get from table
            jsonNode.put("protocol", dnp3Properties.getOutstationProtocol());
            jsonNode.put("status", stateInt);
            jsonNode.put("status_name", connectionState.name());
            jsonNode.put("time", System.currentTimeMillis());
            String json = objectMapper.writeValueAsString(jsonNode);

            log.info(json);
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }
}

package dev.boenkkk.simulator_outstation_dnp3.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app.dnp3")
public class Dnp3Properties {

    private int outstationId;
    private String outstationName;
    private String outstationNetworkInterface;
    private String outstationProtocol;
    private String outstationType;

    private String outstationHost;
    private int outstationPort;
    private String outstationSerialPort;
    private int outstationAddress;
    private int masterAddress;

    private int keepAliveTimeout;
    private int numCoreThreads;
}

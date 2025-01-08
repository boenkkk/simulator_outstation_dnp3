package dev.boenkkk.simulator_outstation_dnp3.model;

import lombok.Data;

@Data
public class Dnp3ServerOutstationModel {

    private String serialPort;
    private String tcpSourceIpAddress;
    private Integer tcpPortNumber;
    private Integer slaveAddress;
    private Integer masterAddress;
}

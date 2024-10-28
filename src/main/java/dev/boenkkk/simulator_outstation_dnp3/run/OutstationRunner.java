package dev.boenkkk.simulator_outstation_dnp3.run;

import dev.boenkkk.simulator_outstation_dnp3.model.Dnp3ServerOutstationModel;
import dev.boenkkk.simulator_outstation_dnp3.service.ServerService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OutstationRunner implements ApplicationRunner {

    @Autowired
    private ServerService serverService;

    @Value("${app.dnp3.outstation-host}")
    private String outstationHost;

    @Value("${app.dnp3.outstation-port}")
    private String outstationPort;

    @Value("${app.dnp3.outstation-address}")
    private String outstationAddress;

    @Value("${app.dnp3.master-address}")
    private String masterAddress;

    @Override
    public void run(ApplicationArguments args) {
        // get outstations
        log.info("outstation: {}|{}|{}|{}", outstationHost, outstationPort, outstationAddress, masterAddress);

        Dnp3ServerOutstationModel outstation = new Dnp3ServerOutstationModel();
        outstation.setTcpSourceIpAddress(outstationHost);
        outstation.setTcpPortNumber(Integer.valueOf(outstationPort));
        outstation.setSlaveAddress(Integer.valueOf(outstationAddress));
        outstation.setMasterAddress(Integer.valueOf(masterAddress));
        log.info("outstation: {}", outstation);

        // add server
        serverService.addServer(outstation);
    }
}

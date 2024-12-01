package dev.boenkkk.simulator_outstation_dnp3.run;

import dev.boenkkk.simulator_outstation_dnp3.model.Dnp3ServerOutstationModel;
import dev.boenkkk.simulator_outstation_dnp3.properties.Dnp3Properties;
import dev.boenkkk.simulator_outstation_dnp3.service.ServerService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OutstationRunner implements ApplicationRunner {

    @Autowired
    private ServerService serverService;

    @Autowired
    private Dnp3Properties dnp3Properties;

    @Override
    public void run(ApplicationArguments args) {
        // get outstations
        Dnp3ServerOutstationModel outstation = new Dnp3ServerOutstationModel();
        outstation.setTcpSourceIpAddress(dnp3Properties.getOutstationHost());
        outstation.setTcpPortNumber(dnp3Properties.getOutstationPort());
        outstation.setSlaveAddress(dnp3Properties.getOutstationAddress());
        outstation.setMasterAddress(dnp3Properties.getMasterAddress());
        log.info("outstation: {}", outstation);

        // add server
        serverService.addServer(outstation);

        // run scheduler
        serverService.runScheduller();
    }
}

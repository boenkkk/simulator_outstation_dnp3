package dev.boenkkk.simulator_outstation_dnp3.scheduler;

import dev.boenkkk.simulator_outstation_dnp3.service.OutstationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class OutstationScheduler {

    @Autowired
    private OutstationsService outstationsService;

    @Scheduled(fixedRate = 1000)
    public void getAllRunningInstance(){
        outstationsService.getAllRunningInstance();
    }
}

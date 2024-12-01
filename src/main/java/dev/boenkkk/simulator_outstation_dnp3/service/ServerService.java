package dev.boenkkk.simulator_outstation_dnp3.service;

import dev.boenkkk.simulator_outstation_dnp3.model.Dnp3ServerOutstationModel;
import dev.boenkkk.simulator_outstation_dnp3.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3.model.OutstationBean.OutstationData;
import dev.boenkkk.simulator_outstation_dnp3.model.callback.ConnectionStateListenerImpl;
import dev.boenkkk.simulator_outstation_dnp3.model.callback.ControlHandlerImpl;
import dev.boenkkk.simulator_outstation_dnp3.model.callback.OutstationApplicationImpl;
import dev.boenkkk.simulator_outstation_dnp3.model.callback.OutstationInformationImpl;
import dev.boenkkk.simulator_outstation_dnp3.config.DatabaseConfigImpl;
import dev.boenkkk.simulator_outstation_dnp3.model.RuntimeChannel;
import dev.boenkkk.simulator_outstation_dnp3.properties.Dnp3Properties;
import dev.boenkkk.simulator_outstation_dnp3.scheduler.SchedulerTask;
import dev.boenkkk.simulator_outstation_dnp3.util.JsonUtil;
import io.stepfunc.dnp3.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static org.joou.Unsigned.ushort;

@Service
@Slf4j
public class ServerService {


    @Autowired
    private RuntimeChannel runtimeChannel;

    @Autowired
    private Dnp3Properties dnp3Properties;

    @Autowired
    private OutstationsService outstationsService;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private DatapointService datapointService;

    @Autowired
    private SocketIOService socketIOService;

    @Autowired
    private SchedulerTask schedulerTask;

    public void addServer(Dnp3ServerOutstationModel dnp3ServerOutstation){
        log.info(dnp3ServerOutstation.toString());
        String outstationTcpAddress = dnp3ServerOutstation.getTcpSourceIpAddress()+":"+dnp3ServerOutstation.getTcpPortNumber();
        String endpoint = dnp3ServerOutstation.getTcpSourceIpAddress();

        // ANCHOR: create_tcp_server
        OutstationServer server = OutstationServer.createTcpServer(
            runtimeChannel.getRuntime(),
            LinkErrorMode.CLOSE,
            outstationTcpAddress
        );
        // ANCHOR_END: create_tcp_server

        // ANCHOR: outstation_config
        // create an outstation configuration with default values
        int maxEventBuffer = 10;
        OutstationConfig outstationConfig = new OutstationConfig(
            ushort(dnp3ServerOutstation.getSlaveAddress()),
            ushort(dnp3ServerOutstation.getMasterAddress()),
            // event buffer sizes
            new EventBufferConfig(
                ushort(maxEventBuffer), // binary
                ushort(maxEventBuffer), // double-bit binary
                ushort(maxEventBuffer), // binary output status
                ushort(maxEventBuffer), // counter
                ushort(maxEventBuffer), // frozen counter
                ushort(maxEventBuffer), // analog
                ushort(maxEventBuffer), // analog output status
                ushort(maxEventBuffer) // octet string
            )
        ).withKeepAliveTimeout(Duration.ofSeconds(dnp3Properties.getKeepAliveTimeout()));
        outstationConfig.decodeLevel.application = AppDecodeLevel.NOTHING;
        outstationConfig.decodeLevel.transport = TransportDecodeLevel.NOTHING;
        outstationConfig.decodeLevel.link = LinkDecodeLevel.NOTHING;
        outstationConfig.decodeLevel.physical = PhysDecodeLevel.NOTHING;
        // ANCHOR_END: outstation_config

        // ANCHOR: tcp_server_add_outstation
        Outstation outstation = server.addOutstation(
            outstationConfig,
            new OutstationApplicationImpl(jsonUtil),
            new OutstationInformationImpl(),
            new ControlHandlerImpl(datapointService, socketIOService),
            new ConnectionStateListenerImpl(dnp3Properties),
            AddressFilter.any()
        );
        // ANCHOR_END: tcp_server_add_outstation

        // ANCHOR: tcp_server_bind
        server.bind();
        // ANCHOR_END: tcp_server_bind

        // Setup initial points
        // ANCHOR: database_init
        outstation.transaction(DatabaseConfigImpl::initializeDatabaseDefault);
        // ANCHOR_END: database_init

        // automaticly started channel
        outstation.enable();

        // register to bean
        OutstationBean outstationBean = outstationsService.getInstance();
        outstationBean.getData().put(
            endpoint,
            OutstationData.builder()
                .outstationServer(server)
                .outstation(outstation)
                .build()
        );
        outstationsService.registerBean(outstationBean);
    }

    public void runScheduller(){
        schedulerTask.toggleScheduler("SCHEDULER_TAP_CHANGER", true, 1, 0, 0, 32);
        schedulerTask.toggleScheduler("SCHEDULER_VR", true, 1, 1, 309, 330);
        schedulerTask.toggleScheduler("SCHEDULER_P", true, 1, 2, 300, 400);
        schedulerTask.toggleScheduler("SCHEDULER_Q", true, 1, 3, 500, 600);
        schedulerTask.toggleScheduler("SCHEDULER_PF", true, 1, 4, 0, 1);
        schedulerTask.toggleScheduler("SCHEDULER_VS", true, 1, 5, 309, 330);
        schedulerTask.toggleScheduler("SCHEDULER_VT", true, 1, 6, 309, 330);
        schedulerTask.toggleScheduler("SCHEDULER_F", true, 1, 7, 49.5, 51.5);
        schedulerTask.toggleScheduler("SCHEDULER_IR", true, 1, 8, 270, 289);
        schedulerTask.toggleScheduler("SCHEDULER_IS", true, 1, 9, 270, 289);
        schedulerTask.toggleScheduler("SCHEDULER_IT", true, 1, 10, 270, 289);
    }
}

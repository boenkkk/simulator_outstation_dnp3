package dev.boenkkk.simulator_outstation_dnp3.service;

import dev.boenkkk.simulator_outstation_dnp3.config.AppConfig;
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
import org.joou.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class ServerService {

    @Autowired
    private AppConfig appConfig;

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

    public String addTcpServer(Dnp3ServerOutstationModel dnp3ServerOutstation){
        log.info(dnp3ServerOutstation.toString());
        String outstationId = String.valueOf(dnp3Properties.getOutstationId());
        String outstationTcpAddress = dnp3ServerOutstation.getTcpSourceIpAddress()+":"+dnp3ServerOutstation.getTcpPortNumber();

        // ANCHOR: create_tcp_server
        OutstationServer server = OutstationServer.createTcpServer(
            runtimeChannel.getRuntime(),
            LinkErrorMode.CLOSE,
            outstationTcpAddress
        );
        // ANCHOR_END: create_tcp_server

        // ANCHOR: tcp_server_add_outstation
        Outstation outstation = server.addOutstation(
            appConfig.getDatabaseConfig(dnp3Properties, dnp3ServerOutstation),
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
            outstationId,
            OutstationData.builder()
                .outstation(outstation)
                .build()
        );
        outstationsService.registerBean(outstationBean);

        return outstationId;
    }

    public String addSerialServer(Dnp3ServerOutstationModel dnp3ServerOutstation){
        log.info(dnp3ServerOutstation.toString());
        String outstationId = String.valueOf(dnp3Properties.getOutstationId());
        String serialPort = dnp3ServerOutstation.getSerialPort();

        SerialSettings serialSettings = new SerialSettings()
            .withBaudRate(UInteger.valueOf(9600))
            .withDataBits(DataBits.EIGHT)
            .withFlowControl(FlowControl.NONE)
            .withStopBits(StopBits.ONE)
            .withParity(Parity.NONE);

        Outstation outstation = Outstation.createSerialSession2(
            runtimeChannel.getRuntime(),
            serialPort,
            serialSettings,
            Duration.ofSeconds(5), // try to open the port every 5 seconds
            appConfig.getDatabaseConfig(dnp3Properties, dnp3ServerOutstation),
                new OutstationApplicationImpl(jsonUtil),
                new OutstationInformationImpl(),
                new ControlHandlerImpl(datapointService, socketIOService),
            state -> System.out.println("Port state change: " + state)
        );

        // Setup initial points
        // ANCHOR: database_init
        outstation.transaction(DatabaseConfigImpl::initializeDatabaseDefault);
        // ANCHOR_END: database_init

        // automaticly started channel
        outstation.enable();

        // register to bean
        OutstationBean outstationBean = outstationsService.getInstance();
        outstationBean.getData().put(
            outstationId,
            OutstationData.builder()
                .outstation(outstation)
                .build()
        );
        outstationsService.registerBean(outstationBean);

        return outstationId;
    }

    public void runScheduller(String outstationId){
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_TAP_CHANGER", true, 1, 0, 0, 32);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_VR", true, 1, 1, 309, 330);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_P", true, 1, 2, 300, 400);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_Q", true, 1, 3, 500, 600);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_PF", true, 1, 4, 0, 1);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_VS", true, 1, 5, 309, 330);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_VT", true, 1, 6, 309, 330);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_F", true, 1, 7, 49.5, 51.5);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_IR", true, 1, 8, 270, 289);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_IS", true, 1, 9, 270, 289);
        schedulerTask.toggleScheduler(outstationId, "SCHEDULER_IT", true, 1, 10, 270, 289);
    }
}

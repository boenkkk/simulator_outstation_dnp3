package dev.boenkkk.simulator_outstation_dnp3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.boenkkk.simulator_outstation_dnp3.model.Dnp3ServerOutstationModel;
import dev.boenkkk.simulator_outstation_dnp3.model.OutstationBean;
import dev.boenkkk.simulator_outstation_dnp3.model.RuntimeChannel;
import dev.boenkkk.simulator_outstation_dnp3.model.callback.LoggerImpl;
import dev.boenkkk.simulator_outstation_dnp3.model.callback.ShutdownListener;
import dev.boenkkk.simulator_outstation_dnp3.properties.Dnp3Properties;
import dev.boenkkk.simulator_outstation_dnp3.util.JsonUtil;
import io.stepfunc.dnp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static org.joou.Unsigned.ushort;

@Configuration
public class AppConfig {

    @Autowired
    private Dnp3Properties dnp3Properties;

    public OutstationConfig getDatabaseConfig(Dnp3Properties dnp3Properties, Dnp3ServerOutstationModel dnp3ServerOutstation) {
        // ANCHOR: outstation_config
        // create an outstation configuration with default values
        // int maxEventBuffer = 50;
        OutstationConfig outstationConfig = new OutstationConfig(
            ushort(dnp3ServerOutstation.getSlaveAddress()),
            ushort(dnp3ServerOutstation.getMasterAddress()),
            // event buffer sizes
            new EventBufferConfig(
                ushort(50), // binary
                ushort(50), // double-bit binary
                ushort(50), // binary output status
                ushort(5), // counter
                ushort(5), // frozen counter
                ushort(50), // analog
                ushort(50), // analog output status
                ushort(3) // octet string
            )
        ).withKeepAliveTimeout(Duration.ofSeconds(dnp3Properties.getKeepAliveTimeout()));
        outstationConfig.decodeLevel.application = AppDecodeLevel.NOTHING;
        outstationConfig.decodeLevel.transport = TransportDecodeLevel.NOTHING;
        outstationConfig.decodeLevel.link = LinkDecodeLevel.NOTHING;
        outstationConfig.decodeLevel.physical = PhysDecodeLevel.NOTHING;
        // ANCHOR_END: outstation_config

        return outstationConfig;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public OutstationBean outstationBean(){
        return new OutstationBean();
    }

    @Bean
    // Setup logging
    public LoggerImpl loggerImpl(JsonUtil jsonUtil){
        return new LoggerImpl(jsonUtil);
    }

    @Bean
    // Create the Tokio runtime
    public RuntimeChannel runtimeChannel(LoggerImpl loggerImpl){
        LoggingConfig loggingConfig = new LoggingConfig().withOutputFormat(LogOutputFormat.JSON);
        Logging.configure(loggingConfig, loggerImpl);
        return new RuntimeChannel(dnp3Properties.getNumCoreThreads());
    }

    @Bean(destroyMethod = "destroy")
    public ShutdownListener shutdownListener(RuntimeChannel runtimeChannel) {
        return new ShutdownListener(runtimeChannel);
    }
}

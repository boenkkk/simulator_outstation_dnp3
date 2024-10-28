package dev.boenkkk.simulator_outstation_dnp3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Configuration
public class AppConfig {

    @Autowired
    private Dnp3Properties dnp3Properties;

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

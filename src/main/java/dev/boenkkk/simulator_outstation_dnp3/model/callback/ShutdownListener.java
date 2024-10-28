package dev.boenkkk.simulator_outstation_dnp3.model.callback;

import dev.boenkkk.simulator_outstation_dnp3.model.RuntimeChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ShutdownListener {

    private RuntimeChannel runtimeChannel;

    public ShutdownListener(RuntimeChannel runtimeChannel) {
        this.runtimeChannel = runtimeChannel;
    }

    public void destroy() {
        log.info("Callback triggered - bean destroy method.");
        log.info("###STOPing###");
        shutdownRuntimeChannel();
        log.info("###STOP FROM THE LIFECYCLE###");
    }

    private void shutdownRuntimeChannel() {
        runtimeChannel.dispose(false);
        runtimeChannel.getRuntime().shutdown();
    }

}

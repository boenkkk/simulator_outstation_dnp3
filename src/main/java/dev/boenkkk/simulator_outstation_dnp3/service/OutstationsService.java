package dev.boenkkk.simulator_outstation_dnp3.service;

import dev.boenkkk.simulator_outstation_dnp3.model.OutstationBean;
import io.stepfunc.dnp3.Outstation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class OutstationsService {

    public static final String OUTSTATION_MAP_BEAN_NAME = "outstationBean";

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private ConfigurableApplicationContext configContext;

    public Optional<OutstationBean.OutstationData> getoOutstationData(String endpoint){
        return Optional.of(getInstance().getData().get(endpoint));
    }

    public OutstationBean getInstance(){
        return (OutstationBean) appContext.getBean(OUTSTATION_MAP_BEAN_NAME);
    }

    public Outstation getOutstation(String endpoint){
        return getoOutstationData(endpoint).map(OutstationBean.OutstationData::getOutstation).orElse(null);
    }

    public void registerBean(OutstationBean outstationBean) {
        DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) configContext.getBeanFactory();
        registry.destroySingleton(OUTSTATION_MAP_BEAN_NAME);
        registry.registerSingleton(OUTSTATION_MAP_BEAN_NAME, outstationBean);
    }

    public Map<String, Object> getAllRunningInstance(){
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("total", getInstance().getData().size());
        retMap.put("data", getInstance().getData());

        log.info(retMap.toString());
        return retMap;
    }
}

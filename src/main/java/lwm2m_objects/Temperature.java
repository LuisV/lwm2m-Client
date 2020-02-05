package lwm2m_objects;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.leshan.client.request.ServerIdentity;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;


public class Temperature extends BaseInstanceEnabler  {

    private static final Logger LOG = LoggerFactory.getLogger(Temperature.class);

    //Read
    private static final int SENSOR = 5700;
    private static final int MIN_MEASURED = 5601;
    private static final int MAX_MEASURED = 5602;
    private static final int MIN_RANGE = 5603;
    private static final int MAX_RANGE = 5604;
    private static final int SENSOR_UNITS = 5701;
    //Execute
    private static final int RESET_MIN_AND_MAX = 5605;

    private static final List<Integer> supportedResources = Arrays.asList(SENSOR, MIN_MEASURED, MAX_MEASURED, MIN_RANGE, MAX_RANGE, SENSOR_UNITS );
    public static List<Integer> getSupportedresources() {
        return supportedResources;
    }

    private static float sensorValue;
    private static float oldTemp = 0;
    private static float minMeasuredValue;
    private static float maxMeasuredValue;
    private static float minRangeValue = 100;
    private static float maxRangeValue = 0;
    private static String sensorUnits = "Cel";

    private final ScheduledExecutorService scheduler;


    public Temperature() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Temp Sensor"));
        scheduler.scheduleAtFixedRate(new Runnable(){
            @Override
            public void run() {
                changeTemp();
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    @Override
    public synchronized ReadResponse read(ServerIdentity indentity, int recourceId) {
        LOG.info("Read on Device Resource " + recourceId);
        switch(recourceId) {
            case SENSOR:
                return ReadResponse.success(recourceId, getSensorValue());
            case MIN_MEASURED:
                return ReadResponse.success(recourceId, getMinMeasuredValue());
            case MAX_MEASURED:
                return ReadResponse.success(recourceId, getMaxMeasuredValue());
            case MIN_RANGE:
                return ReadResponse.success(recourceId, getMinRangeValue());
            case MAX_RANGE:
                return ReadResponse.success(recourceId,getMaxRangeValue());
            case SENSOR_UNITS:
                return ReadResponse.success(recourceId, getSensorUnits());
            default:
                return super.read(indentity, recourceId);
        }
    }

    @Override
    public synchronized ExecuteResponse execute(ServerIdentity identity, int resourceId, String params) {
        LOG.info("Execute on Device resource " + resourceId);
        switch(resourceId) {
            case RESET_MIN_AND_MAX:
                changeTemp();
                return ExecuteResponse.success();
            default:
                return super.execute(identity, resourceId, params);
        }
    }



    public static SystemInfo si = new SystemInfo();
    static HardwareAbstractionLayer hal = si.getHardware();

    private static void printPowerSources(PowerSource[] powerSources) {
        StringBuilder sb = new StringBuilder("Power Sources: ");
        if (powerSources.length == 0) {
            sb.append("Unknown");
        }
        for (PowerSource powerSource : powerSources) {
            sb.append("\n ").append(powerSource.toString());
        }

    }

    public  void changeTemp() {

        float newTemp = (float) hal.getSensors().getCpuTemperature();


        int minOrMax=0;
        Temperature.sensorValue = newTemp;
        if(newTemp<minMeasuredValue)
        {
            Temperature.minMeasuredValue = newTemp;
            minOrMax= MAX_MEASURED;
        }
        if(newTemp>maxMeasuredValue)
        {
            Temperature.maxMeasuredValue = newTemp;
            minOrMax= MIN_MEASURED;
        }

        if(minOrMax!=0){
            fireResourcesChange(SENSOR, minOrMax);
        } else if( (newTemp>= oldTemp+0.2) || (newTemp<= oldTemp-0.2)){
            oldTemp = newTemp;
            fireResourcesChange(SENSOR);
        }
    }

    public static float getSensorValue() {
        return sensorValue;
    }
    public static float getMinMeasuredValue() {
        return minMeasuredValue;
    }
    public static float getMaxMeasuredValue() {
        return maxMeasuredValue;
    }
    public static float getMinRangeValue() {
        return minRangeValue;
    }
    public static float getMaxRangeValue() {
        return maxRangeValue;
    }
    public static String getSensorUnits() {
        return sensorUnits;
    }
}

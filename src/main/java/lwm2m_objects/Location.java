package lwm2m_objects;

import org.eclipse.leshan.client.request.ServerIdentity;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.response.ReadResponse;

import java.util.Date;


public class Location extends BaseInstanceEnabler {

    double latitude;
    double longitude;
    double altitude;
    double uncertantyRadius;

    public Location(){
        latitude = 50.523;
        longitude = 20.3232;
        altitude = 100.0;
        uncertantyRadius = 50;
    }
    @Override
    public ReadResponse read(ServerIdentity identity, int resourceid) {

        // There may be a more efficient way...
        switch (resourceid){
            case 0: return ReadResponse.success(resourceid, latitude);
            case 1: return ReadResponse.success(resourceid, longitude);
            case 2: return ReadResponse.success(resourceid, altitude);
            case 3: return ReadResponse.success(resourceid, uncertantyRadius);
            case 5: return ReadResponse.success(resourceid, new Date());
            // No Accelerometer - No speed/velocity
        }
        return super.read(identity, resourceid);
    }


}

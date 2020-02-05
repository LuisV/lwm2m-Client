package lwm2m_objects;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.leshan.client.request.ServerIdentity;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.response.ReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Location extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(Location.class);

    private static final List<Integer> supportedResources = Arrays.asList(0, 1, 5);

    private float latitude;
    private float longitude;
    private Date timestamp;

    public Location() {
        this(0f, 0f);
    }

    public Location(Float latitude, Float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

        timestamp = new Date();
    }

    @Override
    public ReadResponse read(ServerIdentity identity, int resourceid) {
        LOG.info("Read on Location Resource " + resourceid);
        switch (resourceid) {
            case 0:
                return ReadResponse.success(resourceid, getLatitude());
            case 1:
                return ReadResponse.success(resourceid, getLongitude());
            case 5:
                return ReadResponse.success(resourceid, getTimestamp());
            default:
                return super.read(identity, resourceid);
        }
    }


    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        return supportedResources;
    }


}
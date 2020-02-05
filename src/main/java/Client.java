
import lwm2m_objects.Location;
import lwm2m_objects.Temperature;
import lwm2m_objects.Device;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.leshan.LwM2m;
import org.eclipse.leshan.LwM2mId;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Security;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;

import org.eclipse.leshan.core.model.StaticModel;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.util.Hex;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Client {

    static Location mylocation = new Location();
    static Temperature temp = new Temperature();
    private final static String[] modelPaths = new String[] { "3303.xml" };

    public static void main (String[] args){


        List<ObjectModel> models = ObjectLoader.loadDefault();
        models.addAll(ObjectLoader.loadDdfResources("/models", modelPaths));
        //LwM2mModelProvider pro = new StaticModelProvider(models);
        byte[] pskIdentity= null;
        byte[] pskKey = null;

        pskIdentity= "Chicken".getBytes();
        pskKey= Hex.decodeHex("31323334".toCharArray());
        ObjectsInitializer initializer = new ObjectsInitializer(new StaticModel(models));


        Security a = Security.psk("coap://localhost:5684", 123,pskIdentity,pskKey);
        Security s = Security.psk("coap://18.219.244.119:5684/dashboard/", 123, pskIdentity,pskKey);
        Security b = Security.noSec("coap://localhost:5683", 12345);

        initializer.setInstancesForObject(LwM2mId.SECURITY, s);
        initializer.setInstancesForObject(LwM2mId.SERVER, new Server(123, 20, BindingMode.U, false));
        initializer.setInstancesForObject(LwM2mId.DEVICE, new Device());
        initializer.setInstancesForObject(6, mylocation);
        initializer.setInstancesForObject(3303, temp);
        //SecurityInfo info1 = SecurityInfo.newPreSharedKeyInfo("chicken", "identity1", "secret1".getBytes());

        String endpoint = "Chicken" ; // choose an endpoint name
        NetworkConfig coapConfig;
        File configFile = new File(NetworkConfig.DEFAULT_FILE_NAME);
        if (configFile.isFile()) {
            coapConfig = new NetworkConfig();
            coapConfig.load(configFile);
        } else {
            coapConfig = LeshanClientBuilder.createDefaultNetworkConfig();
            coapConfig.store(configFile);
        }

        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        builder.setCoapConfig(coapConfig);
        builder.setLocalAddress(null, 0);
        builder.setObjects(initializer.createAll());
        LeshanClient client = builder.build();

        client.start();




    }
}

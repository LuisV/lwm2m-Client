
import lwm2m_objects.Location;
import org.eclipse.leshan.LwM2m;
import org.eclipse.leshan.LwM2mId;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Device;
import org.eclipse.leshan.client.object.Security;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.request.BindingMode;

import java.util.Scanner;

public class Client {

    static Location mylocation = new Location();
    public static void main (String[] args){

        ObjectsInitializer initializer = new ObjectsInitializer();

        initializer.setInstancesForObject(LwM2mId.SECURITY, Security.noSec("coap://localhost:5683", 12345));
        initializer.setInstancesForObject(LwM2mId.SERVER, new Server(12345, 30, BindingMode.U, true));
        initializer.setInstancesForObject(LwM2mId.DEVICE, new Device("Testing device", "00001", "11110", "U"));
        initializer.setInstancesForObject(6, mylocation);
        String endpoint = "endpoint me test" ; // choose an endpoint name

        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        builder.setObjects(initializer.createAll());
        LeshanClient client = builder.build();

        client.start();

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                String nextMove = scanner.next();
                mylocation.moveLocation(nextMove);
            }
        }
    }
}

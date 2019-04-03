package model;

import java.net.InetAddress;
import java.util.HashSet;

import edu.wpi.SimplePacketComs.FloatPacketType;
import edu.wpi.SimplePacketComs.phy.UDPSimplePacketComs;

/**
 * to use Elevator myInstance = ExampleClient.get("MyRobotName");
 * then myInstance.data[x] where x is...
 */
public class Elevator extends UDPSimplePacketComs {
    private static byte[] dev = new byte[] { (byte) 170, (byte) 20, (byte) 10, (byte) 2 };
    private FloatPacketType IMU = new FloatPacketType(1871, 64);
    public double[] data = new double[15];
    private Elevator(InetAddress add) throws Exception {
        super(add);
        addPollingPacket(IMU);
        addEvent(1871,()->{
            readFloats(1871, data);
        });
        connect();
    }
    // Search for devices instead of just construction them
    public static Elevator get(String name) throws Exception {
        HashSet<InetAddress> nets = UDPSimplePacketComs.getAllAddresses(name);
        System.out.println("Number of devices found "+nets.size());
        if(nets.size()>0)
            return new Elevator(nets.iterator().next());
        return new Elevator(InetAddress.getByAddress(dev));
    }

   // Elevator(){}
}

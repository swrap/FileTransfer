import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Test
{
    private static Socket s2;

    public static void main(String [] args)
    {
//        try {
//            System.out.println(InetAddress.getLocalHost().getHostAddress());
//        } catch (UnknownHostException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        try {
            Thread th = new Thread()
            {
              public void run()
              {
                  ServerSocket ser;
                try {
                    ser = new ServerSocket(4400);
                    s2 = ser.accept();
                    System.out.println(s2.getRemoteSocketAddress() + " " + s2.getPort() + 
                            " " + s2.getInetAddress());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
              }
            };
            th.start();
            Socket s1 = new Socket("localhost",4400);
            
//            ObjectOutputStream out = new ObjectOutputStream(s2.getOutputStream());
//            out.writeInt(122);
//            out.flush();
            
//            ObjectInputStream in = new ObjectInputStream(s1.getInputStream());
//            System.out.println(in.readInt());
            
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}

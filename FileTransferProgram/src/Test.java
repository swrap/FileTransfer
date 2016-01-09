import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;

import javax.swing.filechooser.FileSystemView;


public class Test
{
    private static Socket s2;

    public static void main(String [] args)
    {
        try {
            Thread th = new Thread()
            {
              public void run()
              {
                  ServerSocket ser;
                try {
                    ser = new ServerSocket(4400);
                    s2 = ser.accept();
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
            
            ObjectInputStream in = new ObjectInputStream(s1.getInputStream());
            System.out.println(in.readInt());
            
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}

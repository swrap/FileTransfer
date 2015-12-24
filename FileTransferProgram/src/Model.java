import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.swing.filechooser.FileSystemView;

public class Model
{
    private static int MAX_FILE_SIZE = 4096;
    
    private SSLSocket ssl;
    private boolean connected = false;
    private Log log;
    private String username;
    private Socket mysoc, theirsoc;
    private OutputStreamWriter writer;
    private OutputStream out;
    
    public Model()
    {
        username = "Sam";
        log = new Log(username);
        log.addMessage("HELLO",true);
        log.addMessage("SUP",false);
        System.out.println(log);
    }
    
    public void sendMessage(String message)
    {
        try
        {
            log.addMessage(message, true);
            writer.write(message);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void sendFile(String filename) throws FileNotFoundException
    {
        try{
            
            writer.write("FILEFROMSENDER:CODE:59895698 " + filename);
            writer.flush();
            
            File file = new File(filename);
            FileReader fileread = new FileReader(file);
            
            char [] buf = new char[MAX_FILE_SIZE];
            int partitions = (int) ((file.length()/MAX_FILE_SIZE)+1);
            
            ByteArrayOutputStream byteout = new ByteArrayOutputStream(MAX_FILE_SIZE);
            
            for(int i = 0; i < partitions; ++i)
            {
                    int num = fileread.read(buf, 0, MAX_FILE_SIZE);
                    String temp = new String(buf);
                    if(temp.length() != MAX_FILE_SIZE)
                    {
                        temp = temp.substring(0, num);
                    }
                    byteout.write(temp.getBytes(), 0, MAX_FILE_SIZE);
                    byteout.writeTo(out);
                    out.flush();
            }
            
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void connect(String ip, int port)
    {

        //opens my socket for recieving input
        try
        {
            mysoc = new Socket(ip, port);
        } catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        //opens server socket to recieve one user connection
        try
        {
            ServerSocket ser = new ServerSocket();
            theirsoc = ser.accept();
            out = theirsoc.getOutputStream();
            writer = new OutputStreamWriter(out);
            ser.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
        //opens scanner to receive input
        try
        {
            Scanner scanner = new Scanner(mysoc.getInputStream());
            while(scanner.hasNextLine())
            {
                String s = scanner.nextLine();
                
                //receiving file
                if(s.startsWith("FILEFROMSENDER:TOTALLENGTH:59895698 "))
                {
                    //name, size
                    String [] both = s.split("\\s+");
                    if(both.length != 3)
                    {
                        /**need to add throw error
                         * or there will be a 
                         * problem
                         */
                    }
                    
                    //makes new folder on desktop for downloads
                    //later set default values
                    File f = new File(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\Downloads");
                    if(!f.exists())
                        f.mkdirs();
                }
                //receiving message
                else
                {
                    log.addMessage(s, false);
                }
            }
            
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
//        will have to add to ssl
//        SSLSocketFactory fac = (SSLSocketFactory) SSLSocketFactory.getDefault();
//        soc = fac.crea
    }
    
    public String getLog()
    {
        return log.toString();
    }
    
    public static void main(String [] args)
    {
        System.out.println("NO");
        new Model();
        System.out.println("YES");
    }
}

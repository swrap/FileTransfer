import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.swing.filechooser.FileSystemView;

public class Model extends Observable
{
    public static final int WAITING_FOR_CONNECTION = 0, CONNECTED = 1, NOT_CONNECTED = 2;
    private static final int DISCONNECT = 0, MESSAGE = 1, FILE = 2;
    private static final int MAX_FILE_SIZE = 4096;
    
    private SSLSocket ssl;
    private Log log;
    private String username;
    private Socket mysoc;
    private OutputStreamWriter writer;
    private ObjectOutputStream objectout;
    private OutputStream out;
    private int state = NOT_CONNECTED;
    private int myport = 5531, theirport = 5531;
    private String theirip;
    private Thread th;
    private boolean stop = false;
    
    public Model()
    {
        username = "Sam";
        log = new Log(username);
    }
    
    public int getMyPort()
    {
        return myport;
    }
    
    public void setMyPort(String myport)
    {
        //need to throw exception
        this.myport = Integer.parseInt(myport);
    }
    
    public int isConnected()
    {
        return state;
    }
    
    public String getTheirIp()
    {
        return theirip;
    }
    
    public int getTheirPort()
    {
        return theirport;
    }
    
    public void setTheirPort(String theirport)
    {
        //need to throw exception
        this.theirport = Integer.parseInt(theirport);
    }
    
    public void sendMessage(String message)
    {
        if(message.length() > 0 && state == this.CONNECTED)
        {
            try
            {
                objectout.writeInt(this.MESSAGE);
                objectout.flush();
                log.addMessage(message, true);
                writer.write(log.getLastMessage() + "\n");
                writer.flush();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.setChanged();
            this.notifyObservers();
        }
    }
    
    public void sendFile(String pathname, String filename) throws FileNotFoundException
    {
        try{
            
            writer.write("FILEFROMSENDER:CODE:59895698 " + filename);
            writer.flush();
            
            File file = new File(pathname, filename);
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
            e.printStackTrace();
        }
    }
    
    public void connect(String ip, String soc)
    {
        state = this.WAITING_FOR_CONNECTION;
        this.setChanged();
        this.notifyObservers();
        
        theirip = ip;
        theirport = Integer.parseInt(soc);
        
        th = new Thread()
        {
            public void run()
            {
                try
                {
                    if(stop)
                    {
                        stop = true;
                        return;
                    }
                    mysoc = new Socket(theirip, theirport);
                    state = Model.this.CONNECTED;
                    Model.this.setChanged();
                    Model.this.notifyObservers();
                }catch (ConnectException e1){ 
                    //we are sure that this means the client was refused opening connection to listen
                    
                    //starting the server here to wait
                    try
                    {
                        ServerSocket ser = new ServerSocket(myport);
                        if(stop)
                        {
                            stop = true;
                            return;
                        }
                        mysoc = ser.accept();
                        theirip = ser.getInetAddress().getHostAddress();
                        out = mysoc.getOutputStream();
                        writer = new OutputStreamWriter(out);
                        ser.close();
                        state = Model.this.CONNECTED;
                        Model.this.setChanged();
                        Model.this.notifyObservers();
                        
                    } catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                    
                }catch (UnknownHostException e3){
                    if(stop)
                    {
                        stop = true;
                        return;
                    }
                    log.addMessage("Unknown host: AKA the ip given or address is not in existance", false);
                    state = Model.this.NOT_CONNECTED;
                    Model.this.setChanged();
                    Model.this.notifyObservers();
                    return;
                }catch (IOException e2){
                    if(stop)
                    {
                        stop = true;
                        return;
                    }
                    state = Model.this.NOT_CONNECTED;
                    log.addMessage("Error Connecting to Client", false);
                    Model.this.setChanged();
                    Model.this.notifyObservers();
                    e2.printStackTrace();
                    return;
                }
                receiveInput();
            }
            
            private void receiveInput()
            {
              //opens scanner to receive input as well as setting up output mechanism
                try
                {
                    out = mysoc.getOutputStream();
                    writer = new OutputStreamWriter(out);
                    objectout = new ObjectOutputStream(out);
                    
                    Scanner scanner = new Scanner(mysoc.getInputStream());
                    ObjectInputStream objectin = new ObjectInputStream(mysoc.getInputStream());
                    while(state == Model.this.CONNECTED)
                    {
                        int obtype = objectin.readInt();
                        String s = scanner.nextLine();
                        if(obtype == Model.this.MESSAGE)
                        {
                            log.addMessage(s);
                        }
                        else if(obtype == Model.this.FILE)
                        {
                            //code, name, size
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
                            //makes folder for the downloads
                            if(!f.exists())
                                f.mkdirs();
                            //creates file
                            f = new File(f.getAbsolutePath(),both[1]);
                            f.createNewFile();
                            
                            InputStream input = new FileInputStream(f);
                        }
                        else if(obtype == Model.this.DISCONNECT)
                        {
                            System.out.println("DISCONNECTED");
                            log.addMessage(s);
                            state = Model.this.NOT_CONNECTED;
                            writer.close();
                            objectout.close();
                            out.close();
                            mysoc.close();
                        }
                        Model.this.setChanged();
                        Model.this.notifyObservers();
                    }
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
//                    e.printStackTrace();
                }
            }
        };
        th.start();
    }
    
    public void cancelConnection()
    {
        stop = true;
        state = Model.this.NOT_CONNECTED;
        Model.this.setChanged();
        Model.this.notifyObservers();
    }
    
    public void disconnectConnection()
    {
        if(state == this.CONNECTED)
        {
            log.addMessage(username + " disconnected. Session ended.", false);
            try
            {
                objectout.writeInt(this.DISCONNECT);
                objectout.flush();
                writer.write(log.getLastMessage() + "\n");
                writer.flush();
                writer.close();
                objectout.close();
                out.close();
                mysoc.close();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }        
            state = Model.this.NOT_CONNECTED;
            Model.this.setChanged();
            Model.this.notifyObservers();
        }
    }
    
    public String getLog()
    {
        return log.toString();
    }
    
//    public static void main(String [] args)
//    {
//        System.out.println("NO");
//        new Model();
//        System.out.println("YES");
//    }
}

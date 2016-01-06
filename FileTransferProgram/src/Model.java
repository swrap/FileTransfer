import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import javax.swing.filechooser.FileSystemView;

public class Model extends Observable
{
    public static final int WAITING_FOR_CONNECTION = 0, CONNECTED = 1, NOT_CONNECTED = 2;
    private static final int DISCONNECT = 0, MESSAGE = 1, FILE = 2;
    public static int BUFFER = 4096;
    
//    private SSLSocket ssl;
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
    
    public String getUsername()
    {
        return username;
    }
    
    public void setTheirPort(String theirport)
    {
        //need to throw exception
        this.theirport = Integer.parseInt(theirport);
    }
    
    public void sendMessage(String message)
    {
        if(message.length() > 0 && state == Model.CONNECTED)
        {
            try
            {
                objectout.writeInt(Model.MESSAGE);
                objectout.flush();
                log.addMessage(message, true);
                writer.write(log.getLastMessage() + "\n");
                writer.flush();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            this.setChanged();
            this.notifyObservers();
        }
    }
    
    public void sendFiles(File [] files)
    {
        for(File file : files)
        {
            try{
                objectout.writeInt(Model.FILE);
                objectout.writeInt(Model.BUFFER);
                objectout.writeLong(file.length());
                objectout.writeChars(file.getName());
                objectout.writeChar('\n');
                objectout.flush();
                
                byte [] buff = new byte[BUFFER];
                int partitions = (int)(file.length()/BUFFER);
                int lastpartitionsize = (int)(file.length() % BUFFER);
                
                OutputStream output = out;
                FileInputStream fileIn = new FileInputStream(file);
                
                for(int i = 0; i < partitions; ++i)
                {
                    fileIn.read(buff, 0, BUFFER);
                    output.write(buff, 0, BUFFER);
                    output.flush();
                }
                
                buff = new byte[lastpartitionsize];
                fileIn.read(buff, 0, lastpartitionsize);
                output.write(buff, 0, lastpartitionsize);
                output.flush();
            } catch (IOException e){
                e.printStackTrace();
            }
            log.addMessage("Sent Files",false);
            Model.this.setChanged();
            Model.this.notifyObservers();
        }
    }
    
    public void connect(String ip, String soc, String username)
    {
        this.username = username;
        log.updateUsername(username);
        state = Model.WAITING_FOR_CONNECTION;
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
                        stop = false;
                        return;
                    }
                    mysoc = new Socket(theirip, theirport);
                    state = Model.CONNECTED;
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
                            stop = false;
                            return;
                        }
                        mysoc = ser.accept();
                        theirip = ser.getInetAddress().getHostAddress();
                        out = mysoc.getOutputStream();
                        writer = new OutputStreamWriter(out);
                        ser.close();
                        state = Model.CONNECTED;
                        Model.this.setChanged();
                        Model.this.notifyObservers();
                        
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        return;
                    }
                    
                }catch (UnknownHostException e3){
                    log.addMessage("Unknown host: AKA the ip given or address is not in existance", false);
                    state = Model.NOT_CONNECTED;
                    Model.this.setChanged();
                    Model.this.notifyObservers();
                    if(stop)
                    {
                        stop = false;
                        return;
                    }
                }catch (IOException e2){
                    state = Model.NOT_CONNECTED;
                    log.addMessage("Error Connecting to Client", false);
                    Model.this.setChanged();
                    Model.this.notifyObservers();
                    e2.printStackTrace();
                    if(stop)
                    {
                        stop = false;
                        return;
                    }
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
                    while(state == Model.CONNECTED)
                    {
                        int obtype = objectin.readInt();
                        if(obtype == Model.MESSAGE)
                        {
                            String s = scanner.nextLine();
                            log.addMessage(s);
                        }
                        else if(obtype == Model.FILE)
                        {
                            System.out.println("HERE");
                            int buffersize = objectin.readInt();
                            long filesize = objectin.readLong();
                            
                            char tempS =objectin.readChar();
                            String name = "";
                            while(tempS != '\n')
                            {
                                name += tempS;
                                tempS = objectin.readChar();
                            }
                            
                            //makes new folder on desktop for downloads
                            //later set default values
                            File f = new File(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\Downloads");
                            //makes folder for the downloads
                            if(!f.exists())
                                f.mkdirs();
                            //creates file
                            f = new File(f.getAbsolutePath(),name);
                            f.createNewFile();
                            
                            byte [] buff = new byte[buffersize];
                            FileOutputStream fileOut = new FileOutputStream(f, true);
                            InputStream input = mysoc.getInputStream();
//                            ByteArrayInputStream byteIn = new ByteArrayInputStream(buff);
                            
                            int partitions = (int)(filesize/buffersize);
                            int lastpartitionsize = (int)(filesize % buffersize);
                            System.out.println("STARTED");
                            for(int i = 0; i < partitions; ++i)
                            {
                                input.read(buff, 0, buffersize);
                                fileOut.write(buff, 0, buffersize);
                                fileOut.flush();
                            }
                            
                            buff = new byte[lastpartitionsize];
                            input.read(buff, 0, lastpartitionsize);
                            fileOut.write(buff, 0, lastpartitionsize);
                            fileOut.flush();
                            System.out.println("DONE WRITING");
                            fileOut.close();
                            log.addMessage("Receiving File", false);
                        }
                        else if(obtype == Model.DISCONNECT)
                        {
                            String s = scanner.nextLine();
                            log.addMessage(s);
                            state = Model.NOT_CONNECTED;
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
//                    e.printStackTrace();
                }
            }
        };
        th.start();
    }
    
    public void cancelConnection()
    {
        stop = true;
        state = Model.NOT_CONNECTED;
        Model.this.setChanged();
        Model.this.notifyObservers();
    }
    
    public void disconnectConnection()
    {
        if(state == Model.CONNECTED)
        {
            log.addMessage(username + " disconnected. Session ended.", false);
            try
            {
                objectout.writeInt(Model.DISCONNECT);
                objectout.flush();
                writer.write(log.getLastMessage() + "\n");
                writer.flush();
                writer.close();
                objectout.close();
                out.close();
                mysoc.close();
            } catch (IOException e)
            {
//                e.printStackTrace();
            }        
            state = Model.NOT_CONNECTED;
            Model.this.setChanged();
            Model.this.notifyObservers();
        }
    }
    
    public String getLog()
    {
        return log.toString();
    }

    public int getBuffer() 
    {
        return Model.BUFFER;
    }

    public void setBuffer(int buffer)
    {
        this.BUFFER = buffer;
    }
}

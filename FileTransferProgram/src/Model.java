import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.Observable;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

public class Model extends Observable
{
    public static final int WAITING_FOR_CONNECTION = 0, CONNECTED = 1, NOT_CONNECTED = 2;
    public static final int DISCONNECT = 0, MESSAGE = 1, FILE = 2;
    private int BUFFER = 4096;
    
    private Log log;
    private String username = "user";
    private int state = NOT_CONNECTED;
    private int myport = 5000, theirport = 5001;
    private String theirip;
    private boolean acceptCon = true;
    private ClientSocketConnection csc;
    
    public Model()
    {
        this.myport = 5000;
        this.theirport = 5001;
        log = new Log(username);
        csc = new ClientSocketConnection(this);
    }
    
    public Model(int myport, int theirport)
    {
        this.myport = myport;
        this.theirport = theirport;
        log = new Log(username);
        csc = new ClientSocketConnection(this);
    }
    
    public int getMyPort()
    {
        return myport;
    }
    
    public void setMyPort(String myport)
    {
        this.myport = Integer.parseInt(myport);
    }
    
    public void setMyPort(int port)
    {
        this.myport = port;
    }
    
    public void setConnected(int state)
    {
        this.state = state;
        this.setChanged();
        this.notifyObservers();
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
        this.theirport = Integer.parseInt(theirport);
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
        log.updateUsername(username);
    }
    
    public boolean acceptingConnection()
    {
        return acceptCon;
    }
    
    public int getBuffer()
    {
        return this.BUFFER;
    }
    
    public void setBuffer(int buffer)
    {
        this.BUFFER = buffer;
    }
    
    public void addedUser(String s)
    {
        this.log.addMessage("Connected to user: " + s, false);
        this.state = this.CONNECTED;
        this.setChanged();
        this.notifyObservers();
    }
    
    public void sendMessage(String message)
    {
        if(message.length() > 0 && state == Model.CONNECTED)
        {
            log.addMessage(message, true);
            csc.sendMessage(log.getLastMessage());
            this.setChanged();
            this.notifyObservers();
        }
    }
    
    public void sendFiles(File [] files)
    {
        for(File file : files)
        {
            csc.sendFile(file);
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
        theirip = ip;
        theirport = Integer.parseInt(soc);
        this.setChanged();
        this.notifyObservers();
        
        if(csc.connect(ip, soc))
        {
            state = Model.CONNECTED;
            log.addMessage("Connecting to ip: " + theirip, false);
            this.setChanged();
            this.notifyObservers();
        }
        else
        {
            state = Model.NOT_CONNECTED;
            log.addMessage("Failed connect to user with ip: " + theirip, false);
            this.setChanged();
            this.notifyObservers();
        }
    }
    
    public void disconnectConnection(boolean completely)
    {
        log.addMessage(username + " disconnected. Session ended.", false);
        csc.killConnections(log.getLastMessage());
        if(completely)
        {
            csc.killClientSocket();
            acceptCon = false;
        }
        state = Model.NOT_CONNECTED;
        Model.this.setChanged();
        Model.this.notifyObservers();
    }
    
    public String getLog()
    {
        return log.toString();
    }
    
    public void input(ObjectInputStream objectin, Scanner scanner)
    {
        while(true)
        {
            //opens scanner to receive input as well as setting up output mechanism
            try
            {
                int obtype = objectin.readInt();
                System.out.println("HERE: " + obtype);
                if(obtype == Model.MESSAGE)
                {
                    String s = scanner.nextLine();
                    log.addMessage(s);
                }
                else if(obtype == Model.FILE)
                {
                    int buffersize = objectin.readInt();
                    long filesize = objectin.readLong();
                    
                    char tempS = objectin.readChar();
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
                    
                    int partitions = (int)(filesize/buffersize);
                    int lastpartitionsize = (int)(filesize % buffersize);
                    System.out.println("STARTED");
                    for(int i = 0; i < partitions; ++i)
                    {
//                        in.read(buff, 0, buffersize);
                        fileOut.write(buff, 0, buffersize);
                        fileOut.flush();
                    }
                    
                    buff = new byte[lastpartitionsize];
//                    in.read(buff, 0, lastpartitionsize);
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
    //                scanner.close();
                    Model.this.setChanged();
                    Model.this.notifyObservers();
                    return;
                }
    //            scanner.close();
                Model.this.setChanged();
                Model.this.notifyObservers();
            } catch (StreamCorruptedException e1) {
                System.err.println("Model.java Closing problems");
                e1.printStackTrace();
            } catch (IOException e2)
            {
                System.err.println("Model.java Problem reading "
                        + "input.");
            }
        }
    }
}

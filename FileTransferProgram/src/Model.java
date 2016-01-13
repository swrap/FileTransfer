import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

public class Model extends Observable
{
    public static final int WAITING_FOR_CONNECTION = 0, CONNECTED = 1, NOT_CONNECTED = 2;
    public static final int DISCONNECT = 0, MESSAGE = 1, FILE = 2, SOCKETS = 3;
    private int BUFFER = 4096;
    
    private Log log;
    private String username = "user";
    private String password = "";
    private int state = NOT_CONNECTED;
    private int myport = 4000;
    private boolean acceptCon = false;
    private ClientSocketConnection csc;
    
    public Model(String username, String password)
    {
        this.username = username;
        this.password = password;
        log = new Log(username);
        csc = new ClientSocketConnection(this);
        this.setChanged();
        this.notifyObservers();
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
    
    public boolean isConnected()
    {
        return (state == Model.CONNECTED);
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
    
    public void setAcceptingConnection(boolean acceptCon)
    {
        this.acceptCon = acceptCon;
    }
    
    public boolean acceptingConnection()
    {
        return acceptCon;
    }
    
    public int getState()
    {
        return state;
    }
    
    public int getBuffer()
    {
        return this.BUFFER;
    }
    
    public void setBuffer(int buffer)
    {
        this.BUFFER = buffer;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setState(int state)
    {
        this.state = state;
        Model.this.setChanged();
        Model.this.notifyObservers();
    }
    
    public void unsuccessfulConnection(String username, SocketAddress socketAddress)
    {
        log.addMessage("Failed connection with user: " + username + " Socket Address: " + socketAddress.toString(), false);
        Model.this.setChanged();
        Model.this.notifyObservers();
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
    
    public void addedUser(String s)
    {
        state = Model.CONNECTED;
        log.addMessage("Connected to user: " + s, false);
        this.setChanged();
        this.notifyObservers();
    }
    
    public boolean connect(String ip, int port)
    {
        try {
            if((ip.equals(InetAddress.getLocalHost().getHostAddress()) ||
                    ip.equals("localhost")) && port == myport || ip.equals(""))
            {
                state = Model.NOT_CONNECTED;
                this.setChanged();
                this.notifyObservers();
                return false;
            }
        } catch (UnknownHostException e) {
            System.err.println("Model.java Error finding local IP");
        }
        
        String s = null;
        if((s = csc.connect(ip, port)) != null)
        {
            state = Model.CONNECTED;
            log.addMessage("Connected to user: " + s, false);
            this.setChanged();
            this.notifyObservers();
            return true;
        }
        else
        {
            log.addMessage("Failed connect to user with ip: " + ip + " on port: " + port, false);
            this.setChanged();
            this.notifyObservers();
            return false;
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
        Model.this.setChanged();
        Model.this.notifyObservers();
    }
    
    public String getLog()
    {
        return log.toString();
    }
    
    public void doubleSession(String username, SocketAddress socketAddress)
    {
        log.addMessage("User: " + username + " attempted to connect, but was"
                + "in a session already. Address of user: " + socketAddress.toString());
        Model.this.setChanged();
        Model.this.notifyObservers();
    }
    
    public void input(InputStream in, ObjectInputStream objectin, Scanner scanner)
    {
        while(true)
        {
            //opens scanner to receive input as well as setting up output mechanism
            try
            {
                int obtype = objectin.readInt();

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
                    for(int i = 0; i < partitions; ++i)
                    {
                        in.read(buff, 0, buffersize);
                        fileOut.write(buff, 0, buffersize);
                        fileOut.flush();
                    }
                    
                    buff = new byte[lastpartitionsize];
                    in.read(buff, 0, lastpartitionsize);
                    fileOut.write(buff, 0, lastpartitionsize);
                    fileOut.flush();
                    fileOut.close();
                    log.addMessage("Receiving File", false);
                }
                else if(obtype == Model.DISCONNECT)
                {
                    String s = scanner.nextLine();
                    log.addMessage(s);
                    Model.this.setChanged();
                    Model.this.notifyObservers();
                    return;
                }
                else if(obtype == Model.SOCKETS)
                {
                    int amount = objectin.readInt();
                    SocketAddress [] socAdd = new SocketAddress[amount];
                    for(int i = 0; i < amount; ++i)
                    {
                        try {
                            socAdd[i] = (SocketAddress)(objectin.readObject());
                        } catch (ClassNotFoundException e) {
                            log.addMessage("Unable to connect to one of the "
                                    + "users. Disconnecting.", false);
                            csc.killConnections("User: " + username + "Could not "
                                    + "connect to all users. Disconnecting.");
                        }
                    }
                    csc.connect(socAdd);
                }
                Model.this.setChanged();
                Model.this.notifyObservers();
            } catch (SocketException e0) {
                if(!e0.getMessage().equals("Socket closed"))
                {
                    e0.printStackTrace();
                }
                //nothing is wrong the socket is closed
                break;
            } catch (EOFException e1) {
                //nothing is wrong the socket is closed
                break;
            } catch (StreamCorruptedException e2) {
                System.err.println("Model.java Closing problems");
            } catch (IOException e3)
            {
                System.err.println("Model.java Problem reading "
                        + "input.");
            }
        }
    }
}

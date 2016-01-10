<<<<<<< HEAD
import java.io.EOFException;
import java.io.File;
=======
import java.io.File;
import java.io.FileInputStream;
>>>>>>> refs/remotes/origin/master
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

public class Model extends Observable
{
    public static final int WAITING_FOR_CONNECTION = 0, CONNECTED = 1, NOT_CONNECTED = 2;
    public static final int DISCONNECT = 0, MESSAGE = 1, FILE = 2;
    private int BUFFER = 4096;
    
<<<<<<< HEAD
=======
//    private SSLSocket ssl;
>>>>>>> refs/remotes/origin/master
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
    
    public int isConnected()
    {
        return state;
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
    
    public int getBuffer()
    {
        return this.BUFFER;
    }
    
    public void setBuffer(int buffer)
    {
        this.BUFFER = buffer;
    }
    
    public int getState()
    {
        return state;
    }
    
    public void setState(int state)
    {
        this.state = state;
        Model.this.setChanged();
        Model.this.notifyObservers();
    }
    
    public void addedUser(String s)
    {
        this.log.addMessage("Connected to user: " + s, false);
        this.state = Model.CONNECTED;
        this.setChanged();
        this.notifyObservers();
    }
    
    public void sendMessage(String message)
    {
        if(message.length() > 0 && state == Model.CONNECTED)
        {
<<<<<<< HEAD
            log.addMessage(message, true);
            csc.sendMessage(log.getLastMessage());
=======
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
>>>>>>> refs/remotes/origin/master
            this.setChanged();
            this.notifyObservers();
        }
    }
    
    public void sendFiles(File [] files)
    {
        for(File file : files)
        {
<<<<<<< HEAD
            csc.sendFile(file);
=======
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
>>>>>>> refs/remotes/origin/master
            log.addMessage("Sent Files",false);
            Model.this.setChanged();
            Model.this.notifyObservers();
        }
    }
    
    public boolean connect(String ip, int port)
    {
<<<<<<< HEAD
=======
        this.username = username;
        log.updateUsername(username);
>>>>>>> refs/remotes/origin/master
        state = Model.WAITING_FOR_CONNECTION;
        this.setChanged();
        this.notifyObservers();
        
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
        
        if(csc.connect(ip, port))
        {
            state = Model.CONNECTED;
            this.setChanged();
            this.notifyObservers();
            return true;
        }
        else
        {
            state = Model.NOT_CONNECTED;
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
        state = Model.NOT_CONNECTED;
        Model.this.setChanged();
        Model.this.notifyObservers();
    }
    
    public String getLog()
    {
        return log.toString();
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
<<<<<<< HEAD
                    String s = scanner.nextLine();
                    log.addMessage(s);
                }
                else if(obtype == Model.FILE)
                {
                    int buffersize = objectin.readInt();
                    long filesize = objectin.readLong();
=======
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
>>>>>>> refs/remotes/origin/master
                    
                    char tempS = objectin.readChar();
                    String name = "";
                    while(tempS != '\n')
                    {
<<<<<<< HEAD
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
=======
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
>>>>>>> refs/remotes/origin/master
                    {
                        in.read(buff, 0, buffersize);
                        fileOut.write(buff, 0, buffersize);
                        fileOut.flush();
                    }
<<<<<<< HEAD
                    
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
=======
                }catch (IOException e2){
                    state = Model.NOT_CONNECTED;
                    log.addMessage("Error Connecting to Client", false);
>>>>>>> refs/remotes/origin/master
                    Model.this.setChanged();
                    Model.this.notifyObservers();
                    return;
                }
                Model.this.setChanged();
                Model.this.notifyObservers();
            } catch (SocketException e0) {
                if(!e0.getMessage().equals("Socket closed"))
                {
<<<<<<< HEAD
                    e0.printStackTrace();
                }
                //nothing is wrong the socket is closed
                break;
            } catch (EOFException e1) {
                //nothing is wrong the socket is closed
                break;
            } catch (StreamCorruptedException e2) {
                System.err.println("Model.java Closing problems");
                e2.printStackTrace();
            } catch (IOException e3)
            {
                System.err.println("Model.java Problem reading "
                        + "input.");
                e3.printStackTrace();
                System.exit(0);
            }
        }
    }
=======
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
>>>>>>> refs/remotes/origin/master
}

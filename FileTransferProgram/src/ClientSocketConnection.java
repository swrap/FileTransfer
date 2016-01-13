import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientSocketConnection {
    private ArrayList<SocketThread> sockets = new ArrayList<SocketThread>();
    private Model model;
    private boolean isAlive = true;
    private ServerThread serverThread;
    
    public ClientSocketConnection(Model model) {
        this.model = model;
        this.serverThread = new ServerThread();
        serverThread.start();
    }

    public boolean isStillRunning() {
        return isAlive;
    }

    public void killClientSocket() {
        isAlive = false;
        serverThread.kill();
    }
    
    public synchronized void killConnections(String message) {
        for (SocketThread s : sockets) {
            s.kill(message);
        }
        sockets.clear();
    }

    public synchronized void removeSoc(SocketThread soc)
    {
        sockets.remove(soc);
    }
    
    private void connect(Socket soc) {
        SocketThread socth = new SocketThread(soc);
        socth.start();
        if(socth.connectionSuccessful())
        {
            sockets.add(socth);
            model.addedUser(socth.getUsername());
        }
        else
        {
            model.unsuccessfulConnection(socth.getUsername(), socth.getSerSocAddress());
        }
    }
    
    public void connect(SocketAddress [] socAdd) {
        try {
            for(SocketAddress tempS : socAdd)
            {
                Socket soc = new Socket();
                soc.connect(tempS);
                SocketThread socth = new SocketThread(soc, true);
                socth.start();
                if(socth.connectionSuccessful())
                {
                    sockets.add(socth);
                    model.addedUser(socth.getUsername());
                }
            }
        } catch (IOException e) {
            System.err.println("ClientSocketConnection.java Error with "
                    + "adding other clients.");
            e.printStackTrace();
        }
    }
    
    public String connect(String ip, int port) {
        try {
            Socket soc = new Socket(ip, port);
            SocketThread socth = new SocketThread(soc);
            socth.start();
            if(socth.connectionSuccessful())
            {
                sockets.add(socth);
                return socth.getUsername();
            }
        } catch (IOException e) {
            System.err.println("ClientSocketConnection.java Error with "
                    + "creating socket returning false.");
            return null;
        }
        return null;
    }

    public void sendMessage(String message) {
        try {
            for (SocketThread s : sockets) {
                OutputStream out = s.getOutputStream();
                ObjectOutputStream objectout = s.getObjectOutputStream();
                OutputStreamWriter writer = s.getOutputStreamWriter();
                
                objectout.writeInt(Model.MESSAGE);
                objectout.flush();
                writer.write(message + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File file) {
        for (SocketThread s : sockets) {
            try {
                OutputStream out = s.getOutputStream();
                ObjectOutputStream objectout = s.getObjectOutputStream();
                
                int buffer = model.getBuffer();
                objectout.writeInt(Model.FILE);
                objectout.writeInt(buffer);
                objectout.writeLong(file.length());
                objectout.writeChars(file.getName());
                objectout.writeChar('\n');
                objectout.flush();

                byte[] buff = new byte[buffer];
                int partitions = (int) (file.length() / buffer);
                int lastpartitionsize = (int) (file.length() % buffer);

                FileInputStream fileIn = new FileInputStream(file);

                for (int i = 0; i < partitions; ++i) {
                    fileIn.read(buff, 0, buffer);
                    out.write(buff, 0, buffer);
                    out.flush();
                }

                buff = new byte[lastpartitionsize];
                fileIn.read(buff, 0, lastpartitionsize);
                out.write(buff, 0, lastpartitionsize);
                out.flush();
                fileIn.close();
            } catch (IOException e) {
                System.err
                        .println("ClientSocketConnection.java Error sending"
                                + " file.");
            }
        }
    }

    private class SocketThread extends Thread implements Serializable{
        private boolean successConnect = false, not_verified = true;
        private Socket soc;
        private String username = "";
        private ObjectOutputStream oos;
        private OutputStreamWriter write;
        private SocketAddress serSocAdd;
        private boolean multiple = false;

        public SocketThread(Socket soc) {
            super();
            this.soc = soc;
        }
        
        public SocketThread(Socket soc, boolean multiple) {
            super();
            this.soc = soc;
            this.multiple = multiple;
        }
        
        public boolean connectionSuccessful() {
            while(not_verified)
            {
                this.yield();
            }
            return successConnect;
        }
        
        public Socket getSocket()
        {
            return soc;
        }
        
        public SocketAddress getTheirAddress()
        {
            return soc.getRemoteSocketAddress();
        }

        public OutputStreamWriter getOutputStreamWriter() {
            return write;
        }

        public ObjectOutputStream getObjectOutputStream() {
            return oos;
        }

        public void kill() {
            try {
                oos.close();
                write.close();
                soc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public SocketAddress getSocketRemoteAddress()
        {
            return soc.getRemoteSocketAddress();
        }

        public void kill(String message) {
            try {
                oos.writeInt(Model.DISCONNECT);
                oos.flush();
                write.write(message + "\n");
                write.flush();
                oos.close();
                write.close();
                soc.close();
            } catch (IOException e) {
                // closing the socket will throw an exception
                e.printStackTrace();
            }
        }

        public String getUsername() {
            return username;
        }
        
        public OutputStream getOutputStream()
        {
            try {
                return soc.getOutputStream();
            } catch (IOException e) {
                //could not get outputstream
                e.printStackTrace();
            }
            return null;
        }
        
        public SocketAddress getSerSocAddress()
        {
            return serSocAdd;
        }

        public void run() {
            try {
                write = new OutputStreamWriter(soc.getOutputStream());

                Scanner scan = new Scanner(soc.getInputStream());
                //writes username
                write.write(model.getUsername()+ '\n');
                write.flush();
                
                if (scan.hasNextLine()) {
                    this.username = scan.nextLine();
                }
                
                //write serverthread socket address
                oos = new ObjectOutputStream(soc.getOutputStream());
                oos.writeObject(serverThread.getSocketAddress());
                oos.flush();
                
                //read serverthread socket address
                ObjectInputStream ins = new ObjectInputStream(soc.getInputStream());
                serSocAdd = (SocketAddress)(ins.readObject());
                
                //write connected
                oos.writeBoolean(model.isConnected());
                oos.flush();
                
                //read connected
                boolean temp = ins.readBoolean();
                
                //write multiple
                oos.writeBoolean(multiple);
                oos.flush();
                
                //read multiple
                boolean tempM = ins.readBoolean();
                                
                if(tempM || multiple || !temp || !model.isConnected())
                {
                    if(!temp)
                    {
                        oos.writeInt(Model.SOCKETS);
                        oos.writeInt(sockets.size());
                        oos.flush();
                        for(SocketThread s : sockets)
                        {
                            if(s != this)
                            {
                                oos.writeObject(s.getSerSocAddress());
                                oos.flush();
                            }
                        }
                    }
                    
                    this.not_verified = false;
                    this.successConnect = true;
                    model.input(soc.getInputStream(),ins, scan);
                }
                else
                {
                    model.doubleSession(this.getUsername(), this.soc.getRemoteSocketAddress());
                    this.not_verified = false;
                    this.successConnect = false;
                }
                soc.close();
            } catch (IOException | ClassNotFoundException e) {
                System.err
                        .println("ClientSocketConnection.java Error with "
                                + "socket or inputstream.");
                e.printStackTrace();

                this.not_verified = false;
                this.successConnect = false;
                
                model.disconnectConnection(false);
            }

            ClientSocketConnection.this.removeSoc(this);
            if(sockets.size() == 0)
            {
                model.setState(Model.NOT_CONNECTED);
            }
        }
    }

    private class ServerThread extends Thread {
        private ServerSocket ser = null;

        public ServerThread() {
            super();
        }

        public void kill() {
            if (ser != null) {
                try {
                    ser.close();
                } catch (IOException e) {
                    System.err.println("Trouble shutting down server");
                }
            }
        }
        
        public SocketAddress getSocketAddress()
        {
            return ser.getLocalSocketAddress();
        }

        public void run() {
            while (true) {
                try {
                    ser = new ServerSocket(model.getMyPort());
                    while (isAlive) {
                        Socket tempS = ser.accept();
                        if (model.acceptingConnection())
                        {
                            ClientSocketConnection.this.connect(tempS);
                        } else {
                            tempS.close();
                        }
                    }
                    ser.close();
                    break;
                } catch (BindException be) {
                    if (be.getMessage().contains("Address already in use")) {
                        System.err
                                .println("ClientSocketConnection.java "
                                        + "User Server Port: "
                                        + model.getMyPort());
                        model.setMyPort(model.getMyPort() + 1);
                    }
                } catch (IOException e) {
                    if (!e.getMessage().equals("socket closed")) {
                        System.err.println("ClientSocketConnection.java "
                                + "Problem with serverthread.");
                    }
                    break;
                }
            }
        }
    }
}

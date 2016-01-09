import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
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

    public void killConnections(String message) {
        for (SocketThread s : sockets) {
            s.kill(message);
        }
        sockets.clear();
    }

    public void removeSoc(SocketThread soc)
    {
        sockets.remove(soc);
    }
    
    public boolean connect(String ip, String port) {
        try {
            Socket soc = new Socket(ip, Integer.parseInt(port));
            SocketThread socth = new SocketThread(soc);
            socth.start();
            sockets.add(socth);
            model.setConnected(Model.CONNECTED);
            return true;
        } catch (IOException e) {
            System.err.println("ClientSocketConnection.java Error with "
                    + "creating socket.");
            return false;
        }
    }

    public void sendMessage(String message) {
        try {
            for (SocketThread s : sockets) {
                OutputStream out = s.getOutputStream();
                ObjectOutputStream objectout = s.getObjectOutputStream();
                OutputStreamWriter writer = s.getOutputStreamWriter();
                
                System.out.println("MESSAGE: " + message);
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

    private class SocketThread extends Thread {
        private Socket soc;
        private String username = "";
        private ObjectOutputStream oos;
        private OutputStreamWriter write;

        public SocketThread(Socket soc) {
            super();
            this.soc = soc;
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

        public void run() {
            try {                
                write = new OutputStreamWriter(soc.getOutputStream());
                
                write.write(model.getUsername()+ '\n');
                write.flush();
                Scanner scan = new Scanner(soc.getInputStream());
                if (scan.hasNextLine()) {
                    String user = scan.nextLine();
                    model.addedUser(user);
                    this.username = user;
                }
                oos = new ObjectOutputStream(soc.getOutputStream());

                ObjectInputStream ins = new ObjectInputStream(soc.getInputStream());
                
                model.input(soc.getInputStream(),ins, scan);
                soc.close();
                ClientSocketConnection.this.removeSoc(this);
            } catch (IOException e) {
                System.err
                        .println("ClientSocketConnection.java Error with "
                                + "socket or inputstream.");
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

        public void run() {
            while (true) {
                try {
                    ser = new ServerSocket(model.getMyPort());
                    while (isAlive) {
                        Socket tempS = ser.accept();
                        if (model.acceptingConnection()) {
                            SocketThread socth = new SocketThread(tempS);
                            socth.start();
                            sockets.add(socth);
                            model.setConnected(Model.CONNECTED);
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

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

    public boolean connect(String ip, String port) {
        System.out.println("PROTASDF: " + port);
        try {
            Socket soc = new Socket(ip, Integer.parseInt(port));
//            if (initialHandshake(soc)) {
            SocketThread socth = new SocketThread(soc);
            socth.start();
            sockets.add(socth);
            model.setConnected(Model.CONNECTED);
//            } else {
//                soc.close();
//            }
            return true;
        } catch (IOException e) {
            System.err.println("ClientSocketConnection.java Error with "
                    + "creating socket.");
            return false;
        }
    }

//    private boolean initialHandshake(Socket soc) {
//        String user = model.getUsername();
//
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(soc.getOutputStream());
//            oos.writeChars(user);
//            oos.writeChar('\n');
//            oos.flush();
//            return true;
//        } catch (IOException e) {
//            System.err.println("ClientSocketConnection.java Error with"
//                    + " initial handshake.");
//        }
//        return false;
//    }

    public void sendMessage(String message) {
//        try {
//            for (SocketThread s : sockets) {
//                System.out.println("MESSAGE: " + message);
////                s.objectout.writeInt(Model.MESSAGE);
////                s.objectout.flush();
////                s.writer.write(message + "\n");
////                s.writer.flush();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void sendFile(File file) {
        for (SocketThread s : sockets) {
            try {
                int buffer = model.getBuffer();
//                s.objectout.writeInt(Model.FILE);
//                s.objectout.writeInt(buffer);
//                s.objectout.writeLong(file.length());
//                s.objectout.writeChars(file.getName());
//                s.objectout.writeChar('\n');
//                s.objectout.flush();

                byte[] buff = new byte[buffer];
                int partitions = (int) (file.length() / buffer);
                int lastpartitionsize = (int) (file.length() % buffer);

                FileInputStream fileIn = new FileInputStream(file);

//                for (int i = 0; i < partitions; ++i) {
//                    fileIn.read(buff, 0, buffer);
//                    s.out.write(buff, 0, buffer);
//                    s.out.flush();
//                }
//
//                buff = new byte[lastpartitionsize];
//                fileIn.read(buff, 0, lastpartitionsize);
//                s.out.write(buff, 0, lastpartitionsize);
//                s.out.flush();
//                fileIn.close();
            } catch (IOException e) {
                System.err
                        .println("ClientSocketConnection.java Error sending"
                                + " file.");
            }
        }
    }

    private class SocketThread extends Thread {
        private boolean running = true;
        private Socket soc;
        private String username = "";

        public SocketThread(Socket soc) {
            super();
            this.soc = soc;
        }

        public void kill() {
            running = false;

            try {
                soc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void kill(String message) {
            running = false;

            try {
                ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
                out.writeInt(Model.DISCONNECT);
                out.flush();
                out.writeChars(message);
                out.writeChar('\n');
                out.flush();
                out.close();
                soc.close();
            } catch (IOException e) {
                // closing the socket will throw an exception
            }
        }

        public String getUsername() {
            return username;
        }

        public void run() {
            try {                
                OutputStreamWriter write = new OutputStreamWriter(soc.getOutputStream());
                
                write.write(model.getUsername()+ '\n');
                write.flush();
                Scanner scan = new Scanner(soc.getInputStream());
                if (scan.hasNextLine()) {
                    String user = scan.nextLine();
                    model.addedUser(user);
                    this.username = user;
                }

                ObjectInputStream ins = new ObjectInputStream(soc.getInputStream());
                
                model.input(ins, scan);
                scan.close();
                ins.close();
                soc.close();
            } catch (IOException e) {
                System.err
                        .println("ClientSocketConnection.java Error with "
                                + "socket or inputstream.");
//                e.printStackTrace();
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
            System.out.println("ENDED");
        }
    }
}

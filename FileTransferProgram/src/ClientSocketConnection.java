import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientSocketConnection
{
    private ArrayList<Socket> sockets = new ArrayList<Socket>();
    private ServerSocket ser;
    private Model model;
    private boolean isAlive = false;
    
    public ClientSocketConnection(Model model)
    {
        this.model = model;
    }
    
    public boolean isStillRunning()
    {
        return isAlive;
    }
    
    private class ServerThread extends Thread
    {
        public ServerThread()
        {
            super();
        }
        
        public void run()
        {
            ServerSocket ser;
            try {
                ser = new ServerSocket();

                while(isAlive)
                {
                    Socket tempS = ser.accept();
                    if(model.acceptingConnection())
                    {
                        sockets.add(tempS);
                    }
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }
}

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Log
{
    //may change when reading in a log
    private ArrayList<String> list = new ArrayList<String>();
    private String username = "";
    
    public Log(String username)
    {
        this.username = username;
    }

    public Log(String username, ArrayList<String> list)
    {
        this.username = username;
        this.list = list;
    }
    
    public ArrayList<String> getList()
    {
        return list;
    }
    
    public String toString()
    {
        String total = "";
        
        for(int i = 0; i < list.size(); ++i)
        {
            total += list.get(i);
            if(i < list.size()-1)
                total += "\n";
        }
        
        return total;
    }
    
    public void addMessage(String s, boolean b)
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        if(b)
        {
            list.add(dateFormat.format(date) + " " + username + ": " + s);
        }
        else
        {
            list.add(dateFormat.format(date) + " SYSTEM: " + s);
        }
    }
    
    public void addMessage(String s)
    {
        list.add(s);
    }
    
    public String getLastMessage()
    {
        return list.get(list.size()-1);
    }
}

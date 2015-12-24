import java.io.File;
import java.util.regex.Matcher;

import javax.swing.filechooser.FileSystemView;


public class Test
{
    public static void main(String [] args)
    {
        String s = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        File f = new File(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\Downloads");
        if(!f.exists())
            f.mkdirs();
        System.out.println();
    }
}

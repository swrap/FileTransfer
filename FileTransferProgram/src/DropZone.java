import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

public class DropZone extends JPanel
{
    private DropTarget target;
    private ZoneListener zone;
    private ArrayList<DropObject> drops = new ArrayList<DropObject>();
    
    public DropZone()
    {
        super();
        this.zone = new ZoneListener();
        this.target = new DropTarget(this,zone);
    }
    
    public void paintComponent(Graphics g)
    {
        Dimension dim = this.getSize();
        String s = "Drop Here";
        g.drawString(s, (int)(dim.getWidth()/2),(int)(dim.getHeight()/2));
        for(DropObject d : drops)
        {
            System.out.println("HERE: " + (int)d.getX() + " " + (int)(d.getY()));
            g.drawImage(d.getImage(), (int)(d.getX()), (int)(d.getY()), null);
        }
    }
    
    private class DropObject
    {
        private Image img;
        private String name = "";
        private Point loc;
        
        public DropObject(File file, Point loc)
        {
            Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
            this.img = ((ImageIcon)icon).getImage();
            this.name = file.getName();
            this.loc = loc;
        }
        
        public Image getImage()
        {
            return img;
        }
        
        public String name()
        {
            return name;
        }
        
        public void setX(int x, int y)
        {
            loc = new Point(x,y);
        }
        
        public double getX()
        {
            return loc.getX();
        }
        
        public double getY()
        {
            return loc.getY();
        }
    }
    
    private class ZoneListener implements DropTargetListener
    {
        
        public ZoneListener()
        {
            super();
        }

        @Override
        public void dragEnter(DropTargetDragEvent arg0)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void dragExit(DropTargetEvent e)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void dragOver(DropTargetDragEvent e)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void drop(DropTargetDropEvent e)
        {
            e.acceptDrop(DnDConstants.ACTION_COPY);
            
            Transferable trans = e.getTransferable();
            
            DataFlavor [] flvs = trans.getTransferDataFlavors();

            try {
                int count = 0;
                for(DataFlavor f : flvs)
                {
                    if(f.isFlavorJavaFileListType())
                    {
                        List<File> files = (List<File>) trans.getTransferData(f);
                        for(File file : files)
                        {
                            drops.add(new DropObject(file, e.getLocation()));
                        }
                    }
                }
            } catch (UnsupportedFlavorException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            e.dropComplete(true);
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent e)
        {
            // TODO Auto-generated method stub
            
        }
    }
}

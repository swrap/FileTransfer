import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

public class DropZone extends JPanel
{
    public static double SIZE_MULTIPLIER = 1.5;
    private DropTarget target;
    private ZoneListener zone;
    private ArrayList<DropObject> drops = new ArrayList<DropObject>();
    private ConnectionFrame cf;
    
    public DropZone(ConnectionFrame connectionFrame)
    {
        super();
        this.zone = new ZoneListener();
        this.target = new DropTarget(this,zone);
        this.setLayout(null);
        this.cf = connectionFrame;
    }
    
    public void deleteFiles(boolean selected)
    {
        if(selected)
        {
            for(int i = 0; i < drops.size(); ++i)
            {
                if(drops.get(i).isFocused())
                {
                    this.remove(drops.get(i));
                    drops.remove(i);
                    --i;
                }
            }
        }
        else
        {
            removeAll();
            drops.removeAll(drops);
        }
        
        revalidate();
        repaint();
    }
    
    public File [] getFiles(boolean selected)
    {
        ArrayList<File> files = new ArrayList<File>();
        for(int i = 0; i < drops.size(); ++i)
        {
            if((selected && drops.get(i).isFocused()) || !selected)
                files.add(drops.get(i).getFile());
        }
        return (File[]) drops.toArray();
    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Dimension dim = this.getSize();
        String s = "Drop Here";
        int tempW = g.getFontMetrics().stringWidth(s)/2;
        g.drawString(s, (int)(dim.getWidth()/2)-tempW,(int)(dim.getHeight()/2));
    }
    
    private class DropObject extends JComponent implements MouseMotionListener, MouseListener
    {
        private Image img;
        private String name = "";
        private File file;
        private Point loc, moved, clicked;
        private boolean focus = true;
        
        public DropObject(File file, Point loc) throws IOException
        {
            super();
            Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
            this.img = ((ImageIcon)icon).getImage();
            this.name = file.getName();
            this.file = file;
            this.loc = loc;
            this.moved = new Point(0,0);
            
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
                        
            this.setMinimumSize(new Dimension(width, height));
            this.setPreferredSize(new Dimension(width, height));
            this.setSize(width, height);
            
            this.setLocation(loc);
            this.setVisible(true);

            this.addMouseMotionListener(this);
            this.addMouseListener(this);
                        
            this.setToolTipText(name);
            
            this.setBorder(BorderFactory.createLineBorder(Color.green));
        }
        
        public boolean isFocused()
        {
            return focus;
        }
        
        public File getFile()
        {
            return file;
        }
        
        public void paintComponent(Graphics g)
        {
            g.drawImage(img, -5, 0, null);
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            moved = e.getPoint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            if(e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e))
            {
                this.setBorder(BorderFactory.createLineBorder(Color.green));
                focus = true;
            }
            else if(e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e))
            {
                this.setBorder(BorderFactory.createLineBorder(Color.black));
                focus = false;
            }
            if(e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e))
            {
                int width = (int)(this.getWidth()*SIZE_MULTIPLIER);
                int height = (int)(this.getHeight()*SIZE_MULTIPLIER);
                if(width < DropZone.this.getWidth() && height < DropZone.this.getHeight())
                {
                    img = img.getScaledInstance(width, height, Image.SCALE_FAST);
                    this.setMinimumSize(new Dimension(width, height));
                    this.setPreferredSize(new Dimension(width, height));
                    this.setSize(width, height);
                }
            }
            else if(e.getClickCount() == 2 && SwingUtilities.isRightMouseButton(e))
            {
                int width = (int)(this.getWidth()/1.2);
                int height = (int)(this.getHeight()/1.2);
                if(width > 10 && height > 10)
                {
                    img = img.getScaledInstance(width, height, Image.SCALE_FAST);
                    this.setMinimumSize(new Dimension(width, height));
                    this.setPreferredSize(new Dimension(width, height));
                    this.setSize(width, height);
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mousePressed(MouseEvent e)
        {

            clicked = e.getLocationOnScreen();
            Point temp = this.getLocationOnScreen();
            clicked.x = clicked.x < temp.x ? temp.x - clicked.x : clicked.x - temp.x;
            clicked.y = clicked.y < temp.y ? temp.y - clicked.y : clicked.y - temp.y;
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            if(moved.x != 0 && moved.y != 0)
            {
                this.setLocation(loc.x + moved.x - clicked.x, loc.y + moved.y - clicked.y);
                Point p = this.getLocation();
                if(p.x < 0)
                    p.setLocation(0,p.y);
                else if(p.x + this.getWidth() > DropZone.this.getWidth())
                    p.setLocation(DropZone.this.getWidth() - this.getWidth(), p.y);
                if(p.y < 0)
                    p.setLocation(p.x, 0);
                else if(p.y + this.getHeight() > DropZone.this.getHeight())
                    p.setLocation(p.x, DropZone.this.getHeight() - this.getHeight());
                this.setLocation(p);
                loc = p;
                moved = new Point(0,0);
            }
            repaint();
            revalidate();
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
                            DropObject dr = new DropObject(file, e.getLocation());
                            drops.add(dr);
                            DropZone.this.add(dr);
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
            DropZone.this.revalidate();
            DropZone.this.repaint();
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent e)
        {
            // TODO Auto-generated method stub
            
        }
    }
}

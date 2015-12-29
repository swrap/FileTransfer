import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class DropZone extends JPanel
{
    public DropZone()
    {
        super();
    }
    
    public void paintComponent(Graphics g)
    {
        Dimension d = this.getSize();
        String s = "Drop Here";
        g.drawString(s, (int)(d.getWidth()/2),(int)(d.getHeight()/2));
    }
    
    private class ZoneListener implements DropTargetListener
    {

        @Override
        public void dragEnter(DropTargetDragEvent arg0)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void dragExit(DropTargetEvent arg0)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void dragOver(DropTargetDragEvent arg0)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void drop(DropTargetDropEvent dtde)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde)
        {
            // TODO Auto-generated method stub
            
        }
    }
}

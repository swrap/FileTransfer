import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;









//import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import java.awt.Color;

import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Action;










import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.border.LineBorder;


public class ConnectionFrame extends JFrame implements Observer, WindowListener
{
    private JTextField textField;
    private JComponent theirIp;
    private JComponent mySoc;
    private JComponent theirSoc;
    private JTextPane txtpnHello;
    private Model model;
    private JLabel lblConnected;
    private JButton btnConnect;
    private JComponent user;
    private DropZone dropZone;
    
    public ConnectionFrame(final Model model)
    {
        this.model = model;
        setBackground(Color.WHITE);
        setTitle("File Transport");
        setFont(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 105, 75, 0, 95, 0};
        gridBagLayout.rowHeights = new int[]{0, 22, 22, 22, 68, 22, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);

        this.addWindowListener(this);
        dropZone = new DropZone(this);
        
        JLabel lblUsername = new JLabel("Username:");
        GridBagConstraints gbc_lblUsername = new GridBagConstraints();
        gbc_lblUsername.anchor = GridBagConstraints.EAST;
        gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
        gbc_lblUsername.gridx = 0;
        gbc_lblUsername.gridy = 0;
        getContentPane().add(lblUsername, gbc_lblUsername);
        
        user = new JTextField();
        ((JTextField)user).setText("user");
        GridBagConstraints gbc_user = new GridBagConstraints();
        gbc_user.insets = new Insets(0, 0, 5, 5);
        gbc_user.fill = GridBagConstraints.HORIZONTAL;
        gbc_user.gridx = 1;
        gbc_user.gridy = 0;
        getContentPane().add(user, gbc_user);
        ((JTextField)user).setColumns(10);
        
        JLabel lblYourIP = new JLabel("Your IP:");
        lblYourIP.setBackground(Color.WHITE);
        lblYourIP.setVerticalAlignment(SwingConstants.BOTTOM);
        GridBagConstraints gbc_lblYourIP = new GridBagConstraints();
        gbc_lblYourIP.anchor = GridBagConstraints.EAST;
        gbc_lblYourIP.insets = new Insets(0, 0, 5, 5);
        gbc_lblYourIP.gridx = 0;
        gbc_lblYourIP.gridy = 1;
        getContentPane().add(lblYourIP, gbc_lblYourIP);
        
        JLabel lblLocalHost = new JLabel("localhost");
        GridBagConstraints gbc_lblLocalHost = new GridBagConstraints();
        gbc_lblLocalHost.insets = new Insets(0, 0, 5, 5);
        gbc_lblLocalHost.gridx = 1;
        gbc_lblLocalHost.gridy = 1;
        getContentPane().add(lblLocalHost, gbc_lblLocalHost);
        
        JLabel lblSocket = new JLabel("My Port:");
        GridBagConstraints gbc_lblSocket = new GridBagConstraints();
        gbc_lblSocket.gridwidth = 2;
        gbc_lblSocket.anchor = GridBagConstraints.EAST;
        gbc_lblSocket.insets = new Insets(0, 0, 5, 5);
        gbc_lblSocket.gridx = 2;
        gbc_lblSocket.gridy = 1;
        getContentPane().add(lblSocket, gbc_lblSocket);
        
        mySoc = new JLabel();
        ((JLabel)mySoc).setText(model.getMyPort()+"");
        GridBagConstraints gbc_mySoc = new GridBagConstraints();
        gbc_mySoc.insets = new Insets(0, 0, 5, 0);
        gbc_mySoc.gridx = 4;
        gbc_mySoc.gridy = 1;
        getContentPane().add(mySoc, gbc_mySoc);
        
        JLabel lblOtherIP = new JLabel("Other IP: ");
        GridBagConstraints gbc_lblOtherIP = new GridBagConstraints();
        gbc_lblOtherIP.anchor = GridBagConstraints.EAST;
        gbc_lblOtherIP.insets = new Insets(0, 0, 5, 5);
        gbc_lblOtherIP.gridx = 0;
        gbc_lblOtherIP.gridy = 2;
        getContentPane().add(lblOtherIP, gbc_lblOtherIP);
        
        theirIp = new JTextField();
        ((JTextField)theirIp).setText("localhost");
        GridBagConstraints gbc_theirIp = new GridBagConstraints();
        gbc_theirIp.insets = new Insets(0, 0, 5, 5);
        gbc_theirIp.fill = GridBagConstraints.HORIZONTAL;
        gbc_theirIp.gridx = 1;
        gbc_theirIp.gridy = 2;
        getContentPane().add(theirIp, gbc_theirIp);
        ((JTextField)theirIp).setColumns(10);
        
        JLabel lblSocket_1 = new JLabel("Their Port:");
        GridBagConstraints gbc_lblSocket_1 = new GridBagConstraints();
        gbc_lblSocket_1.gridwidth = 2;
        gbc_lblSocket_1.anchor = GridBagConstraints.EAST;
        gbc_lblSocket_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblSocket_1.gridx = 2;
        gbc_lblSocket_1.gridy = 2;
        getContentPane().add(lblSocket_1, gbc_lblSocket_1);
        
        theirSoc = new JTextField();
        ((JTextField)theirSoc).setText(model.getTheirPort()+"");
        GridBagConstraints gbc_theirSoc = new GridBagConstraints();
        gbc_theirSoc.insets = new Insets(0, 0, 5, 0);
        gbc_theirSoc.fill = GridBagConstraints.HORIZONTAL;
        gbc_theirSoc.gridx = 4;
        gbc_theirSoc.gridy = 2;
        getContentPane().add(theirSoc, gbc_theirSoc);
        ((JTextField)theirSoc).setColumns(10);
        
        JLabel lblStatus = new JLabel("Status:");
        GridBagConstraints gbc_lblStatus = new GridBagConstraints();
        gbc_lblStatus.anchor = GridBagConstraints.EAST;
        gbc_lblStatus.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatus.gridx = 0;
        gbc_lblStatus.gridy = 3;
        getContentPane().add(lblStatus, gbc_lblStatus);
        
        lblConnected = new JLabel("Connected");
        lblConnected.setOpaque(true);
        GridBagConstraints gbc_lblConnected = new GridBagConstraints();
        gbc_lblConnected.insets = new Insets(0, 0, 5, 5);
        gbc_lblConnected.gridx = 1;
        gbc_lblConnected.gridy = 3;
        getContentPane().add(lblConnected, gbc_lblConnected);
        
        btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
                if(btnConnect.getText().equals("Connect"))
                {
                    //connect to the designated IP
                    model.connect(((JTextField)theirIp).getText(), ((JTextField)theirSoc).getText(), ((JTextField)user).getText());
                }
                else if(btnConnect.getText().equals("Cancel"))
                {
                    model.cancelConnection();
                }
                else if(btnConnect.getText().equals("Disconnect"))
                {
                    model.disconnectConnection();
                }
            }
        });
        GridBagConstraints gbc_btnConnect = new GridBagConstraints();
        gbc_btnConnect.gridwidth = 3;
        gbc_btnConnect.insets = new Insets(0, 0, 5, 0);
        gbc_btnConnect.gridx = 2;
        gbc_btnConnect.gridy = 3;
        getContentPane().add(btnConnect, gbc_btnConnect);
        
        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 5;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 4;
        getContentPane().add(scrollPane, gbc_scrollPane);
        
        JLabel lblNewLabel_1 = new JLabel("Messages");
        scrollPane.setColumnHeaderView(lblNewLabel_1);
        
        txtpnHello = new JTextPane();
        txtpnHello.setEditable(false);
        scrollPane.setViewportView(txtpnHello);
        
        textField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.gridwidth = 4;
        gbc_textField.anchor = GridBagConstraints.NORTH;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 0;
        gbc_textField.gridy = 5;
        getContentPane().add(textField, gbc_textField);
        textField.setColumns(10);
        textField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                model.sendMessage(textField.getText());
                textField.setText("");
            }
        });
        
        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e)
            {
                model.sendMessage(textField.getText());
                textField.setText("");
            }
        });
        GridBagConstraints gbc_btnSend = new GridBagConstraints();
        gbc_btnSend.insets = new Insets(0, 0, 5, 0);
        gbc_btnSend.gridx = 4;
        gbc_btnSend.gridy = 5;
        getContentPane().add(btnSend, gbc_btnSend);

        //sets the starting defaults
        lblConnected.setBackground(Color.RED);
        lblConnected.setText("NOT Connected");
        btnConnect.setText("Connect");
        
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setToolTipText("Drop Files");
        scrollPane_1.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.gridheight = 3;
        gbc_scrollPane_1.gridwidth = 3;
        gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.gridx = 0;
        gbc_scrollPane_1.gridy = 6;
        getContentPane().add(scrollPane_1, gbc_scrollPane_1);
        
        JLabel lblFileArea = new JLabel("File Area");
        scrollPane_1.setColumnHeaderView(lblFileArea);
        scrollPane_1.setViewportView(dropZone);
        
        JButton deleteSel = new JButton("Delete Selected");
        deleteSel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dropZone.deleteFiles(true);
            }
        });
        
        JButton deleteAll = new JButton("Delete All");
        deleteAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                dropZone.deleteFiles(false);
            }
        });
        GridBagConstraints gbc_deleteAll = new GridBagConstraints();
        gbc_deleteAll.fill = GridBagConstraints.HORIZONTAL;
        gbc_deleteAll.insets = new Insets(0, 0, 5, 5);
        gbc_deleteAll.gridx = 3;
        gbc_deleteAll.gridy = 7;
        getContentPane().add(deleteAll, gbc_deleteAll);
        GridBagConstraints gbc_deleteSel = new GridBagConstraints();
        gbc_deleteSel.fill = GridBagConstraints.HORIZONTAL;
        gbc_deleteSel.insets = new Insets(0, 0, 5, 0);
        gbc_deleteSel.gridx = 4;
        gbc_deleteSel.gridy = 7;
        getContentPane().add(deleteSel, gbc_deleteSel);
        
        JButton sendAll = new JButton("Send All");
        sendAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                model.sendFiles(dropZone.getFiles(false));
            }
        });
        GridBagConstraints gbc_sendAll = new GridBagConstraints();
        gbc_sendAll.fill = GridBagConstraints.HORIZONTAL;
        gbc_sendAll.insets = new Insets(0, 0, 0, 5);
        gbc_sendAll.gridx = 3;
        gbc_sendAll.gridy = 8;
        getContentPane().add(sendAll, gbc_sendAll);
        
        JButton sendSel = new JButton("Send Selected");
        sendSel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                model.sendFiles(dropZone.getFiles(true));
            }
        });
        GridBagConstraints gbc_sendSel = new GridBagConstraints();
        gbc_sendSel.fill = GridBagConstraints.HORIZONTAL;
        gbc_sendSel.gridx = 4;
        gbc_sendSel.gridy = 8;
        getContentPane().add(sendSel, gbc_sendSel);
        
        setSize(518,431);
    }
    
    @Override
    public void update(Observable e, Object o)
    {        
        switch(model.isConnected())
        {
        case Model.WAITING_FOR_CONNECTION:
            lblConnected.setBackground(Color.YELLOW);
            lblConnected.setText("Waiting For Connection");
            btnConnect.setText("Cancel");
            change();
            break;
        case Model.CONNECTED:
            lblConnected.setBackground(Color.GREEN);
            lblConnected.setText("Connected");
            btnConnect.setText("Disconnect");
            break;
        case Model.NOT_CONNECTED:
            lblConnected.setBackground(Color.RED);
            lblConnected.setText("NOT Connected");
            btnConnect.setText("Connect");
            change();
            break;
        }

        txtpnHello.setText(model.getLog());
        //sets the text to the get log
        if(theirSoc instanceof JTextField)
        {
            ((JLabel)mySoc).setText(model.getMyPort()+"");
            ((JTextField)theirSoc).setText(model.getTheirPort()+"");
            ((JTextField)theirIp).setText(model.getTheirIp());
            ((JTextField)user).setText(model.getUsername());
        }
        else
        {
            ((JLabel)theirSoc).setText(model.getTheirPort()+"");
            ((JLabel)theirIp).setText(model.getTheirIp());
            ((JLabel)user).setText(model.getUsername());
        }
    }
    
    public void change()
    {
        getContentPane().remove(theirSoc);
        getContentPane().remove(theirIp);
        getContentPane().remove(user);
        

        GridBagConstraints gbc_theirSoc = new GridBagConstraints();
        GridBagConstraints gbc_theirIp = new GridBagConstraints();
        GridBagConstraints gbc_user = new GridBagConstraints();

        if(theirSoc instanceof JTextField)
        {
            theirSoc = new JLabel(((JTextField)theirSoc).getText());
            theirIp = new JLabel(((JTextField)theirIp).getText());
            user = new JLabel(((JTextField)user).getText());
        }
        else
        {
            theirSoc = new JTextField(((JLabel)theirSoc).getText());
            theirIp = new JTextField(((JLabel)theirIp).getText());
            user = new JTextField(((JLabel)user).getText());
            gbc_theirSoc.fill = GridBagConstraints.HORIZONTAL;
            gbc_theirIp.fill = GridBagConstraints.HORIZONTAL;
            gbc_user.fill = GridBagConstraints.HORIZONTAL;
        }
        
        gbc_theirSoc.insets = new Insets(0, 0, 5, 0);
        gbc_theirSoc.gridx = 4;
        gbc_theirSoc.gridy = 2;
        getContentPane().add(theirSoc, gbc_theirSoc);
        
        gbc_theirIp.insets = new Insets(0, 0, 5, 5);
        gbc_theirIp.gridx = 1;
        gbc_theirIp.gridy = 2;
        getContentPane().add(theirIp, gbc_theirIp);
        
        gbc_user.insets = new Insets(0, 0, 5, 5);
        gbc_user.gridx = 1;
        gbc_user.gridy = 0;
        getContentPane().add(user, gbc_user);
        
        revalidate();
        repaint();
    }

    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowClosing(WindowEvent e) {
        model.disconnectConnection();
//        System.exit(0);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub
        
    }
    
    public static void main(String [] args)
    {
        Model a = new Model();
        ConnectionFrame f = new ConnectionFrame(a);
        a.addObserver(f);
        
        Model b = new Model();
        ConnectionFrame f2 = new ConnectionFrame(b);
        b.addObserver(f2);
        f2.setLocation(200, 200);
    }
}

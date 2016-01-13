import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import java.awt.Insets;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;

import java.awt.Window.Type;
import java.awt.Dialog.ModalExclusionType;

import javax.swing.SpinnerNumberModel;


public class StartFrame extends JFrame implements WindowListener
{
    private JTextField username;
    private JTextField ip;
    private JPasswordField password;
    private JSpinner port;
    private Model m;
    
    public StartFrame() {
        setTitle("Welcome");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{130, 100, 58, 100, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);
        
        JLabel lblUsername = new JLabel("Username:");
        GridBagConstraints gbc_lblUsername = new GridBagConstraints();
        gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
        gbc_lblUsername.anchor = GridBagConstraints.EAST;
        gbc_lblUsername.gridx = 0;
        gbc_lblUsername.gridy = 0;
        getContentPane().add(lblUsername, gbc_lblUsername);
        
        username = new JTextField();
        GridBagConstraints gbc_username = new GridBagConstraints();
        gbc_username.insets = new Insets(0, 0, 5, 5);
        gbc_username.fill = GridBagConstraints.HORIZONTAL;
        gbc_username.gridx = 1;
        gbc_username.gridy = 0;
        getContentPane().add(username, gbc_username);
        username.setColumns(10);
        
        JLabel lblIp = new JLabel("IP:");
        GridBagConstraints gbc_lblIp = new GridBagConstraints();
        gbc_lblIp.anchor = GridBagConstraints.EAST;
        gbc_lblIp.insets = new Insets(0, 0, 5, 5);
        gbc_lblIp.gridx = 2;
        gbc_lblIp.gridy = 0;
        getContentPane().add(lblIp, gbc_lblIp);
        
        ip = new JTextField();
        GridBagConstraints gbc_ip = new GridBagConstraints();
        gbc_ip.insets = new Insets(0, 0, 5, 0);
        gbc_ip.fill = GridBagConstraints.HORIZONTAL;
        gbc_ip.gridx = 3;
        gbc_ip.gridy = 0;
        getContentPane().add(ip, gbc_ip);
        ip.setColumns(10);
        
        JLabel lblPasswordoptional = new JLabel("Password (optional):");
        GridBagConstraints gbc_lblPasswordoptional = new GridBagConstraints();
        gbc_lblPasswordoptional.anchor = GridBagConstraints.EAST;
        gbc_lblPasswordoptional.insets = new Insets(0, 0, 5, 5);
        gbc_lblPasswordoptional.gridx = 0;
        gbc_lblPasswordoptional.gridy = 1;
        getContentPane().add(lblPasswordoptional, gbc_lblPasswordoptional);
        
        password = new JPasswordField();
        GridBagConstraints gbc_password = new GridBagConstraints();
        gbc_password.insets = new Insets(0, 0, 5, 5);
        gbc_password.fill = GridBagConstraints.HORIZONTAL;
        gbc_password.gridx = 1;
        gbc_password.gridy = 1;
        getContentPane().add(password, gbc_password);
        
        JLabel lblPort = new JLabel("Port:");
        GridBagConstraints gbc_lblPort = new GridBagConstraints();
        gbc_lblPort.anchor = GridBagConstraints.EAST;
        gbc_lblPort.insets = new Insets(0, 0, 5, 5);
        gbc_lblPort.gridx = 2;
        gbc_lblPort.gridy = 1;
        getContentPane().add(lblPort, gbc_lblPort);
        
        port = new JSpinner();
        port.setModel(new SpinnerNumberModel(new Integer(4001), null, null, new Integer(1)));
        port.setToolTipText("Normally does not need to change unless port is in use on other computer. For most cases keep as defaulted value.");
        GridBagConstraints gbc_port = new GridBagConstraints();
        gbc_port.fill = GridBagConstraints.HORIZONTAL;
        gbc_port.insets = new Insets(0, 0, 5, 0);
        gbc_port.gridx = 3;
        gbc_port.gridy = 1;
        getContentPane().add(port, gbc_port);
        
        JButton btnConnect = new JButton("Connect To Any ServerClient");
        btnConnect.setToolTipText("Enter in info above. Connect to any client that is part of the server");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                m = new Model(username.getText(), new String(password.getPassword()));
                if(m.connect(ip.getText(), (int)(port.getValue())))
                {
                    m.setAcceptingConnection(true);
                    ConnectionFrame frame = new ConnectionFrame(m, false);
                    m.addObserver(frame);
                    StartFrame.this.dispose();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Could not connect to client.");
                    m.setAcceptingConnection(false);
                }
            }
        });
        GridBagConstraints gbc_btnConnect = new GridBagConstraints();
        gbc_btnConnect.insets = new Insets(0, 0, 5, 0);
        gbc_btnConnect.fill = GridBagConstraints.BOTH;
        gbc_btnConnect.gridwidth = 4;
        gbc_btnConnect.gridx = 0;
        gbc_btnConnect.gridy = 2;
        getContentPane().add(btnConnect, gbc_btnConnect);
        
        JButton btnOpenOwnServer = new JButton("Open Own Server");
        btnOpenOwnServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                m = new Model(username.getText(), new String(password.getPassword()));
                m.setAcceptingConnection(true);
                ConnectionFrame frame = new ConnectionFrame(m, true);
                m.addObserver(frame);
                StartFrame.this.dispose();
            }
        });
        
        JLabel lblIpForServer = new JLabel("IP for server is already decided:");
        GridBagConstraints gbc_lblIpForServer = new GridBagConstraints();
        gbc_lblIpForServer.gridwidth = 2;
        gbc_lblIpForServer.anchor = GridBagConstraints.EAST;
        gbc_lblIpForServer.insets = new Insets(0, 0, 5, 5);
        gbc_lblIpForServer.gridx = 0;
        gbc_lblIpForServer.gridy = 3;
        getContentPane().add(lblIpForServer, gbc_lblIpForServer);
        
        JLabel lblLocalhost = new JLabel("localhost");
        GridBagConstraints gbc_lblLocalhost = new GridBagConstraints();
        gbc_lblLocalhost.gridwidth = 2;
        gbc_lblLocalhost.insets = new Insets(0, 0, 5, 0);
        gbc_lblLocalhost.gridx = 2;
        gbc_lblLocalhost.gridy = 3;
        getContentPane().add(lblLocalhost, gbc_lblLocalhost);
        GridBagConstraints gbc_btnOpenOwnServer = new GridBagConstraints();
        gbc_btnOpenOwnServer.gridheight = 2;
        gbc_btnOpenOwnServer.fill = GridBagConstraints.BOTH;
        gbc_btnOpenOwnServer.gridwidth = 4;
        gbc_btnOpenOwnServer.gridx = 0;
        gbc_btnOpenOwnServer.gridy = 4;
        getContentPane().add(btnOpenOwnServer, gbc_btnOpenOwnServer);
        
        this.setVisible(true);
        this.setSize(new Dimension(400, 200));
    }

    
    public static void main(String [] args)
    {
        Model m = new Model("Sam", "pass");
        m.setAcceptingConnection(true);
        ConnectionFrame frame = new ConnectionFrame(m, true);
        m.addObserver(frame);
        
        Model m2 = new Model("Edd", "pass");
        m2.setAcceptingConnection(true);
        ConnectionFrame frame2 = new ConnectionFrame(m2, true);
        m2.addObserver(frame2);
        frame2.setLocation(500, 0);
        
        Model m3 = new Model("Yes", "pass");
        m3.setAcceptingConnection(true);
        ConnectionFrame frame3 = new ConnectionFrame(m3, true);
        m3.addObserver(frame3);
        frame3.setLocation(1000, 0);
        
        Model m4 = new Model("Who", "pass");
        m4.setAcceptingConnection(true);
        ConnectionFrame frame4 = new ConnectionFrame(m4, true);
        m4.addObserver(frame4);
        frame4.setLocation(1500, 0);
    }


    @Override
    public void windowActivated(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void windowClosed(WindowEvent arg0) {
        m.disconnectConnection(true);
    }


    @Override
    public void windowClosing(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void windowDeactivated(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void windowDeiconified(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void windowIconified(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void windowOpened(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}

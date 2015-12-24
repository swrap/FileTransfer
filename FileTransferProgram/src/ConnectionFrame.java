import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import java.awt.Color;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;


public class ConnectionFrame extends JFrame
{
    private JTextField textField;
    private JTextField txtMine;
    private JTextField txtTheirip;
    private JTextField txtMysocket;
    private JTextField txtTheirsocket;
    
    public ConnectionFrame()
    {
        setBackground(Color.WHITE);
        setTitle("SSL");
        setFont(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 105, 75, 92, 0};
        gridBagLayout.rowHeights = new int[]{22, 22, 22, 68, 22, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);
        
        JLabel lblYourIP = new JLabel("Your IP: 98.114.170.91");
        GridBagConstraints gbc_lblYourIP = new GridBagConstraints();
        gbc_lblYourIP.anchor = GridBagConstraints.NORTHEAST;
        gbc_lblYourIP.insets = new Insets(0, 0, 5, 5);
        gbc_lblYourIP.gridx = 0;
        gbc_lblYourIP.gridy = 0;
        getContentPane().add(lblYourIP, gbc_lblYourIP);
        
        txtMine = new JTextField();
        txtMine.setText("mine");
        GridBagConstraints gbc_txtMine = new GridBagConstraints();
        gbc_txtMine.insets = new Insets(0, 0, 5, 5);
        gbc_txtMine.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMine.gridx = 1;
        gbc_txtMine.gridy = 0;
        getContentPane().add(txtMine, gbc_txtMine);
        txtMine.setColumns(10);
        
        JLabel lblSocket = new JLabel("Socket: 8777");
        GridBagConstraints gbc_lblSocket = new GridBagConstraints();
        gbc_lblSocket.anchor = GridBagConstraints.EAST;
        gbc_lblSocket.insets = new Insets(0, 0, 5, 5);
        gbc_lblSocket.gridx = 2;
        gbc_lblSocket.gridy = 0;
        getContentPane().add(lblSocket, gbc_lblSocket);
        
        txtMysocket = new JTextField();
        txtMysocket.setText("mysocket");
        GridBagConstraints gbc_txtMysocket = new GridBagConstraints();
        gbc_txtMysocket.insets = new Insets(0, 0, 5, 0);
        gbc_txtMysocket.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMysocket.gridx = 3;
        gbc_txtMysocket.gridy = 0;
        getContentPane().add(txtMysocket, gbc_txtMysocket);
        txtMysocket.setColumns(10);
        
        JLabel lblOtherIP = new JLabel("Other IP: 98.114.170.91");
        GridBagConstraints gbc_lblOtherIP = new GridBagConstraints();
        gbc_lblOtherIP.anchor = GridBagConstraints.NORTHEAST;
        gbc_lblOtherIP.insets = new Insets(0, 0, 5, 5);
        gbc_lblOtherIP.gridx = 0;
        gbc_lblOtherIP.gridy = 1;
        getContentPane().add(lblOtherIP, gbc_lblOtherIP);
        
        txtTheirip = new JTextField();
        txtTheirip.setText("theirip");
        GridBagConstraints gbc_txtTheirip = new GridBagConstraints();
        gbc_txtTheirip.insets = new Insets(0, 0, 5, 5);
        gbc_txtTheirip.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtTheirip.gridx = 1;
        gbc_txtTheirip.gridy = 1;
        getContentPane().add(txtTheirip, gbc_txtTheirip);
        txtTheirip.setColumns(10);
        
        JLabel lblSocket_1 = new JLabel("Socket: 8777");
        GridBagConstraints gbc_lblSocket_1 = new GridBagConstraints();
        gbc_lblSocket_1.anchor = GridBagConstraints.EAST;
        gbc_lblSocket_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblSocket_1.gridx = 2;
        gbc_lblSocket_1.gridy = 1;
        getContentPane().add(lblSocket_1, gbc_lblSocket_1);
        
        txtTheirsocket = new JTextField();
        txtTheirsocket.setText("theirsocket");
        GridBagConstraints gbc_txtTheirsocket = new GridBagConstraints();
        gbc_txtTheirsocket.insets = new Insets(0, 0, 5, 0);
        gbc_txtTheirsocket.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtTheirsocket.gridx = 3;
        gbc_txtTheirsocket.gridy = 1;
        getContentPane().add(txtTheirsocket, gbc_txtTheirsocket);
        txtTheirsocket.setColumns(10);
        
        JLabel lblStatus = new JLabel("Status:");
        GridBagConstraints gbc_lblStatus = new GridBagConstraints();
        gbc_lblStatus.anchor = GridBagConstraints.WEST;
        gbc_lblStatus.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatus.gridx = 0;
        gbc_lblStatus.gridy = 2;
        getContentPane().add(lblStatus, gbc_lblStatus);
        
        JLabel lblConnected = new JLabel("Connected");
        GridBagConstraints gbc_lblConnected = new GridBagConstraints();
        gbc_lblConnected.insets = new Insets(0, 0, 5, 5);
        gbc_lblConnected.gridx = 1;
        gbc_lblConnected.gridy = 2;
        getContentPane().add(lblConnected, gbc_lblConnected);
        
        JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
                System.out.println("RUNNING");
            }
        });
        GridBagConstraints gbc_btnConnect = new GridBagConstraints();
        gbc_btnConnect.gridwidth = 2;
        gbc_btnConnect.insets = new Insets(0, 0, 5, 5);
        gbc_btnConnect.gridx = 2;
        gbc_btnConnect.gridy = 2;
        getContentPane().add(btnConnect, gbc_btnConnect);
        
        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 4;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 3;
        getContentPane().add(scrollPane, gbc_scrollPane);
        
        JLabel lblNewLabel_1 = new JLabel("Messages");
        scrollPane.setColumnHeaderView(lblNewLabel_1);
        
        JTextPane txtpnHello = new JTextPane();
        scrollPane.setViewportView(txtpnHello);
        
        textField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.gridwidth = 3;
        gbc_textField.anchor = GridBagConstraints.NORTH;
        gbc_textField.insets = new Insets(0, 0, 0, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 0;
        gbc_textField.gridy = 4;
        getContentPane().add(textField, gbc_textField);
        textField.setColumns(10);
        
        JButton btnSend = new JButton("Send");
        GridBagConstraints gbc_btnSend = new GridBagConstraints();
        gbc_btnSend.gridx = 3;
        gbc_btnSend.gridy = 4;
        getContentPane().add(btnSend, gbc_btnSend);
        setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{getContentPane(), lblYourIP, txtMine, lblSocket, txtMysocket, lblOtherIP, txtTheirip, lblSocket_1, txtTheirsocket, lblStatus, lblConnected, btnConnect, scrollPane, lblNewLabel_1, txtpnHello, textField, btnSend}));

        setSize(500,500);
    }
    
    public static void main(String [] args)
    {
        new ConnectionFrame();
    }
}

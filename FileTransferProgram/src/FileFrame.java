import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JProgressBar;

import java.awt.GridBagConstraints;

import javax.swing.JSlider;

import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class FileFrame extends JFrame implements Observer
{
    private JSlider slider;
    private JTextPane fileLog;
    private JLabel bufferSize;
    private JProgressBar progressBar;
    private JLabel timeLeft;
    private JLabel speed;
    
    public FileFrame(final Model model)
    {
        setTitle("File Transfer Info.");
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{129, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{74, 0, 35, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);
        
        JLabel lblBufferLength = new JLabel("Buffer Length");
        GridBagConstraints gbc_lblBufferLength = new GridBagConstraints();
        gbc_lblBufferLength.insets = new Insets(0, 0, 5, 5);
        gbc_lblBufferLength.gridx = 0;
        gbc_lblBufferLength.gridy = 0;
        getContentPane().add(lblBufferLength, gbc_lblBufferLength);
        
        slider = new JSlider(1, 16384, model.getBuffer());
        slider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e)
            {
                model.setBuffer(slider.getValue());
                bufferSize.setText(model.getBuffer() + "");
            }            
        });
        GridBagConstraints gbc_slider = new GridBagConstraints();
        gbc_slider.insets = new Insets(0, 0, 5, 5);
        gbc_slider.gridx = 1;
        gbc_slider.gridy = 0;
        getContentPane().add(slider, gbc_slider);
        
        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridheight = 5;
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 2;
        gbc_scrollPane.gridy = 0;
        getContentPane().add(scrollPane, gbc_scrollPane);
        
        JLabel lblLog = new JLabel("Log");
        scrollPane.setColumnHeaderView(lblLog);
        
        fileLog = new JTextPane();
        scrollPane.setViewportView(fileLog);
        
        JLabel lblCurrentBufferSize = new JLabel("Current Buffer Size:");
        GridBagConstraints gbc_lblCurrentBufferSize = new GridBagConstraints();
        gbc_lblCurrentBufferSize.anchor = GridBagConstraints.EAST;
        gbc_lblCurrentBufferSize.insets = new Insets(0, 0, 5, 5);
        gbc_lblCurrentBufferSize.gridx = 0;
        gbc_lblCurrentBufferSize.gridy = 1;
        getContentPane().add(lblCurrentBufferSize, gbc_lblCurrentBufferSize);
        
        bufferSize = new JLabel(model.getBuffer() + "");
        GridBagConstraints gbc_bufferSize = new GridBagConstraints();
        gbc_bufferSize.insets = new Insets(0, 0, 5, 5);
        gbc_bufferSize.gridx = 1;
        gbc_bufferSize.gridy = 1;
        getContentPane().add(bufferSize, gbc_bufferSize);
        
        JLabel lblDownloadSpeed = new JLabel("Download Speed");
        GridBagConstraints gbc_lblDownloadSpeed = new GridBagConstraints();
        gbc_lblDownloadSpeed.insets = new Insets(0, 0, 5, 5);
        gbc_lblDownloadSpeed.gridx = 0;
        gbc_lblDownloadSpeed.gridy = 2;
        getContentPane().add(lblDownloadSpeed, gbc_lblDownloadSpeed);
        
        progressBar = new JProgressBar();
//        progressBar.add();
        GridBagConstraints gbc_progressBar = new GridBagConstraints();
        gbc_progressBar.insets = new Insets(0, 0, 5, 5);
        gbc_progressBar.gridx = 1;
        gbc_progressBar.gridy = 2;
        getContentPane().add(progressBar, gbc_progressBar);
        
        JLabel lblNewLabel = new JLabel("Time Left: ");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 3;
        getContentPane().add(lblNewLabel, gbc_lblNewLabel);
        
        timeLeft = new JLabel("");
        GridBagConstraints gbc_timeLeft = new GridBagConstraints();
        gbc_timeLeft.insets = new Insets(0, 0, 5, 5);
        gbc_timeLeft.gridx = 1;
        gbc_timeLeft.gridy = 3;
        getContentPane().add(timeLeft, gbc_timeLeft);
        
        JLabel lblSpeed = new JLabel("Speed:");
        GridBagConstraints gbc_lblSpeed = new GridBagConstraints();
        gbc_lblSpeed.anchor = GridBagConstraints.EAST;
        gbc_lblSpeed.insets = new Insets(0, 0, 0, 5);
        gbc_lblSpeed.gridx = 0;
        gbc_lblSpeed.gridy = 4;
        getContentPane().add(lblSpeed, gbc_lblSpeed);
        
        speed = new JLabel("");
        GridBagConstraints gbc_speed = new GridBagConstraints();
        gbc_speed.insets = new Insets(0, 0, 0, 5);
        gbc_speed.gridx = 1;
        gbc_speed.gridy = 4;
        getContentPane().add(speed, gbc_speed);
    }

    @Override
    public void update(Observable o, Object arg)
    {
        Model m = (Model) arg;
    }
    
}

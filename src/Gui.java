import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * This class works with graphic interface. It initialize graphic and update it.
 * */
public class Gui {
    /** This object is frame of app*/
    private JFrame frame = new JFrame("Messenger");
    /** This area shows messages and status of interfaces*/
    private JTextArea dialogWindow = new JTextArea(20, 30);
    /** This area shows info about interface's IPs and number of ports*/
    private JTextArea serviceWindow = new JTextArea(10, 21);
    /** This is button, which start process of receiving messages
     * @see Application#startServer() */
    private JButton buttonStartServer = new JButton("Turn On");
    /** This is button, which ends process of receiving messages
     * @see Application#stopServer()  */
    private JButton buttonStopServer = new JButton("Turn Off");
    /** This is panel, where all elements stored*/
    private JPanel panelButtons = new JPanel();
    /** This is object of main part of app*/
    private Application server;

    /**
     * Constructor of gui part
     * @param server object, which allows take info from main part of app
     * */
    public Gui(Application server) {
        this.server = server;
    }

    /**
     * This method initialize all graphic elements and add it to window, which is shown to user
     * */
    protected void initFrameServer() {
        dialogWindow.setEditable(false);
        dialogWindow.setLineWrap(true);
        serviceWindow.setEditable(false);
        frame.add(new JScrollPane(dialogWindow), BorderLayout.CENTER);
        frame.add(new JScrollPane(serviceWindow), BorderLayout.EAST);
        panelButtons.add(buttonStartServer);
        panelButtons.add(buttonStopServer);
        frame.add(panelButtons, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.stopServer();
                System.exit(0);
            }
        });
        frame.setVisible(true);

        buttonStartServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.startServer();
            }
        });
        buttonStopServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stopServer();
            }
        });
    }

    /**
     * This method updates text in area, where text of messages shown
     * */
    public void refreshDialogWindow(String serviceMessage) {
	Date time = new Date();
	SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm:ss");
        dialogWindow.append(timeFormat.format(time) + " : "  + serviceMessage);
    }

    /**
     * This method updates text in service area(IPs, ports)
     * */
    public void refreshServiceWindow(String serviceMessage) {
        serviceWindow.append(serviceMessage);
    }

    /**
     * This method clear service info, when user press "Turn Off" button
     * @see Application#stopServer()
     * */
    public void clearServiceWindow() {
        serviceWindow.setText(null);
    }
}

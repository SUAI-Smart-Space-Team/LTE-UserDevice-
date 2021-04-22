import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;

/**
 * This class control all application work. It control graphic part and control work of threads
 * */
public class Application {

    /** This is object of graphic interface*/
    public static Gui gui;
    /** This is number of WiFi's port*/
    private int WIFI_PORT;
    /** This is number of LTE's port*/
    private int LTE_PORT;
    /** This is max value which can be given to WiFi/LTE port*/
    private final int MAX_PORT_NUMBER = 48999;
    /** This is min value which can be given to WiFi/LTE port*/
    private final int MIN_PORT_NUMBER = 48654;
    /** This is UDP socket for WiFi interface*/
    private DatagramSocket WIFI_Socket;
    /** This is UDP socket for LTE interface*/
    private DatagramSocket LTE_Socket;
    /** This is flag which shows application status.
     * If false, app can't get any messages
     * If true, app can accept messages*/
    private boolean isAlive = false;
    /** This is flag which shows does user have WiFi interface or not
     * If false, user don't connected for WiFi
     * If true, user connected for WiFi*/
    private boolean wifiExists = false;
    /** This is flag which shows does user have LTE interface or not
     * If false, user don't connected for LTE
     * If true, user connected for LTE*/
    private boolean lteExists = false;
    /** This is thread, which works with LTE interface*/
    private ServerThread LTE_Thread;
    /** This is thread, which works with WiFi interface*/
    private ServerThread WIFI_Thread;
    /** This is string, which contains IPv4 for WiFi interface*/
    private String wifiIpIN;
    /** This is string, which contains IPv4 for LTE interface*/
    private String lteIpIN;
    /** This is string, which contains name of the os on what app is running
     * @see Application#getInfoAboutIPs() */
    private String OS;

    /** This method open sockets and start threads for work with all connected interfaces,
     * when user press "Turn On" button */
    protected void startServer() {
        try {
            if (!isAlive) {
                isAlive = true;
                this.getInfoAboutIPs();
                if (this.wifiExists) {
                    WIFI_Socket = new DatagramSocket(WIFI_PORT);
                    this.WIFI_Thread = new ServerThread(this.WIFI_Socket, "WIFI");
                    this.WIFI_Thread.start();
                }
                if (this.lteExists) {
                    LTE_Socket = new DatagramSocket(LTE_PORT);
                    this.LTE_Thread = new ServerThread(this.LTE_Socket, "LTE");
                    this.LTE_Thread.start();
                }
            }
        } catch (Exception e) {
            gui.refreshDialogWindow("Can't turn on server.\n");
        }
    }

    /** This method close sockets and stop all threads, when user press "Turn Off" button */
    protected void stopServer() {
        if (isAlive) {
            isAlive = false;
            try {
                if (wifiExists) {
                    WIFI_Socket.close();
                    gui.refreshDialogWindow("WiFi - Down\n");
                    wifiIpIN = null;
                }
                if (lteExists) {
                    LTE_Socket.close();
                    gui.refreshDialogWindow("LTE - Down\n");
                    lteIpIN = null;
                }
                gui.clearServiceWindow();

            } catch (Exception e) {
                gui.refreshDialogWindow("Can't stop server.\n");
            }
        }
    }

    /** This method takes info about WiFi, LTE connected interfaces from
     * all network interfaces, which connected for this PC*/
    protected void getInfoAboutIPs() {
        try {
            gui.refreshServiceWindow("WIFI IP's\n");
            Enumeration<NetworkInterface> tmp = NetworkInterface.getNetworkInterfaces();
            while (tmp.hasMoreElements()) {
                NetworkInterface networkInterfaceTmp = (NetworkInterface) tmp.nextElement();
                Enumeration element = networkInterfaceTmp.getInetAddresses();
                String name = networkInterfaceTmp.getName();
                String lteName = networkInterfaceTmp.getDisplayName().toLowerCase(Locale.ROOT);

                if (this.OS.contains("mac")) {
                    if (name.equals("en0")) {
                        while (element.hasMoreElements()) {
                            InetAddress i = (InetAddress) element.nextElement();
                            if (i.getHostAddress().indexOf('.') != -1) {
                                this.wifiIpIN = i.getHostAddress();
                                this.wifiExists = true;
                            }
                        }
                    }
                    continue;
                }

                if (this.OS.contains("linux")) {
                    if (name.contains("enp")) {
                        if (lteName.contains("band luxe")) {
                            while (element.hasMoreElements()) {
                                InetAddress i = (InetAddress) element.nextElement();
                                if (i.getHostAddress().indexOf('.') != -1) {
                                    this.lteIpIN = i.getHostAddress();
                                    this.lteExists = true;
                                }
                            }
                        }
                    }

                    if (name.contains("wlp")) {
                        while (element.hasMoreElements()) {
                            InetAddress i = (InetAddress) element.nextElement();
                            if (i.getHostAddress().indexOf('.') != -1) {
                                this.wifiIpIN = i.getHostAddress();
                                this.wifiExists = true;
                            }
                        }
                    }
                    continue;

                }

                if (this.OS.contains("windows")) {
                    if (lteName.contains("bandluxe")) {
                        while (element.hasMoreElements()) {
                            InetAddress i = (InetAddress) element.nextElement();
                            if (i.getHostAddress().indexOf('.') != -1) {
                                this.lteIpIN = i.getHostAddress();
                                this.lteExists = true;
                            }
                        }
                    }

                    if (name.contains("wlan")) {
                        while (element.hasMoreElements()) {
                            InetAddress i = (InetAddress) element.nextElement();
                            if (i.getHostAddress().indexOf('.') != -1) {
                                this.wifiIpIN = i.getHostAddress();
                                this.wifiExists = true;
                            }
                        }
                    }
                    continue;
                }
            }
            gui.refreshServiceWindow("WiFi In IP: " + this.wifiIpIN + "\n");
            gui.refreshServiceWindow("WiFi Out IP: " + this.wifiIpIN + "\n");
            gui.refreshServiceWindow("Wifi port: " + this.WIFI_PORT + "\n");
            gui.refreshServiceWindow("LTE In IP: " + this.lteIpIN + "\n");
            gui.refreshServiceWindow("LTE Out IP: " + this.lteIpIN + "\n");
            gui.refreshServiceWindow("LTE port: " + this.LTE_PORT + "\n");
        } catch (Exception e) {
            gui.refreshDialogWindow("Can't get info about net interfaces.\n");
        }
    }

    /** This is start method, which run the hole app, give number of ports for WiFi\LTE
     *  and initialize graphic object */
    public static void main(String[] args) {
        Application server = new Application();
        Random random = new Random();
        server.OS = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        //server.WIFI_PORT = 48761;
        //server.LTE_PORT = 48654;
        server.WIFI_PORT = (server.MIN_PORT_NUMBER + random.nextInt(server.MAX_PORT_NUMBER - server.MIN_PORT_NUMBER)) % server.MAX_PORT_NUMBER;
        server.LTE_PORT = (server.MIN_PORT_NUMBER + random.nextInt(server.MAX_PORT_NUMBER - server.MIN_PORT_NUMBER)) % server.MAX_PORT_NUMBER;
        if (server.WIFI_PORT == server.LTE_PORT)
            server.LTE_PORT = (server.MIN_PORT_NUMBER + random.nextInt(server.MAX_PORT_NUMBER - server.MIN_PORT_NUMBER)) % server.MAX_PORT_NUMBER;
        gui = new Gui(server);
        gui.initFrameServer();

    }

    /** This is inner class which make all work with sockets.
     * It receive all UDP packets, parse data from it and show it one the screen*/
    private class ServerThread extends Thread {
        private DatagramSocket socket;
        private byte[] receiveData;
        private String interfaceName;

        /**This is constructor which creates Thread object
         * @param inputSocket - it is interface's socket where UDP packets arrive
         * @param name - it is name of the interface, which is served by this thread */
        public ServerThread(DatagramSocket inputSocket, String name) {
            this.socket = inputSocket;
            this.interfaceName = name;
            gui.refreshDialogWindow(this.interfaceName + " UP\n");
        }

        /**This is method, which receive data from packet, parse it and show on the screen*/
        @Override
        public void run() {
            while (true) {
                if (isAlive) {
                    this.receiveData = new byte[1024];
                    DatagramPacket inputPacket = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        this.socket.receive(inputPacket);
                    } catch (IOException e) {
                        if (isAlive)
                            gui.refreshDialogWindow("Error on getting message\n");
                        break;
                    }
                    String sentence = new String(inputPacket.getData());
                    gui.refreshDialogWindow( this.parseMessage(sentence) + "\n");
                }
            }
        }

        /**
         * This is method, which parse JSON string and return string which contains only interface's
         * name and message
         * @param inputText - JSON string, which is received from socket
         * @return returns string in format - "Used interface" messageText
         * */
        private String parseMessage(String inputText) {
            int pos1 = inputText.indexOf('{');
            int pos2 = inputText.indexOf('}');
            String tmp = inputText.substring(pos1 + 1, pos2);
            StringBuilder result = new StringBuilder();
            String[] data = tmp.split(",");
            for (int i = 0; i < data.length; i++) {
                if (i == 0) {
                    String[] finalData = data[i].split(":");
                    result.append(finalData[1]);
                    result.append(" ");
                }
                if (i == 1) {
                    String[] finalData = data[i].split(":");
                    int end = finalData[1].lastIndexOf("\"");
                    result.append(finalData[1].substring(1, end));
                    result.append(" ");
                }
            }
            result.append("\n");
            return result.toString();
        }

    }

}

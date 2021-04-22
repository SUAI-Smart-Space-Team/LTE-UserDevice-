import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Random;

public class Application {

    public static Gui gui; //object of graphics interface
    private int WIFI_PORT;
    private int LTE_PORT;
    private final int MAX_PORT_NUMBER = 48999;
    private final int MIN_PORT_NUMBER = 48654;
    private DatagramSocket WIFI_Socket;
    private DatagramSocket LTE_Socket;
    private boolean isAlive = false;
    private boolean wifiExists = false;
    private boolean lteExists = false;
    private ServerThread LTE_Thread;
    private ServerThread WIFI_Thread;
    private String wifiIpIN;
    private String lteIpIN;

    //method, which open udp socket and start thread
    protected void startServer() {
        try {
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
        } catch (Exception e) {
            gui.refreshDialogWindow("Can't turn on server.\n");
        }
    }

    //method, which close socket and turn off thread
    protected void stopServer() {
        isAlive = false;
        try {
            if (wifiExists) {
                WIFI_Socket.close();
                gui.refreshDialogWindow("WiFi - Down\n");
            }
            if (lteExists) {
                LTE_Socket.close();
                gui.refreshDialogWindow("LTE - Down\n");
            }
            gui.clearServiceWindow();

        } catch (Exception e) {
            gui.refreshDialogWindow("Can't stop server.\n");
        }
    }

    protected void getInfoAboutIPs() {
        try {
            gui.refreshServiceWindow("WIFI IP's\n");
            Enumeration<NetworkInterface> tmp = NetworkInterface.getNetworkInterfaces();
            while (tmp.hasMoreElements()) {
                NetworkInterface networkInterfaceTmp = (NetworkInterface) tmp.nextElement();
                Enumeration element = networkInterfaceTmp.getInetAddresses();
                String name = networkInterfaceTmp.getName();
                if (name.indexOf('w') != -1) {
                    while (element.hasMoreElements()) {
                        InetAddress i = (InetAddress) element.nextElement();
                        if (i.getHostAddress().indexOf('.') != -1) {
                            this.wifiIpIN = i.getHostAddress();
                            this.wifiExists = true;
                        }
                    }
                }
                if (name.indexOf('e') != -1) {
                    if (name.indexOf('V') == -1) {
                        while (element.hasMoreElements()) {
                            InetAddress i = (InetAddress) element.nextElement();
                            if (i.getHostAddress().indexOf('.') != -1) {
                                this.lteIpIN = i.getHostAddress();
                                this.lteExists = true;
                            }
                        }
                    }
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

    //entry method for application
    public static void main(String[] args) {
        Application server = new Application();
        Random random = new Random();
        //server.WIFI_PORT = 48832;
        //server.LTE_PORT = 48654;
        server.WIFI_PORT = (server.MIN_PORT_NUMBER + random.nextInt(server.MAX_PORT_NUMBER - server.MIN_PORT_NUMBER)) % server.MAX_PORT_NUMBER;
        server.LTE_PORT = (server.MIN_PORT_NUMBER + random.nextInt(server.MAX_PORT_NUMBER - server.MIN_PORT_NUMBER)) % server.MAX_PORT_NUMBER;
        if (server.WIFI_PORT == server.LTE_PORT)
            server.LTE_PORT = (server.MIN_PORT_NUMBER + random.nextInt(server.MAX_PORT_NUMBER - server.MIN_PORT_NUMBER)) % server.MAX_PORT_NUMBER;
        gui = new Gui(server);
        gui.initFrameServer();

    }

    //inner class, which works with socket
    private class ServerThread extends Thread {
        private DatagramSocket socket;
        private int portNumber;
        private byte[] receiveData;
        private String interfaceName;

        public ServerThread(DatagramSocket inputSocket, String name) {
            this.socket = inputSocket;
            this.interfaceName = name;
            gui.refreshDialogWindow(this.interfaceName + " UP\n");
        }

        //method, which get data from socket and print it on application screen
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
                    gui.refreshDialogWindow(this.parseMessage(sentence) + "\n");
                }
            }
        }

        //method, which parses input JSON
        private String parseMessage(String inputText) {
            int pos1 = inputText.indexOf('{');
            int pos2 = inputText.indexOf('}');
            String tmp = inputText.substring(pos1 + 1, pos2 - 1);
            StringBuilder result = new StringBuilder();
            String[] data = tmp.split(",");
            for (int i = 0; i < data.length; i++) {
                String[] finalData = data[i].split(":");
                result.append(finalData[1]);
                result.append(" ");
            }
            result.append("\n");
            return result.toString();
        }

    }

}
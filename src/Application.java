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
    private ServerThread LTE_Thread;
    private ServerThread WIFI_Thread;
    private String wifiIpIN;

    //methhod, which open udp socket and start thread
    protected void startServer() {
        try {
            WIFI_Socket = new DatagramSocket(WIFI_PORT);
            LTE_Socket = new DatagramSocket(LTE_PORT);
            isAlive = true;
            this.getInfoAboutIPs();
            this.WIFI_Thread = new ServerThread(this.WIFI_Socket, "WIFI");
            this.WIFI_Thread.start();
            this.LTE_Thread = new ServerThread(this.LTE_Socket, "LTE");
            this.LTE_Thread.start();
            gui.refreshDialogWindow("Opened socket for receiving messages.\n");
        } catch (Exception e) {
            gui.refreshDialogWindow("Can't turn on server.\n");
        }
    }

    //method, which close socket and turn off thread
    protected void stopServer() {
        isAlive = false;
        try {
            WIFI_Socket.close();
            gui.refreshDialogWindow("Server stopped.\n");

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
                        this.wifiIpIN = i.getHostAddress();
                    }
                }
            }
            gui.refreshServiceWindow("In IP: " + this.wifiIpIN + "\n");
            gui.refreshServiceWindow("Out IP: " + this.wifiIpIN + "\n");
            gui.refreshServiceWindow("Wifi port: " + this.WIFI_PORT + "\n");
        } catch (Exception e) {
            gui.refreshDialogWindow("Can't get info about net interfaces.\n");
        }
    }

    //entry method for application
    public static void main(String[] args) {
        Application server = new Application();
        Random random = new Random();
        //server.WIFI_PORT = 50001;
        //server.LTE_PORT = 50003;
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
            gui.refreshStatusWindow(this.interfaceName + " UP!\n");
        }

        //method, which get data from socket and print it on application screen
        @Override
        public void run() {
            while (true) {
                if (isAlive) {
                    this.receiveData = new byte[1024];
                    DatagramPacket inputPacket = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        WIFI_Socket.receive(inputPacket);
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Application {

    private static Gui gui; //object of graphics interface
    private final int SERVER_PORT = 50001;
    private DatagramSocket serverSocket;
    private boolean isAlive = false;
    private ServerThread serverThread;

    //methhod, which open udp socket and start thread
    protected void startServer() {
        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
            isAlive = true;
            this.serverThread = new ServerThread(this.serverSocket);
            gui.refreshDialogWindowServer("Создаю поток" + "\n");
            this.serverThread.start();
            gui.refreshDialogWindowServer("Сервер запущен.\n");
        } catch (Exception e) {
            gui.refreshDialogWindowServer("Не удалось запустить сервер.\n");
        }
    }

    //method, which close socket and turn off thread
    protected void stopServer() {
        isAlive = false;
        try {
            serverSocket.close();
            gui.refreshDialogWindowServer("Сервер остановлен.\n");

        } catch (Exception e) {
            gui.refreshDialogWindowServer("Остановить сервер не удалось.\n");
        }
    }

    //entry method for application
    public static void main(String[] args) {
        Application server = new Application();
        gui = new Gui(server);
        gui.initFrameServer();

    }

    //inner class, which works with socket
    private class ServerThread extends Thread {
        private DatagramSocket socket;
        private byte[] receiveData;

        public ServerThread(DatagramSocket inputSocket) {
            this.socket = inputSocket;
        }

        //method, which get data from socket and print it on application screen
        @Override
        public void run() {
            while (true) {
                if (isAlive) {
                    this.receiveData = new byte[1024];
                    DatagramPacket inputPacket = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        serverSocket.receive(inputPacket);
                    } catch (IOException e) {
                        if (isAlive)
                            gui.refreshDialogWindowServer("Ошибка при получении сообщения\n");
                        break;
                    }
                    String sentence = new String(inputPacket.getData());
                    gui.refreshDialogWindowServer(this.parseMessage(sentence) + "\n");
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

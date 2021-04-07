import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Gui {
    private JFrame frame = new JFrame("Конечное устройство");
    private JTextArea dialogWindow = new JTextArea(10, 40);
    private JButton buttonStartServer = new JButton("Запустить");
    private JButton buttonStopServer = new JButton("Остановить");
    private JPanel panelButtons = new JPanel();
    private Application server;

    public Gui(Application server) {
        this.server = server;
    }

    //метод инициализации графического интерфейса приложения сервера
    protected void initFrameServer() {
        dialogWindow.setEditable(false);
        dialogWindow.setLineWrap(true);  //автоматический перенос строки в JTextArea
        frame.add(new JScrollPane(dialogWindow), BorderLayout.CENTER);
        panelButtons.add(buttonStartServer);
        panelButtons.add(buttonStopServer);
        frame.add(panelButtons, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null); // при запуске отображает окно по центру экрана
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //класс обработки события при закрытии окна приложения Сервера
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

    //метод который добавляет в текстовое окно новое сообщение
    public void refreshDialogWindowServer(String serviceMessage) {
        dialogWindow.append(serviceMessage);
    }
}
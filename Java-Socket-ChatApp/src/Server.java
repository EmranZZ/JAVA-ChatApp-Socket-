
import javax.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class Server extends JFrame {

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("CLIENT-1");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN,20);

    public Server() {

        try {
            server = new ServerSocket(9000);
            System.out.println("CLIENT-1 is ready to accept connection...");
            System.out.println("waiting...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createGUI(){

        this.setTitle("CLIENT-1 END");
        this.setSize(600,650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        messageArea.setFont(font);
        messageArea.setBackground(new Color(204, 255, 204));

        messageInput.setFont(font);
        messageInput.setBackground(new Color(255, 255, 204));


        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        messageArea.setEditable(false);

        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    private void handleEvents() {

        messageInput.addActionListener(e -> {

            String contentToSend = messageInput.getText();
            messageArea.append("ME: " + contentToSend + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
            out.println(contentToSend);
            out.flush();
            messageInput.setText("");

        });
    }

    public void startReading()
    {
        Runnable r1 = ()->{
            System.out.println("Reader started...");
            try {
                while(true){
                    String msg = br.readLine();
                    if (msg.equalsIgnoreCase("exit")) {
                        System.out.println("CLIENT-2 terminated the chat");

                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "CLIENT-2 terminated the chat");
                            messageInput.setEnabled(false);
                            messageInput.setBackground(Color.LIGHT_GRAY);
                        });
                        socket.close();
                        break;
                    }

                    messageArea.append("CLIENT-2: " + msg + "\n");
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {

        System.out.println("CLIENT-1 is going to start...");
        new Server();
    }
}

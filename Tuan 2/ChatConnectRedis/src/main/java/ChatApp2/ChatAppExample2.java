package ChatApp2;



import redis.clients.jedis.Jedis;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatAppExample2 {
    private static final String Chat2 = "chat_messages2";
    private static final String Chat = "chat_messages";

    public static void main(String[] args) {
        // Tạo một frame Swing
        JFrame frame = new JFrame("Chat App 222222");

        // Tạo một TextArea để hiển thị tin nhắn
        final JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);

        // Tạo một TextField để nhập tin nhắn
        final JTextField messageField = new JTextField();

        // Tạo một nút để gửi tin nhắn
        JButton sendButton = new JButton("Gửi");

        // Kết nối tới Redis server
		final Jedis jedis = new Jedis("redis://default:YFglioJCKlf4EelIRqP3qMRcfuWsKNxi@redis-16916.c279.us-central1-1.gce.cloud.redislabs.com:16916");

        // Lấy các tin nhắn đã lưu từ Redis và hiển thị lên TextArea
        String chatMessages = jedis.get(Chat);
        if (chatMessages != null) {
            chatArea.setText(chatMessages);
        }

        Thread updateThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000); // Đợi 1 giây
                        updateMessage();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void updateMessage() {
				// TODO Auto-generated method stub
            	 String message = messageField.getText();

                 // Kiểm tra xem tin nhắn có giá trị hay không
                 if (!message.isEmpty()) {
                     // Lấy thời gian hiện tại
                     LocalDateTime now = LocalDateTime.now();
                     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                     String timestamp = now.format(formatter);

                     // Tạo định dạng tin nhắn: [Thời gian] Tin nhắn
                     String formattedMessage = "[" + timestamp + "] " + message + "\n";

                     // Hiển thị tin nhắn lên TextArea
                     chatArea.append(formattedMessage);

                     // Lưu tin nhắn xuống Redis
                     jedis.append(Chat2, formattedMessage);

                 
            }
            }
        });
        updateThread.start();
        
        // Thêm ActionListener để xử lý sự kiện khi nút Gửi được nhấn
        sendButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
                // Lấy tin nhắn từ TextField
                String message = messageField.getText();

                // Kiểm tra xem tin nhắn có giá trị hay không
                if (!message.isEmpty()) {
                    // Lấy thời gian hiện tại
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String timestamp = now.format(formatter);

                    // Tạo định dạng tin nhắn: [Thời gian] Tin nhắn
                    String formattedMessage = "[" + timestamp + "] " + message + "\n";

                    // Hiển thị tin nhắn lên TextArea
                    chatArea.append(formattedMessage);

                    // Lưu tin nhắn xuống Redis
                    jedis.append(Chat2, formattedMessage);

                    // Xóa nội dung TextField
                    messageField.setText("");
                }
            }
        });

        // Thêm TextArea, TextField và nút vào frame
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.getContentPane().add(messageField, BorderLayout.SOUTH);
        frame.getContentPane().add(sendButton, BorderLayout.EAST);

        // Cấu hình frame
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Đóng kết nối Redis khi frame được đóng
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                jedis.close();
            }
        });
    }
}


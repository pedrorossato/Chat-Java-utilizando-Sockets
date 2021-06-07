package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Client {
	private String serverAddress;
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	private JFrame frame = new JFrame("Chat");
	private JTextField textField = new JTextField(50);
	private JTextArea messageArea = new JTextArea(16,50);
	
	public Client(String serverAddress) {
		this.serverAddress = serverAddress;
		
		textField.setEditable(false);
		messageArea.setEditable(false);
		frame.getContentPane().add(textField,BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageArea),BorderLayout.CENTER);
		frame.pack();
		
		// Adiciona evento de quando enviar mensagem, envia para o writer, para envio por socket outputStream, e limpa a mensagem escrita
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				out.println(textField.getText());
				textField.setText("");
			}
		});
	}
	
	private String getName() {
		return JOptionPane
				.showInputDialog(frame, "Choose a screen name:", "Screen name selection", JOptionPane.PLAIN_MESSAGE);
	}
	
	private void run() throws IOException {
        try {
            socket = new Socket(serverAddress, 59001);
            // In e Out : funcionam como buffers de entrada e saida
			// Quando se usa o scanner, se lê o inputStream do Socket
            in = new Scanner(socket.getInputStream());
            // Quando se usa o printWriter, se escreve no outputStream do Socket
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                var line = in.nextLine();
                // Pedido do servidor para submeter um nome
                if (line.startsWith("SUBMITNAME")) {
                    out.println(getName());
                // Informação do servidor com cliente aceito
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.frame.setTitle("Chatter - " + line.substring(13));
                    textField.setEditable(true);
                // Informacao do servidor com uma nova mensagem 
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        // Instacia novo cliente com janela
        var client = new Client(args[0]);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}

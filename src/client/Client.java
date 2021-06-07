package client;

import comunication.Message;
import comunication.TypeOfMessage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Client {
	private String serverAddress;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
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
				try {
					out.writeObject(new Message(textField.getText()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				textField.setText("");
			}
		});
	}
	
	private String getName() {
		return JOptionPane
				.showInputDialog(frame, "Escolha um nickname:", "Escolha de nickname", JOptionPane.PLAIN_MESSAGE);
	}
	
	private void run() throws IOException, ClassNotFoundException {
        try {
            socket = new Socket(serverAddress, 59001);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            while (true) {
                Message message = (Message)in.readObject();
                // Pedido do servidor para submeter um nome
                if (message.getTipo() == TypeOfMessage.SUBMITNAME) {
                	String client = getName();
                    out.writeObject(new Message(client.toCharArray()));
                // Informação do servidor com cliente aceito
                } else if (message.getTipo() == TypeOfMessage.NAMEACCEPTED) {
                    this.frame.setTitle("Chatter - " + String.valueOf(message.remetente));
                    textField.setEditable(true);
                // Informacao do servidor com uma nova mensagem 
                } else if (message.getTipo() == TypeOfMessage.MESSAGE) {
                    messageArea.append(String.valueOf(message.remetente) + message.mensagem + "\n");
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

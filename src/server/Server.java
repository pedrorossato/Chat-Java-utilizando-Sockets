package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import comunication.Message;
import comunication.TypeOfMessage;

public class Server {
	private static Set<char[]> clients = new HashSet<>();
	private static Set<ObjectOutputStream> writers = new HashSet<>();
	
	public static void main(String[] args) throws Exception {
        System.out.println("O servidor está rodando...");
        // Cria pool de 500 threads para clientes
        var pool = Executors.newFixedThreadPool(500);
        try (var listener = new ServerSocket(59001)) {
        	// Escuta na porta 59001 até aceitar conexao
            while (true) {
            	// Quando aceita, o método execute cria uma nova thread para rodar o handler
            	pool.execute(new Handler(listener.accept()));
            }
        }
    }
	
	private static class Handler implements Runnable {
		private char[] client;
		private Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		
		public Handler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				// In e Out : funcionam como buffers de entrada e saida
				// Quando se usa o scanner, se lê o inputStream do Socket
				in = new ObjectInputStream(socket.getInputStream());
				// Quando se usa o printWriter, se escreve no outputStream do Socket
                out = new ObjectOutputStream(socket.getOutputStream());
                
                while (true) {
                	// Pede informação do nome para o cliente
                    out.writeObject(new Message(TypeOfMessage.SUBMITNAME));
                    // Recebe o nome do cliente
                    Message response = (Message)in.readObject();
                    client = response.remetente;
                    if (client == null) {
                        return;
                    }
                    // Aqui, adiciona-se tratamento de concorrencia, pois pode haver 2 clientes tentando se adicionar à lista de clientes ao mesmo tempo. 
                    synchronized (clients) {
                        if (!(client.length == 0) && !clients.contains(client)) {
                        	clients.add(client);
                        	System.out.println(String.valueOf(client) + " entrou");
                            break;
                        }
                    }
                }
                // Transmite ao cliente que o nome foi aceito e ele foi adicionado
                out.writeObject(new Message(TypeOfMessage.NAMEACCEPTED,client));
                // Broadcast para os clientes, informando que o novo cliente entrou
                for (ObjectOutputStream writer : writers) {
                	writer.writeObject(new Message(TypeOfMessage.MESSAGE,client," entrou no chat"));
                }
                // Adiciona o cliente à lista de streams de clientes, para fazer multicast para todos inclusive o novo cliente
                writers.add(out);
                
                // Multicast das mensagens enviadas pelos clientes
                while (true) {
                    Message input = (Message)in.readObject();
                    if (input.mensagem.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    for (ObjectOutputStream writer : writers) {
                    	writer.writeObject(new Message(TypeOfMessage.MESSAGE,client, String.format(": %s", input.mensagem)));
                    }
                }
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				try {
					if (out != null) {
	                    writers.remove(out);
	                }
	                if (client != null) {
	                    System.out.println(String.valueOf(client) + " saiu");
	                    clients.remove(client);
	                    for (ObjectOutputStream writer : writers) {
	                        writer.writeObject(new Message(TypeOfMessage.MESSAGE,client," deixou o chat"));
	                    }
	                }
                    socket.close();
                } catch (IOException e) {
                }
			}
		}
	}
}

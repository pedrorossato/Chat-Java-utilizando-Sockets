package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;

public class Server {
	private static Set<String> clients = new HashSet<>();
	private static Set<PrintWriter> writers = new HashSet<>();
	
	public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
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
		private String client;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;
		
		public Handler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				// In e Out : funcionam como buffers de entrada e saida
				// Quando se usa o scanner, se lê o inputStream do Socket
				in = new Scanner(socket.getInputStream());
				// Quando se usa o printWriter, se escreve no outputStream do Socket
                out = new PrintWriter(socket.getOutputStream(), true);
                
                while (true) {
                	// Pede informação do nome para o cliente
                    out.println("SUBMITNAME");
                    // Recebe o nome do cliente
                    client = in.nextLine();
                    if (client == null) {
                        return;
                    }
                    // Aqui, adiciona-se tratamento de concorrencia, pois pode haver 2 clientes tentando se adicionar à lista de clientes ao mesmo tempo. 
                    synchronized (clients) {
                        if (!client.isBlank() && !clients.contains(client)) {
                        	clients.add(client);
                            break;
                        }
                    }
                }
                // Transmite ao cliente que o nome foi aceito e ele foi adicionado
                out.println("NAMEACCEPTED " + client);
                // Broadcast para os clientes, informando que o novo cliente entrou
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + client + " has joined");
                }
                // Adiciona o cliente à lista de streams de clientes, para fazer brodcast para todos inclusive o novo cliente
                writers.add(out);
                
                // Broadcast das mensagens enviadas pelos clientes
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + client + ": " + input);
                    }
                }
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				if (out != null) {
                    writers.remove(out);
                }
                if (client != null) {
                    System.out.println(client + " is leaving");
                    clients.remove(client);
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + client + " has left");
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
			}
		}
	}
}

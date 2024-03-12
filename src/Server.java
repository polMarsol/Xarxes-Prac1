import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int port = 1234;

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(port);
            Socket clientSocket = ss.accept();
            System.err.println("Connexió acceptada.");

            // Crear un hilo para manejar la lectura de mensajes del cliente
            new Thread(new ClientReader(clientSocket)).start();

            // Loop para que el servidor pueda enviar mensajes al cliente
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String serverMsg;
            while ((serverMsg = consoleReader.readLine()) != null) {
                writer.println(serverMsg);
                if (serverMsg.equals("FI")) {
                    break;
                }
            }
            writer.close();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientReader implements Runnable {
    private Socket socket;

    public ClientReader(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clientMsg;
            while ((clientMsg = reader.readLine()) != null) {
                System.out.println("Client: <<" + clientMsg + ">>");
                if (clientMsg.equals("FI")) {
                    break;
                }
            }
            reader.close();
            socket.close();
            System.out.println("Connexió tancada.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("\n");
        }
    }
}

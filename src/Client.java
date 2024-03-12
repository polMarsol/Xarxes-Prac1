import java.io.*;
import java.net.Socket;

public class Client {
    private static final int port = 1234;
    private static String host = "127.0.0.1";

    public static void main(String[] args) {
        if (args.length > 0) {
            host = args[0];
        }
        try {
            Socket socket = new Socket(host, port);

            // Crear un hilo para manejar la lectura de mensajes del servidor
            new Thread(new ServerReader(socket)).start();

            // Loop para que el cliente pueda enviar mensajes al servidor
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String clientMsg;
            while ((clientMsg = consoleReader.readLine()) != null) {
                writer.println(clientMsg);
                if (clientMsg.equals("FI")) {
                    break;
                }
            }
            writer.close();
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            System.err.println();
        }
    }
}

class ServerReader implements Runnable {
    private Socket socket;

    public ServerReader(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverMsg;
            while ((serverMsg = reader.readLine()) != null) {
                System.out.println("Server: <<" + serverMsg + ">>");
                if (serverMsg.equals("FI")) {
                    break;
                }
            }
            reader.close();
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            System.out.println("\n");
        }
    }
}

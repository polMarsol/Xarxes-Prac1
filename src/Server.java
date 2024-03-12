import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port = 1234;

        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Server: Esperando conexiones...");

            while (true) {
                Socket s = ss.accept();
                System.out.println("Server: Conexión establecida con un cliente");

                // Thread para leer mensajes del cliente
                Thread readThread = new Thread(() -> {
                    try {
                        InputStream is = s.getInputStream();
                        DataInputStream dis = new DataInputStream(is);

                        while (true) {
                            String message = dis.readUTF();
                            System.out.println("Cliente: " + message);
                        }
                    } catch (IOException e) {
                        System.out.println("Error al leer datos del cliente: " + e.getMessage());
                    }
                });
                readThread.start();

                // Thread para enviar mensajes al cliente
                Thread writeThread = new Thread(() -> {
                    try {
                        OutputStream os = s.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                        while (true) {
                            System.out.print("Server: ");
                            String message = reader.readLine();

                            dos.writeUTF(message);
                            dos.flush();
                        }
                    } catch (IOException e) {
                        System.out.println("Error al enviar datos al cliente: " + e.getMessage());
                    }
                });
                writeThread.start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}

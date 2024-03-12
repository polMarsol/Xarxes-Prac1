import Client.java.UnThread;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        System.out.println("Client: Inicio intento de conexión");
        String host = "127.0.0.1"; // Cambiar a la dirección IP del servidor si es necesario
        int port = 1234;

        try {
            Socket s = new Socket(host, port);

            // Thread para leer mensajes del servidor
            Thread readThread = new Thread(new UnThread(), "Client"){
                try {
                    InputStream is = s.getInputStream();
                    DataInputStream dis = new DataInputStream(is);

                    while (true) {
                        String message = dis.readUTF();
                        System.out.println("Server: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Error al leer datos del servidor: " + e.getMessage());
                }
            });
            readThread.start();

            // Thread para enviar mensajes al servidor
            Thread writeThread = new Thread(() -> {
                try {
                    OutputStream os = s.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                    while (true) {
                        System.out.print("Cliente: ");
                        String message = reader.readLine();

                        dos.writeUTF(message);
                        dos.flush();
                    }
                } catch (IOException e) {
                    System.out.println("Error al enviar datos al servidor: " + e.getMessage());
                }
            });
            writeThread.start();

            // Esperar a que ambos threads terminen
            readThread.join();
            writeThread.join();

            s.close();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error en la conexión: " + e.getMessage());
        }
    }
}

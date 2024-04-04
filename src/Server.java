//Feta en grup --> AUTORS: Hugo Fernandez Sisquella i Pol Marsol Torras

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int port = 1234;
    private static boolean clientConnectat = false; // Variable per controlar si ja hi ha un client connectat

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(port);

            while (true) {
                Socket s = ss.accept(); // Acceptar la connexió del client

                if (!clientConnectat) { // Si no hi ha cap client connectat
                    clientConnectat = true; // Indicar que ara hi ha un client connectat

                    // Iniciar fils per gestionar la comunicació amb el client
                    Thread tW = new Thread(new threadServerW(s));
                    Thread tR = new Thread(new threadServerR(s));
                    System.err.println("Connexió acceptada.");

                    tR.start(); // Iniciar-los
                    tW.start();
                } else {
                    // Si ja hi ha un client connectat, enviar un missatge indicant que el servidor no està disponible
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    dos.writeUTF("Servidor no disponible");
                    dos.flush();
                    s.close(); // Tancar la connexió amb el client
                }
            }
        } catch (IOException e) {
            System.err.println("Servidor no disponible. Ja està en ús.");
        }
    }

    // Thread per escoltar al client
    private static class threadServerR implements Runnable {
        private final Socket s;

        public threadServerR(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                String str;

                while (true) {
                    str = dis.readUTF(); // Llegir el missatge del client
                    if (str.equals("FI")) { // Si el client envia "FI", sortir del bucle
                        System.out.println("Client: <<" + str + ">>");
                        break;
                    }
                    System.out.println("Client: <<" + str + ">>");
                    dos.flush();
                }
                dis.close();
                dos.close();
                s.close();
                System.exit(0); // Sortir del programa
            } catch (IOException e) {
                System.out.println("Connexió tancada.");
                System.exit(0); // Sortir del programa
            }
        }
    }

    // Thread per enviar dades al client
    private static class threadServerW implements Runnable {
        private final Socket s;

        public threadServerW(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF("Connexió acceptada."); // Enviar missatge de connexió acceptada al client, tal
                // i com hi ha en els exemples de la pràctica.
                InputStream consola = System.in;
                BufferedReader d = new BufferedReader(new InputStreamReader(consola));
                String str = "";

                while (!str.equals("FI")) { // Mentre no es rebi "FI" fer:
                    str = d.readLine().trim(); // Llegir la entrada de la consola tornant a netejar els espais inicials
                    // i finals.
                    if (!str.trim().isEmpty()) {
                        dos.writeUTF(str); // Enviar el missatge al client si hi té contingut
                    }
                    dos.flush();//Per esborrar el buffer i que no s'acumulin els missatges i es puguin enviar
                    // immediatament.
                }
                dis.close();
                dos.close();
                s.close();

            } catch (Exception e) {
                System.exit(0); // Sortir del programa
            }
        }
    }
}

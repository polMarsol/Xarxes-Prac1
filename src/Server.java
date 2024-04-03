import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private static final int port = 1234;


    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(port);

            // Accept only one client
            Socket s = ss.accept();
                Thread tW = new Thread(new threadServerW(s));
                Thread tR = new Thread(new threadServerR(s));
                System.err.println("Connexió acceptada.");

                tR.start();
                tW.start();
                tR.join();
                tW.join();

                s.close();
        } catch (IOException | InterruptedException e) {
            System.err.println("Servidor no disponible. Ja està en ús.");
        }
    }
    private static class threadServerR implements Runnable {
        private final Socket s;

        public threadServerR(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                String str = "";

                while (!str.equals("FI")) {
                    str = dis.readUTF();
                    System.out.println("Client: <<" + str + ">>");
                    dos.flush();
                }
                dis.close();
                dos.close();
                s.close();

                System.exit(0);
            } catch (IOException e) {
                System.out.println("Connexió tancada.");
                System.exit(0);
            }
        }
    }
    private static class threadServerW implements Runnable {
        private final Socket s;

        public threadServerW(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF("Connexió acceptada.");
                InputStream consola = System.in;
                BufferedReader d = new BufferedReader(new InputStreamReader(consola));
                String str = "";

                while (!str.equals("FI")) {
                    str = d.readLine().trim();
                    if (!str.trim().isEmpty()) { ///////////////MIRAR
                        dos.writeUTF(str);
                    }
                    dos.flush();
                }
                dis.close();
                dos.close();
                s.close();

            } catch (IOException e) {
                System.err.println("Writing error: "+ e.getMessage());
            }
        }
    }
}

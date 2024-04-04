import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int port = 1234;
    private static boolean clientConnectat = false;

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(port);

            while (true) {
                Socket s = ss.accept();
                if (!clientConnectat) {
                    clientConnectat = true;

                    Thread tW = new Thread(new threadServerW(s));
                    Thread tR = new Thread(new threadServerR(s));
                    System.err.println("Connexió acceptada.");

                    tR.start();
                    tW.start();
                } else {
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    dos.writeUTF("Servidor no disponible");
                    dos.flush();
                    s.close();
                }
            }
        } catch (IOException e) {
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
                String str;

                while (true) {
                    str = dis.readUTF();
                    if (str.equals("FI")) {
                        System.out.println("Client: <<" + str + ">>");
                        break;
                    }
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
                    if (!str.trim().isEmpty()) {
                        dos.writeUTF(str);
                    }
                    dos.flush();
                }
                dis.close();
                dos.close();
                s.close();

            } catch (Exception e) {
                System.exit(0);
            }
        }
    }
}

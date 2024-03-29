import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private static final int port = 1234;
    private static final AtomicBoolean isTerminated = new AtomicBoolean(false);


    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(port);

            // Accept only one client
            Socket s = ss.accept();
                Thread tW = new Thread(new threadServerW(s));
                Thread tR = new Thread(new threadServerR(s));
                System.err.println("Connexió acceptada.");
                tW.start();
                tR.start();
                tW.join();
                tR.join();
                System.err.println("Connexió tancada.");
                s.close();
                System.exit(0);
        } catch (IOException | InterruptedException e) {
            System.err.println("Server not available");
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
                InputStream consola = System.in;
                BufferedReader d = new BufferedReader(new InputStreamReader(consola));
                String str = "";

                while (!isTerminated.get() && !str.equals("FI")) {
                    str = d.readLine();
                    if (!str.isEmpty()) {
                        dos.writeUTF(str);
                    }
                    dos.flush();
                }
                dis.close();
                dos.close();
                s.close();
                System.err.println("Connexió tancada.");
                System.exit(0);
            } catch (IOException e) {
                /*System.err.println("Writing error: "+ e.getMessage());*/
            }
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

                while (!isTerminated.get() && !str.equals("FI")) {
                    str = dis.readUTF();
                    System.out.println("Client: \"" + str + "\"");
                    dos.flush();
                }
                dis.close();
                dos.close();
                s.close();
                System.err.println("Connexió tancada.");
                System.exit(0);
            } catch (IOException e) {
                /*System.err.println(e.getMessage());*/
            }
        }
    }
}

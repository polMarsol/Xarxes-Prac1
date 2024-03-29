import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private static final int port = 1234;
    private static String host = "127.0.0.1";
    private static final AtomicBoolean isTerminated = new AtomicBoolean(false);
    private static final AtomicBoolean isNotAvailable = new AtomicBoolean(false);


    public static void main(String[] args) {
        if (args.length > 0) {
            host = args[0];
        }
        try {
            if(!isNotAvailable.get()) {
                Socket socket = new Socket(host, port);
                System.err.println("Connexió acceptada.");
                isNotAvailable.set(true);
                Thread tR = new Thread(new threadClientR(socket));
                Thread tW = new Thread(new threadClientW(socket));
                tR.start();
                tW.start();
                tR.join();
                tW.join();

                System.err.println("Connexió tancada.");
                socket.close();
                System.exit(0);
            } else {
                System.err.println("Error, server ocupat");
                System.exit(1);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Connection refused.");
        }
    }

    private static class threadClientR implements Runnable {
        Socket s;

        private threadClientR(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                String str = "";

                while (!isTerminated.get() && !str.equals("FI")) {
                    str = dis.readUTF();
                    System.out.println("Server: \"" + str + "\"");
                    dos.flush();
                }

                dos.close();
                dis.close();
                s.close();
                System.err.println("Connexió tancada.");
                System.exit(0);
            } catch (IOException e) {

            }
        }
    }

    private static class threadClientW implements Runnable {
        Socket s;

        private threadClientW(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                InputStream consola = System.in;
                BufferedReader d = new BufferedReader(new InputStreamReader(consola));
                String entrada = "";
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                DataInputStream dis = new DataInputStream(s.getInputStream());
                while (!isTerminated.get() && !entrada.equals("FI")) {
                    entrada = d.readLine();
                    if (!entrada.isEmpty()) {
                        dos.writeUTF(entrada);
                    }
                    dos.flush();
                }
                dos.close();
                dis.close();
                s.close();
            } catch (IOException e) {

            }
        }
    }
}

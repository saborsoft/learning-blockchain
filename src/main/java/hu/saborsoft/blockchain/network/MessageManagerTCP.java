package hu.saborsoft.blockchain.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessageManagerTCP extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean forever = true;
    private String friendName;

    public MessageManagerTCP(Socket socket, String friendName) throws IOException {
        //In TCP socket, you must create the outputstream first
        //then the inputstream. The order is critical
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.friendName = friendName;
    }

    public void sendMessage(String mesg) {
        try {
            this.out.writeObject(mesg);
        } catch (IOException ioe) {
            System.out.println("Error: writing message runs into exception.");
            ioe.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Message manager is up ...");
        while (forever) {
            try {
                String m = (String) (this.in.readObject());
                System.out.println(this.friendName + "]: " + m);
                if (m.trim().startsWith("END")) {
                    forever = false;
                }
            } catch (Exception e) {
                System.out.println("Error: This is only for text messaging.");
                System.exit(1);
            }
        }
        System.out.println("message manager retired");
        System.exit(1);
    }
}

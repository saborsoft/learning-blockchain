package hu.saborsoft.blockchain.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;


public class TestUDPServer {
    private int serverPort;
    private DatagramSocket serverUDPSocket;
    private boolean forever = true;
    private Scanner in;

    public TestUDPServer(int serverPort) {
        this.serverPort = serverPort;
        this.in = new Scanner(System.in);
        try {
            this.serverUDPSocket = new DatagramSocket(this.serverPort);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        in = new Scanner(System.in);

    }

    public void start() {
        System.out.println("UDP server starts at port " + this.serverPort);
        while (forever) {
            byte[] buf = new byte[2048];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            try {
                System.out.println("server listening to incoming messages ...");
                serverUDPSocket.receive(dp);
                System.out.println("client]: port=" + dp.getPort() + ", IP=" + dp.getAddress().getHostAddress());
                String r = (new String(dp.getData())).trim();
                if (r.startsWith("END")) {
                    System.out.println("ENDing now ...");
                    forever = false;
                    in.close();
                    continue;
                } else {
                    System.out.println("client]: " + r);
                }
                System.out.println("Please type your response below:");
                String m = in.nextLine();
                DatagramPacket rp = new DatagramPacket(m.getBytes(), m.getBytes().length,
                        dp.getAddress(), dp.getPort());
                serverUDPSocket.send(rp);
                if (m.startsWith("END")) {
                    forever = false;
                }
            } catch (IOException e) {
                forever = false;
                throw new RuntimeException(e);
            }
        }
        this.serverUDPSocket.close();
    }

    public static void main(String[] args) {
        TestUDPServer server = new TestUDPServer(8888);
        server.start();
        System.out.println("========= Server Ended ==========");
    }
}

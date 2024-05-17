package hu.saborsoft.blockchain.network;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

/**
 * Only necessary if the program stops working unexpected and the port is remained open.
 * <p>
 * https://stackoverflow.com/questions/8688949/how-to-close-tcp-and-udp-ports-via-windows-command-line
 * <p>
 * find the port
 * netstat -ano | findstr :8888
 * <p>
 * kill the process
 * taskkill /pid [PID_ID] /F
 */
public class TestUDPClient {
    private int serverPort;
    private InetAddress serverAddress;
    private boolean forever = true;
    private Scanner in;
    private DatagramSocket clientSocket;

    public TestUDPClient(int serverPort, String serverAddress) {
        this.serverPort = serverPort;
        in = new Scanner(System.in);
        try {
            this.serverAddress = InetAddress.getByName(serverAddress);
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException uhe) {
            throw new RuntimeException(uhe);
        }
    }

    public void start() throws IOException {
        while (forever) {
            System.out.println("Please type your message to be sent: ");
            String mesg = in.nextLine();
            //generate a sending datagram
            DatagramPacket dp = new DatagramPacket(mesg.getBytes(), mesg.length(), this.serverAddress, serverPort);
            //send the datagram
            this.clientSocket.send(dp);
            if (mesg.startsWith("END")) {
                forever = false;
                continue;
            }
            //now wait for a message
            byte[] data = new byte[2048];
            DatagramPacket rp = new DatagramPacket(data, data.length);
            this.clientSocket.receive(rp);
            System.out.println("client received a message from the server:");
            System.out.println("\tserver address:" + rp.getAddress().getHostName() + ", port=" + rp.getPort());
            String m = new String(rp.getData()).trim();
            if (m.startsWith("END")) {
                System.out.println("== ENDing now ==");
                forever = false;
                in.close();
            } else {
                System.out.println("server]: " + m);
            }
        }
        this.clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("What is the IP address of the server?:");
        Scanner in = new Scanner(System.in);
        String ip = in.nextLine();
        if (ip.trim().length() < 5) {
            ip = "localhost";
        }
        System.out.println("IP: " + ip);
        TestUDPClient client = new TestUDPClient(8888, ip);
        System.out.println("UDP client starts now, server is listening at port 8888");
        client.start();
        in.close();
        System.out.println("===== Client Stopped =============");
    }
}

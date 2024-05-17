package hu.saborsoft.blockchain.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TestTCPClient {
    private static int port = 8888;

    public static void main(String[] args) throws IOException {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Please enter the server's IP address");
        String ip = keyboard.nextLine();
        Socket socket = new Socket(ip, port);
        System.out.println("What is your friend's name");
        String name = keyboard.nextLine();
        //cannot close the scanner, it is important, because if you close it
        //you shutdown the System.in
        //keyboard.close();
        System.out.println("connected, ready to go");
        MessageManagerTCP manager = new MessageManagerTCP(socket, name);
        manager.start();
        System.out.println("manager started");
        CommunicationChannel channel = new CommunicationChannel(manager);
        channel.start();
        System.out.println("channel ready for you");
    }
}

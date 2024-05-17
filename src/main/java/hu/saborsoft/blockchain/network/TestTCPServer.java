package hu.saborsoft.blockchain.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TestTCPServer {
    public static final int port = 8888;

    public static void main(String[] args) throws IOException {
        System.out.println("What you want to call your friend?");
        Scanner in = new Scanner(System.in);
        String friendName = in.nextLine();
        ServerSocket server = new ServerSocket(port);
        System.out.println("server is listening now");
        Socket socket = server.accept();
        System.out.println("Connected, start chat!");
        MessageManagerTCP messageManager = new MessageManagerTCP(socket, friendName);
        messageManager.start();
        System.out.println("message manager started");
        CommunicationChannel channel = new CommunicationChannel(messageManager);
        channel.start();
        System.out.println("communication channel is ready");
        server.close();
    }
}

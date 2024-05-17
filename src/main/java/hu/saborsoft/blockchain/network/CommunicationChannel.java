package hu.saborsoft.blockchain.network;

import java.util.Scanner;

public class CommunicationChannel extends Thread {
    private Scanner in;
    private MessageManagerTCP messageManager;
    private boolean forever = true;

    public CommunicationChannel(MessageManagerTCP messageManager) {
        this.messageManager = messageManager;
        in = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Communication channel is up, please type below:");
        while (forever) {
            try {
                String mesg = in.nextLine();
                messageManager.sendMessage(mesg);
                if (mesg.trim().startsWith("END")) {
                    forever = false;
                }
            } catch (Exception e) {
                forever = false;
            }
        }
        System.out.println("Channel closed");
        System.exit(1);
    }
}

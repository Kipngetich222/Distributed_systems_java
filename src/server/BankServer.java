package server;

import common.Account;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.InetAddress;

public class BankServer {
    public static void main(String[] args) {
        try {
            // Get server IP dynamically
            String serverIP = InetAddress.getLocalHost().getHostAddress(); 
            int port = 3000; // RMI registry port

            // Check if RMI registry is already running, otherwise create it
            try {
                Registry registry = LocateRegistry.getRegistry(port);
                registry.list(); // Check if it responds
                System.out.println("RMI Registry already running on port " + port);
            } catch (Exception e) {
                LocateRegistry.createRegistry(port);
                System.out.println("RMI Registry started on port " + port);
            }

            // Create an Account object and bind it to the RMI registry
            Account account = new AccountImpl("123456789");
            String bindLocation = "rmi://" + serverIP + ":" + port + "/BankAccount";
            Naming.rebind(bindLocation, account);

            System.out.println("Server started at: " + bindLocation);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Server failed to start!");
        }
    }
}

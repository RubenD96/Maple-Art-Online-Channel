package net.server;

import constants.ServerConstants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

@RequiredArgsConstructor
public class LoginConnector extends Thread {

    private @NonNull final Server server;
    private @NonNull final ChannelServer channel;

    @Override
    public void run() {
        connect();
    }

    public void connect() {
        try {
            Socket socket = new Socket(ServerConstants.IP, 8888);

            System.out.println("Connected to login server");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        } catch (IOException ioe) {
            //ioe.printStackTrace();
            try {
                sleep(1000);
                connect();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    private static class ReadThread extends Thread {

        private BufferedReader reader;
        private final Socket socket;
        private final LoginConnector connector;

        public ReadThread(Socket socket, LoginConnector connector) {
            this.socket = socket;
            this.connector = connector;

            try {
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
            } catch (IOException ioe) {
                System.out.println("Error getting input stream: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        }

        public void run() {
            while (true) {
                try {
                    String response = reader.readLine();
                    System.out.println("Login server (" + connector.channel.getPort() + "): " + response);
                    String[] splitted = response.split(":");
                    switch (Integer.parseInt(splitted[0])) {
                        case 1: // SelectWorldHandler
                            if (connector.server.getClients().get(Integer.parseInt(splitted[2])) == null) {
                                connector.server.getClients().put(Integer.parseInt(splitted[2]), new MigrateInfo(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), splitted[1]));
                            }
                            break;
                    }
                } catch (IOException ioe) {
                    System.out.println("Error reading from server: " + ioe.getMessage());
                    //ioe.printStackTrace();
                    break;
                }
            }
            connector.connect();
        }
    }

    private static class WriteThread extends Thread {
        private PrintWriter writer;
        private Socket socket;
        private LoginConnector connector;

        public WriteThread(Socket socket, LoginConnector connector) {
            this.socket = socket;
            this.connector = connector;

            try {
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
            } catch (IOException ex) {
                System.out.println("Error getting output stream: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        public void run() {
            Scanner scanner = new Scanner(System.in);
            String test;

            do {
                test = scanner.nextLine();
                writer.println(test);
            } while (!test.equals("quit"));

            try {
                socket.close();
            } catch (IOException ioe) {
                System.out.println("Error writing to server: " + ioe.getMessage());
            }
        }
    }
}

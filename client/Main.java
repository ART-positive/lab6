package client;

import client.connection.ClientConnection;
import client.core.Invoker;
import client.core.printers.CLIPrinter;
import shared.core.exceptions.ConnectionException;
import shared.interfaces.IPrinter;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.logging.Level;

public class Main {
    private static final int DEFAULT_PORT = 2222;

    private static final String HOST_NAME = "127.0.0.1";

    private static final int PORT_INDEX = 0;

    public static void main(String ... args){
        int port;
        try{
            if (args.length == 0){
                port = DEFAULT_PORT;
                //System.err.println("Expected 1 argument, received 0");
                //return;
            }
            else {
                port = Integer.parseInt(args[PORT_INDEX]);
                if (port <= 1023) {
                    System.err.println("Не удается запустить клиента на этом порту!");
                    return;
                }
            }
        }
        catch (NumberFormatException exception){
            System.err.println("Порт в неправильном формате. Ожидается целое число.");
            return;
        }
        ClientConnection connection = new ClientConnection(HOST_NAME, port);
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        IPrinter printer = new CLIPrinter();
        ServerListenerThread serverListenerThread = new ServerListenerThread(pipedOutputStream, connection, printer);
        try{
            Invoker invoker = new Invoker(printer, pipedOutputStream, connection);
            serverListenerThread.setDaemonThread(true);
            serverListenerThread.startThread();
            invoker.startListening();
        }
        catch (IOException exception){
            printer.print("Не удается создать поток!");
        }
        catch (ConnectionException exception){
            printer.print(exception.getMessage());
        }
        catch (NoSuchElementException ex){
            printer.print("Ошибка : " + ex);
        }
        catch (Exception exception){
            printer.print("Фатальная ошибка!\n" + exception);
        }
    }
}

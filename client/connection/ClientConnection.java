package client.connection;

import client.connection.interfaces.IClientConnection;
import client.connection.interfaces.IMessageSender;
import shared.connection.interfaces.IRequest;
import client.core.Invoker;
import shared.connection.requests.CommandRequest;
import shared.connection.requests.ValidationRequest;
import shared.core.exceptions.ConnectionException;
import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The class contains the logic of creating a connection and managing it.
 */
public class ClientConnection implements IClientConnection {

    private InetSocketAddress address;

    private IMessageSender sender;

    private final int RECONNECT_TIME_LIMIT = 10; //Seconds

    private final int THREAD_SLEEP_TIME = 200; //Interval between server connect requests.

    private Socket socket;

    private Invoker invoker;

    /**
     * Creates a class object ready for connection
     * @param ip Host Name
     * @param port Port number
     */
    public ClientConnection(String ip, int port){
        address = new InetSocketAddress(ip,port);
    }

    /**
     * Creates a connection with the server
     * @param invoker
     * @return Returns if a connection was created
     * @throws ConnectionException Are thrown out in case of connection error
     */
    public boolean connect(Invoker invoker) throws ConnectionException {
        try{
             socket = new Socket(address.getHostName(), address.getPort());
             this.invoker = invoker;
             invoker.getPrinter().print("Соединение создано!");
             sender = new TCPSender(socket, invoker.getPrinter());
             return true;
        }
        catch (UnknownHostException exception){
            invoker.getPrinter().print("Неизвестное имя хоста!");
        }
        catch (IOException exception){
            throw new ConnectionException();
        }
        return false;
    }

    /**
     * Reads the response from the input stream.
     * @return Server response
     * @throws ConnectionException
     */
    @Override
    public IRequest getResponse() throws ConnectionException {
        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            IRequest request = (IRequest) objectInputStream.readObject();
            return request;
        }
        catch (ClassNotFoundException exception){
            invoker.getPrinter().print("Получен неизвестный класс!");
        }
        catch (IOException exception){
            invoker.getPrinter().print("Не удалось установить соединение!");
            tryReconnect();
        }
        return new ValidationRequest(null,null);
    }

    private void tryReconnect() {
        long startTime = System.currentTimeMillis();
        invoker.getPrinter().print("Переподключение...");
        try {
            socket.close();
        }
        catch (IOException ex){
            System.out.println(ex );
        }
        while ((System.currentTimeMillis() - startTime)/1000 < RECONNECT_TIME_LIMIT){
            try {
                Thread.sleep(THREAD_SLEEP_TIME);
                if(connect(invoker)){
                    return;
                }
            }
            catch (ConnectionException | InterruptedException exception){
                invoker.getPrinter().print("Прерыввание потока");
            }
        }
        invoker.getPrinter().print("Повторное подключение не удалось!");
        System.exit(1);

    }

    @Override
    public boolean isConnected(){
        return socket.isClosed();
    }

    public IMessageSender getSender(){
        return sender;
    }
}

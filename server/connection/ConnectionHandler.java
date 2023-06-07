package server.connection;

import server.core.Invoker;
import server.core.printers.SocketPrinter;
import server.commands.Command;
import server.connection.interfaces.IServerConnection;
import shared.connection.interfaces.IRequest;
import shared.connection.requests.CommandRequest;
import shared.connection.requests.PingRequest;
import shared.connection.requests.ValidationRequest;
import server.core.managers.CommandsManager;
import server.core.managers.ModelsManager;

import javax.swing.undo.AbstractUndoableEdit;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class contains logic for creating and managing a connection.
 */
public class ConnectionHandler implements IServerConnection {

    /**
     * Read buffer size in bytes.
     */
    private final int BUFFER_CAPACITY = 4096;

    private static final Logger logger = Logger.getLogger(ConnectionHandler.class.getName());

    private Selector selector;

    private final InetSocketAddress address;

    /**
     * Stores information about which channel handles each of the threads.
     */
    private Map<String, SocketChannel> channels;

    private final Invoker invoker;

    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

    /**
     * Creates a connection object, and also creates an Invoker object
     * @param host
     * @param port
     * @param modelsManager
     */
    public ConnectionHandler(String host, int port, ModelsManager modelsManager) {
        address = new InetSocketAddress(host, port);
        channels = new HashMap<>();
        CommandsManager commandsManager = new CommandsManager();
        this.invoker = new Invoker(new SocketPrinter(this), modelsManager, commandsManager);
        commandsManager.initializeCommands(invoker);
        invoker.setConnection(this);
    }

    /**
     * Waiting for the creation of a connection or request from clients.
     */
    public void waitConnection(){
        try{
            logger.log(Level.INFO, "Ожидание соединения...");
            selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(address);
            logger.log(Level.INFO, "Сервер запущен на порту: {0}", address.getPort());
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();
                checkSelectedKeys(keyIterator);
            }

        }
        catch (AlreadyBoundException exception){
            logger.log(Level.WARNING, "Соединение уже связано. ", exception);
        }
        catch (UnsupportedAddressTypeException exception){
            logger.log(Level.WARNING, "Неподдерживаемый адрес. ", exception);
        }
        catch (IOException exception){
            logger.log(Level.WARNING, "Ошибка подключения: ", exception);
        }
    }

    private void checkSelectedKeys(Iterator<SelectionKey> keyIterator){
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isValid()) {
                if (key.isAcceptable()) {
                    createConnection(key);
                    continue;
                }
                if (key.isReadable() && channelCheck((SocketChannel) key.channel())) {
                    read(key);
                }
            }
        }
    }

    /**
     * Reads data from the transmitted channel.
     * @param key
     */
    private void read(SelectionKey key){
        try{
            buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            SocketChannel channel = (SocketChannel) key.channel();
            if(!channelCheck(channel) || channel.read(buffer) == 0 || !checkStreamHeader(buffer.array())){//If channel buffer hasn't got new data.
                return;
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            IRequest request = (IRequest) objectInputStream.readObject();
            logger.log(Level.INFO, "Получен новый запрос от: {0}", channel);
            channels.put(Thread.currentThread().getName(), (SocketChannel) key.channel());
            executeRequest(request, invoker);
        }
        catch (SocketException exception){
            logger.log(Level.WARNING, "Сброс соединения во время чтения.");
        }
        catch (ClassNotFoundException exception){
            logger.log(Level.WARNING, "Неизвестный класс в запросе.");
        }
        catch (IOException exception){
            logger.log(Level.SEVERE, "Исключение при чтении из канала.", exception);
        }
    }

    private boolean checkStreamHeader(byte[] buffer){
        int zeroCounter = 0;
        for (int i = 0; i<8; i++){
            if (buffer[i] == 0) zeroCounter++;
        }
        if (zeroCounter == 8){
            return false;
        }
        return true;
    }

    /**
     *Creates a new SocketChannel.
     * @param key Key with ServerSocketChannel.
     */
    private void createConnection(SelectionKey key){
        try{
            SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
            if (channel == null) return;
            logger.log(Level.INFO, "Соединение получено: {0}", channel);
            channel.configureBlocking(false);

            channel.register(selector, SelectionKey.OP_READ + SelectionKey.OP_WRITE);
        }
        catch (IOException exception){
            logger.log(Level.SEVERE, "Исключение при создании соединения.", exception);
        }
    }

    /**
     * Определяет тип запроса и выполняет его.
     * @param request
     * @param invoker
     */
    private void executeRequest(IRequest request, Invoker invoker){
        CommandsManager commandsManager = invoker.getCommandsManager();
        if (request instanceof CommandRequest){
            logger.log(Level.INFO, "Запрос новой команды: {0}", request);
            CommandRequest commandRequest = (CommandRequest)request;
            Command command = commandsManager.getCommand(commandRequest.getCommand(), invoker.getPrinter());
            invoker.invokeCommand(command, commandRequest.getData());
            return;
        }
        if(request instanceof ValidationRequest){
            logger.log(Level.INFO, "Новый запрос на валидацию: {0}", request);
            ValidationRequest validationRequest = (ValidationRequest) request;
            Command command = commandsManager.getCommand(validationRequest.getCommand(), invoker.getPrinter());
            if (command!=null){
                command.validate(validationRequest.getData());
            }
        }

    }

    /**
     * Writes data to the stream of the specified SelectionKey.
     * @param key
     * @param data
     * @throws IOException
     */
    private void write(SelectionKey key, IRequest data) throws IOException{
        byte[] buffer;
        SocketChannel channel = (SocketChannel) key.channel();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(data);
        buffer = new byte[byteArrayOutputStream.size()];
        channel.write(ByteBuffer.wrap(buffer).put(byteArrayOutputStream.toByteArray()).flip());
    }

    /**
     * Sends the transmitted object.
     * As a channel for sending a response, it takes out the channel from Max connections that is currently processing the thread.
     * @param data
     */
    @Override
    public void send(IRequest data) {
        boolean sent = false;
        if (!channels.containsKey(Thread.currentThread().getName())){
            logger.log(Level.INFO, "Невозможно отправить данные!");
            return;
        }
        SocketChannel channel = channels.get(Thread.currentThread().getName());
        try{
            while (!sent) {
                if (channelCheck(channel)){
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = keys.iterator();
                    while (keyIterator.hasNext()){
                        SelectionKey key = keyIterator.next();
                        if (key.channel().equals(channel) && channelCheck((SocketChannel) key.channel())){
                            logger.log(Level.INFO, "Отправка запроса: {0}", data);
                            write(key, data);
                            sent = true;
                        }
                    }
                }
            }
        }
        catch (IOException exception){
            logger.log(Level.WARNING, "Исключение при отправке данных: ", exception);
        }

    }

    /**
     * Checks the validity of the channel.
     * @param channel
     * @return
     */
    private boolean channelCheck(SocketChannel channel){
        try{
            write(channel.keyFor(selector), new PingRequest());
            return true;
        }
        catch (IOException exception){
            logger.log(Level.WARNING, "Не удается получить доступ к клиенту. ");
            channels.remove(Thread.currentThread().getName());
            try{
                logger.log(Level.INFO, "Закрытие канала: {0}", channel);
                channel.close();
                logger.log(Level.INFO, "Канал закрыт", channel);
            }
            catch (IOException ex){
                logger.log(Level.WARNING, "Не удается закрыть канал с клиентом", channel);
            }
            return false;
        }
    }
    public Invoker getInvoker(){
        return invoker;
    }
}

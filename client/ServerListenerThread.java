package client;

import client.connection.interfaces.IClientConnection;
import shared.connection.interfaces.IRequest;
import shared.connection.requests.CommandRequest;
import shared.connection.requests.MessageRequest;
import shared.connection.requests.ValidationRequest;
import shared.core.exceptions.ConnectionException;
import shared.interfaces.IPrinter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PipedOutputStream;

/**
 * Потоковый класс для фоновой прослушки сервера.
 */
public class ServerListenerThread{

    private PipedOutputStream pipedOutputStream;

    private IClientConnection connection;

    private IPrinter printer;

    private Thread thread;

    /**
     * Поток для передачи данных в основной поток.
     * @param stream
     * @param connection
     * @param printer
     */
    public ServerListenerThread(PipedOutputStream stream, IClientConnection connection, IPrinter printer){
        pipedOutputStream = stream;
        this.connection = connection;
        this.printer = printer;
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                startListening();
            }
        });
    }



    public void run(){
        thread.run();
    }


    public void setDaemonThread(boolean bool) {
        thread.setDaemon(bool);
    }

    public void startThread() {
        thread.start();
    }

    public void startListening(){
        while(!thread.isInterrupted()){
        //while(!thread.isInterrupted()){
            try{
                IRequest response = connection.getResponse();
                if (response instanceof MessageRequest){
                    handleMessageRequest(response);
                }
                else if(response instanceof ValidationRequest){
                    if (response.getData() == null) continue;
                    handleValidationRequest(response);
                }
                else if(response instanceof CommandRequest){
                    handleCommandRequest(response);
                }
            }
            catch (ConnectionException exception){
                printer.print("Ошибка : " + exception);
                return;
            }
        }
    }

    private void handleCommandRequest(IRequest response){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(response);
            pipedOutputStream.write(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            printer.print("Не удается записать командный запрос в piped stream!");
        }
    }

    private void handleValidationRequest(IRequest response){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(response);
            pipedOutputStream.write(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            printer.print("Не удается записать запрос на проверку в piped stream!");
        }
    }

    private void handleMessageRequest(IRequest response){
        printer.print((String)response.getData());
    }
}

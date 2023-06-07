package client.connection;

import client.core.Invoker;
import shared.connection.requests.CommandRequest;
import shared.connection.requests.ValidationRequest;
import shared.interfaces.IPrinter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PipedInputStream;

public class ThreadsBridgeHandler {

    /**
     * Ожидает ответа IValidationRequest от другого потока и возвращает его.
     * @return
     */
    public static boolean getValidationResponse(Invoker invoker){
        try {
            ValidationRequest request = (ValidationRequest) new ObjectInputStream(invoker.getPipedInputStream()).readObject();
            return (boolean)request.getData();
        } catch (IOException e) {
            invoker.getPrinter().print("Не удалось установить соединение!");
        } catch (ClassNotFoundException e) {
            invoker.getPrinter().print("Не удалось разобрать запрос на валидацию!");
        }
        return false;
    }

    public static boolean waitCommandExecuted(Invoker invoker){
        try {
            CommandRequest request = (CommandRequest) new ObjectInputStream(invoker.getPipedInputStream()).readObject();
            return (boolean)request.getData();
        } catch (IOException e) {
            invoker.getPrinter().print("Не удалось установить соединение!");
        } catch (ClassNotFoundException e) {
            invoker.getPrinter().print("Не удалось разобрать запрос на валидацию!");
        }
        return false;
    }
}

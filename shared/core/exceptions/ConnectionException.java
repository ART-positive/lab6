package shared.core.exceptions;

public class ConnectionException extends Exception{
    public ConnectionException(){
        super("Не удалось подключиться к серверу...");
    }
}

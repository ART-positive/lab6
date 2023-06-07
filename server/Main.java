package server;

import server.connection.ConnectionHandler;
import server.core.Invoker;
import server.core.datahandlers.XMLWriter;
import shared.connection.requests.MessageRequest;
import shared.core.exceptions.FileAccessException;
import shared.core.exceptions.FileDoesNotExistException;
import server.core.managers.ModelsManager;
import server.core.validators.FileValidator;
import shared.core.models.MusicBand;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Main {
    public static Logger logger;

    private static Timer timer;

    /**
     * The period of automatic saving of the collection.
     */
    private static final int TIMER_DELAY = 60000;

    private static final int PORT_INDEX = 0;

    /**
     * Localhost address
     */
    private static final String HOST_NAME = "127.0.0.1";

    private static final int DEFAULT_PORT = 2222;



    public static void main(String ... args){
        //configureLogger();
        //logger.log(Level.INFO, "Server is running.");
        int port = checkArgs(args);

        ModelsManager modelsManager = new ModelsManager(new LinkedHashSet<>());
        //modelsManager.setOutFile("data.xml");
        ConnectionHandler connectionHandler = new ConnectionHandler(HOST_NAME , port, modelsManager);
        Invoker invoker = connectionHandler.getInvoker();
        try{
            if (FileValidator.fileCheck("data.xml")){
                modelsManager.setOutFile( new File("data.xml"));
                invoker.getWriter().setFilePath("data.xml");
                invoker.loadData();
            }
        }
        catch (FileAccessException | FileDoesNotExistException exception){

            //logger.log(Level.WARNING, "Exception: ", exception);
            //logger.log(Level.INFO, "Creating own data file...");
            try{
                invoker.getWriter().setFilePath("data.xml");
                File file = new File("data.xml");
                if(file.createNewFile()){
                    //logger.log(Level.INFO, "data.yaml created.");
                }
                else {
                    throw new IOException();
                }
            }
            catch (IOException IOex){
                //logger.log(Level.WARNING, "Can not create new file!", IOex);
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //initializeTimer(invoker);
        //timer.start();
        connectionHandler.waitConnection();
    }
    private static int checkArgs(String ... args){
        int port = 0;
        try{
            if (args.length == 0){
                //logger.log(Level.INFO,"Expected 1 argument, received 0");
                System.err.println("Ожидалось 1 аргумент, получено 0");
                System.exit(1);
            }
            port = Integer.parseInt(args[PORT_INDEX]);
            if(port<=1023){
                //logger.log(Level.INFO, "Can not start server on this port!");
                System.err.println("Не удается запустить сервер на этом порту!");
                System.exit(1);
            }
        }
        catch (NumberFormatException exception){
            //logger.log(Level.WARNING,"Port in the wrong format. Expected Integer.");
            System.err.println("Порт в неправильном формате. Ожидается целое число.");
            System.exit(1);
        }
        return port;
    }
}

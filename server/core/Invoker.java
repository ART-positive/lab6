package server.core;

import server.commands.Command;
import server.connection.interfaces.IServerConnection;
import server.core.datahandlers.XMLReader;
import server.core.datahandlers.XMLWriter;
import server.core.managers.CommandsManager;
import server.core.managers.ModelsManager;
import shared.connection.requests.CommandRequest;
import shared.core.exceptions.*;
import shared.core.models.MusicBand;
import shared.interfaces.IPrinter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class. Contains all the logic for linking all classes of the programme.
 */
public class Invoker {

    private static final Logger logger = Logger.getLogger(Invoker.class.getName());

    private static boolean isDataLoading = false;

    private IPrinter printer;

    private ModelsManager modelsManager;

    private IServerConnection connection;

    private XMLWriter writer;



    private CommandsManager commandsManager;


    public Invoker(IPrinter printer, ModelsManager modelsManager, CommandsManager commandsManager){
        this.printer = printer;
        this.modelsManager = modelsManager;
        this.commandsManager = commandsManager;
        writer = new XMLWriter();
    }

    /**
     * Invoke command logic.
     * @param command command's object.
     * @param arguments command's arguments.
     */
    public void invokeCommand(Command command, Object arguments){
        try{
            if (command != null) {
                printer.print(command.execute(arguments));
                connection.send(new CommandRequest(null, true));
            }
        }
        catch (RecursionException | FileAccessException | CommandParamsException | FileDoesNotExistException |
               ArgumentLimitsException ex){
            logger.log(Level.WARNING, "Что-то пошло не так при работе с командой.", ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method loads data from file
     */
    public void loadData() throws IOException {
        logger.log(Level.INFO,"Началась загрузка данных...");
        isDataLoading = true;
        //LinkedHashSet<MusicBand> hashset = new LinkedHashSet<>();

        XMLReader reader = new XMLReader(); // Initialize parser

        for (MusicBand elem : reader.read(modelsManager.getOutFile())) {
            modelsManager.addModels(elem);
        }
        isDataLoading = false;
        logger.log(Level.INFO, "Загрузка данных завершена.");
    }


    public void setConnection(IServerConnection connection){
        this.connection = connection;
    }

    public static boolean getIsDataLoading(){
        return isDataLoading;
    }

    public IPrinter getPrinter() {
        return printer;
    }


    public ModelsManager getModelsManager() {
        return modelsManager;
    }


    public IServerConnection getConnection() {
        return connection;
    }

    public CommandsManager getCommandsManager(){
        return commandsManager;
    }

    public XMLWriter getWriter(){
        return writer;
    }

}

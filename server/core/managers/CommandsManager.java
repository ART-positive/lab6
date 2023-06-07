package server.core.managers;

import server.commands.*;
import server.core.Invoker;
import shared.commands.commandsdtos.CommandDTO;
import shared.interfaces.IPrinter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains tools for storing, parsing and returning command instances.
 */
public class CommandsManager {
    private static final Logger logger = Logger.getLogger(CommandsManager.class.getName());
    private Boolean isDataCollecting = false;
    private ArrayList<String> data = new ArrayList<>();
    private Map<String, Command> commandsCollection;

    public CommandsManager(){
    }

    /**
     * Return all allowed commands.
     * @return Map object with Command objects.
     */
    public Map<String, Command> getCommandsCollection() {
        return commandsCollection;
    }

    /**
     * Add commands to the HashMap.
     */
    public void initializeCommands(Invoker invoker) {
        commandsCollection = new HashMap<>();
        commandsCollection.put("AddIfMinCommand", new AddIfMinCommand(invoker));
        commandsCollection.put("AddIfMaxCommand", new AddIfMaxCommand(invoker));
        commandsCollection.put("HelpCommand", new HelpCommand(invoker));
        commandsCollection.put("InfoCommand", new InfoCommand(invoker));
        commandsCollection.put("AddCommand", new AddCommand(invoker));
        commandsCollection.put("UpdateCommand", new UpdateCommand(invoker));
        commandsCollection.put("ClearCommand", new ClearCommand(invoker));
        commandsCollection.put("ShowCommand", new ShowCommand(invoker));
        commandsCollection.put("RemoveByIdCommand", new RemoveByIdCommand(invoker));
        commandsCollection.put("ExitCommand", new ExitCommand(invoker));
        commandsCollection.put("PrintFieldAscendingFrontManCommand", new PrintFieldAscendingFrontManCommand(invoker));
        commandsCollection.put("MaxByGenreCommand", new MaxByGenreCommand(invoker));
        commandsCollection.put("RemoveLowerCommand", new RemoveLowerCommand(invoker));
        logger.log(Level.INFO,"Commands initialized.");
    }

    /**
     * Get command from the commands HashMap
     * @param commandDTO
     * @return Command object.
     */
    public Command getCommand(CommandDTO commandDTO, IPrinter printer){
        if (commandsCollection.containsKey(commandDTO.getName())){
            return commandsCollection.get(commandDTO.getName());
        }
        printer.print("Команда не существует!\nВы можете посмотреть \"help\" с полным списком разрешенных команд.");
        return commandsCollection.get("help");
    }
}

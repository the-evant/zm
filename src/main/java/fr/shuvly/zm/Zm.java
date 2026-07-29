package fr.shuvly.zm;

import fr.shuvly.core.paper.PCore;
import fr.shuvly.zm.command.CommandManager;
import fr.shuvly.zm.game.GameManager;
import fr.shuvly.zm.manager.MessageManager;
import fr.shuvly.zm.map.MapManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Zm
    extends JavaPlugin
{

    private static Zm INSTANCE;

    private PCore core;
    private Logger logger;

    private MapManager mapManager;
    private GameManager gameManager;


    @Override
    public void onEnable()
    {
        INSTANCE = this;
        logger = getLogger();

        try {
            this.core = PCore.getInstance();
        } catch (Exception exception) {
            logger.severe("Core failed to load, stopping server...");
            logger.severe(exception.getMessage());
            this.getServer().shutdown();
        }

        this.setupManagers();

        initializeGame();
    }

    @Override
    public void onDisable()
    {
        // ...
    }

    private void setupManagers()
    {
        core.setMessageManager(new MessageManager());

        this.mapManager = new MapManager();
        this.mapManager.loadAvailableMaps();

        new CommandManager(getServer().getPluginManager());
    }

    private void initializeGame()
    {
        this.gameManager = new GameManager();
    }


    public static Zm getInstance() { return INSTANCE; }
    public PCore getCore() { return core; }
    public Logger getPLogger() { return logger; }
    public MapManager getMapManager() { return mapManager; }
    public GameManager getGameManager() { return gameManager; }

}

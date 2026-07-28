package fr.shuvly.zm;

import fr.shuvly.core.paper.PCore;
import fr.shuvly.zm.command.CommandManager;
import fr.shuvly.zm.game.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class Zm
    extends JavaPlugin
{

    private static Zm INSTANCE;

    private PCore core;
    private Logger logger;

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

        new CommandManager(getServer().getPluginManager());

        initializeGame();
    }

    @Override
    public void onDisable()
    {
        // ...
    }

    private void initializeGame()
    {
        this.gameManager = new GameManager();

        File mapFile = new File(getDataFolder(), "maps/zm_dev.yml");

        if (mapFile.exists()) {
            try {
                gameManager.loadMap(mapFile);
            } catch (Exception e) {
                logger.severe("Failed to load map: " + e.getMessage());
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } else {
            logger.warning("No map file found at " + mapFile.getPath());
        }
    }


    public static Zm getInstance() { return INSTANCE; }
    public PCore getCore() { return core; }
    public Logger getPLogger() { return logger; }
    public GameManager getGameManager() { return gameManager; }

}

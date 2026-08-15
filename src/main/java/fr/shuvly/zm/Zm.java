package fr.shuvly.zm;

import fr.shuvly.core.paper.PCore;
import fr.shuvly.zm.command.CommandManager;
import fr.shuvly.zm.game.GameManager;
import fr.shuvly.zm.listener.ComponentInteractionListener;
import fr.shuvly.zm.listener.WeaponInteractionListener;
import fr.shuvly.zm.listener.WeaponReloadListener;
import fr.shuvly.zm.manager.MessageManager;
import fr.shuvly.zm.tablist.TablistManager;
import fr.shuvly.zm.sidebar.ZmSidebarProvider;
import fr.shuvly.zm.map.MapManager;
import fr.shuvly.zm.world.io.WorldFileManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Zm
    extends JavaPlugin
{

    private static Zm INSTANCE;
    private Logger logger;

    private PCore core;

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

        setupManagers();
        setupListeners();
    }

    @Override
    public void onDisable()
    {
        this.gameManager.shutdown();
        WorldFileManager.shutdown();
    }

    private void setupManagers()
    {
        this.mapManager = new MapManager();
        this.mapManager.loadAvailableMaps();

        this.gameManager = new GameManager();

        new CommandManager(getServer().getPluginManager());

        this.core.setTablistManager(new TablistManager());
        this.core.setMessageManager(new MessageManager());
        this.core.setSidebarProvider(new ZmSidebarProvider());

        this.core.getSidebarManager().startTicking(10L);
    }

    private void setupListeners()
    {
        final PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new WeaponInteractionListener(), this);
        pluginManager.registerEvents(new WeaponReloadListener(), this);
        pluginManager.registerEvents(new ComponentInteractionListener(), this);
    }


    public static Zm getInstance() { return INSTANCE; }
    public PCore getCore() { return core; }
    public MapManager getMapManager() { return mapManager; }
    public GameManager getGameManager() { return gameManager; }

}

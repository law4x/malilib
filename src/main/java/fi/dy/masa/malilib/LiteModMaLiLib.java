package fi.dy.masa.malilib;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.ShutdownListener;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.dispatch.InitializationDispatcher;
import fi.dy.masa.malilib.event.dispatch.InitializationDispatcherImpl;

public class LiteModMaLiLib implements Configurable, LiteMod, InitCompleteListener, ShutdownListener
{
    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

    public LiteModMaLiLib()
    {
    }

    @Override
    public String getName()
    {
        return MaLiLibReference.MOD_NAME;
    }

    @Override
    public String getVersion()
    {
        return MaLiLibReference.MOD_VERSION;
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass()
    {
        return MaLiLibConfigPanel.class;
    }

    @Override
    public void init(File configPath)
    {
        InitializationDispatcher.INSTANCE.registerInitializationHandler(new MaLiLibInitHandler());
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader)
    {
        // Dispatch the init calls to all the registered handlers
        ((InitializationDispatcherImpl) InitializationDispatcher.INSTANCE).onGameInitDone();
    }

    @Override
    public void onShutDown()
    {
        ((ConfigManagerImpl) ConfigManager.INSTANCE).saveAllConfigs();
    }
}

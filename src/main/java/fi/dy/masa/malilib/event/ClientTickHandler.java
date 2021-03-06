package fi.dy.masa.malilib.event;

import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.event.dispatch.TickEventDispatcherImpl;

public interface ClientTickHandler
{
    /**
     * Called from the end of the client tick code (for world ticks, not the main game loop/rendering).
     * <br>br>
     * The classes implementing this method should be registered to {@link TickEventDispatcherImpl}
     * @param mc
     */
    void onClientTick(Minecraft mc);
}

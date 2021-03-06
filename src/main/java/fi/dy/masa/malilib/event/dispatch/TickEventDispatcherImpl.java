package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.event.ClientTickHandler;

public class TickEventDispatcherImpl implements TickEventDispatcher
{
    private final List<ClientTickHandler> clientTickHandlers = new ArrayList<>();

    TickEventDispatcherImpl()
    {
    }

    @Override
    public void registerClientTickHandler(ClientTickHandler handler)
    {
        if (this.clientTickHandlers.contains(handler) == false)
        {
            this.clientTickHandlers.add(handler);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onClientTick(Minecraft mc)
    {
        if (this.clientTickHandlers.isEmpty() == false)
        {
            for (ClientTickHandler handler : this.clientTickHandlers)
            {
                handler.onClientTick(mc);
            }
        }
    }
}

package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.BaseScreen;

public class BooleanConfigButton extends GenericButton
{
    private final BooleanConfig config;

    public BooleanConfigButton(int x, int y, int width, int height, BooleanConfig config)
    {
        super(x, y, width, height, "");
        this.config = config;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.config.toggleBooleanValue();
        this.updateDisplayString();

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    protected String generateDisplayString()
    {
        String valueStr = String.valueOf(this.config.getBooleanValue());

        return (this.config.getBooleanValue() ? BaseScreen.TXT_DARK_GREEN : BaseScreen.TXT_DARK_RED) + valueStr + BaseScreen.TXT_RST;
    }
}

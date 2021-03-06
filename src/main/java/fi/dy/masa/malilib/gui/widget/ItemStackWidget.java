package fi.dy.masa.malilib.gui.widget;

import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.render.overlay.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;

public class ItemStackWidget extends BackgroundWidget
{
    protected ItemStack stack;
    protected boolean doHighlight;
    protected int highlightColor;
    protected float scale = 1f;

    public ItemStackWidget(int x, int y, ItemStack stack)
    {
        super(x, y, 16, 16);

        this.setBorderWidth(0);
        this.setBackgroundColor(0xC0C0C0C0);
        this.setBackgroundEnabled(true);

        this.setStack(stack);
    }

    public ItemStackWidget setStack(ItemStack stack)
    {
        this.stack = stack;

        this.updateWidth();
        this.updateHeight();

        return this;
    }

    public ItemStackWidget setDoHighlight(boolean doHighlight)
    {
        this.doHighlight = doHighlight;
        return this;
    }

    public ItemStackWidget setHighlightColor(int color)
    {
        this.highlightColor = color;
        return this;
    }

    public ItemStackWidget setScale(float scale)
    {
        this.scale = scale;
        return this;
    }

    @Override
    public void updateWidth()
    {
        int width = 16;

        if (this.backgroundEnabled)
        {
            width += this.paddingX * 2 + this.borderWidth * 2;
        }

        this.setWidth(width);
    }

    @Override
    public void updateHeight()
    {
        int height = 16;

        if (this.backgroundEnabled)
        {
            height += this.paddingY * 2 + this.borderWidth * 2;
        }

        this.setHeight(height);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        super.render(mouseX, mouseY, isActiveGui, hovered);

        int x = this.getX();
        int y = this.getY();
        int z = this.getZLevel();
        int width = this.getWidth();
        int height = this.getHeight();

        if (this.backgroundEnabled)
        {
            x += this.paddingX + this.borderWidth;
            y += this.paddingY + this.borderWidth;
        }

        if (this.doHighlight && this.isHoveredForRender(mouseX, mouseY))
        {
            RenderUtils.drawRect(x, y, width, height, this.highlightColor, z);
        }

        if (this.stack.isEmpty() == false)
        {
            InventoryOverlay.renderStackAt(this.stack, x, y, z, this.scale, this.mc);
        }
    }
}

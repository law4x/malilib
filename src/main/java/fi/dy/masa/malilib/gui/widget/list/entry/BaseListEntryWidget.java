package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.ContainerWidget;

public class BaseListEntryWidget extends ContainerWidget
{
    protected final int listIndex;
    protected final int originalListIndex;
    protected boolean isOdd;

    public BaseListEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex)
    {
        super(x, y, width, height);

        this.listIndex = listIndex;
        this.originalListIndex = originalListIndex;
    }

    public void setIsOdd(boolean isOdd)
    {
        this.isOdd = isOdd;
    }

    public int getListIndex()
    {
        return this.listIndex;
    }

    /**
     * This gets called from BaseListWidget before the widgets
     * are cleared before being re-created. This allows for example
     * config widgets to save their changes before being destroyed.
     */
    public void onAboutToDestroy()
    {
    }

    /**
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return true;
    }

    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId, boolean selected)
    {
        this.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }
}

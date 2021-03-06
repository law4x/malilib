package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.position.Padding;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.gui.widget.ScrollBarWidget;
import fi.dy.masa.malilib.gui.widget.SearchBarWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class BaseListWidget extends ContainerWidget
{
    @Nullable protected BaseScreen parentScreen;
    @Nullable protected SearchBarWidget searchBarWidget;
    @Nullable protected ContainerWidget headerWidget;
    @Nullable protected EventListener entryRefreshListener;
    protected final ArrayList<BaseListEntryWidget> listWidgets = new ArrayList<>();
    protected final Padding listPosition = new Padding(2, 2, 2, 2);
    protected final ScrollBarWidget scrollBar;

    protected int entryWidgetStartX;
    protected int entryWidgetStartY;
    protected int entryWidgetFixedHeight = 22;
    protected int entryWidgetWidth;
    protected int lastScrollbarPosition;
    protected int listHeight;
    protected int visibleListEntries;

    protected boolean allowKeyboardNavigation;
    protected boolean areEntriesFixedHeight = true;

    public BaseListWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        // The position gets updated in setSize()
        this.scrollBar = new ScrollBarWidget(0, 0, 8, height);
        this.scrollBar.setArrowTextures(BaseIcon.SMALL_ARROW_UP, BaseIcon.SMALL_ARROW_DOWN);
    }

    public abstract int getFilteredListEntryCount();

    @Nullable
    protected abstract BaseListEntryWidget createListEntryWidget(int x, int y, int listIndex);

    protected void createSearchBarWidget()
    {
    }

    /**
     * Creates a header widget, that will be displayed before the first entry of the list.
     */
    protected void createHeaderWidget()
    {
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        this.updatePositioningAndElements();
    }

    @Override
    protected void onSizeChanged()
    {
        this.updatePositioningAndElements();
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        this.updatePositioningAndElements();
    }

    protected void updatePositioningAndElements()
    {
        this.updateEntryWidgetPositioning();
        this.updateSubWidgetsToGeometryChanges();
        this.refreshEntries();
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.scrollBar);

        SearchBarWidget searchBarWidget = this.getSearchBarWidget();

        if (searchBarWidget != null)
        {
            this.addWidget(searchBarWidget);
        }

        if (this.headerWidget != null)
        {
            this.addWidget(this.headerWidget);
        }
    }

    public void initWidget()
    {
        this.clearWidgets();

        this.createSearchBarWidget();
        this.createHeaderWidget();

        this.updateEntryWidgetPositioning();

        this.updateSubWidgetsToGeometryChanges();
        this.reAddSubWidgets();

        this.refreshEntries();

        Keyboard.enableRepeatEvents(true);
    }

    protected void updateEntryWidgetPositioning()
    {
        int leftPadding = this.listPosition.getLeftPadding();
        int rightPadding = this.listPosition.getRightPadding();
        int topPadding = this.listPosition.getTopPadding();
        int bottomPadding = this.listPosition.getBottomPadding();

        SearchBarWidget search = this.getSearchBarWidget();
        ContainerWidget header = this.headerWidget;
        int x = this.getX();
        int y = this.getY();
        int offY = 0;
        if (search != null) { offY += search.getY() - y + search.getHeight(); }
        if (header != null) { offY += header.getHeight(); }

        this.entryWidgetStartX = x + leftPadding;
        this.entryWidgetStartY = y + topPadding + offY;
        int listWidth = this.getListMaxWidthForTotalWidth(this.getWidth());
        this.entryWidgetWidth = x + listWidth - this.entryWidgetStartX - rightPadding - this.scrollBar.getWidth();
        this.listHeight = y + this.getHeight() - this.entryWidgetStartY - bottomPadding;
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int bw = this.borderEnabled ? this.borderWidth : 0;
        int x = this.getX() + bw;
        int y = this.getY() + bw;
        int listWidth = this.getListMaxWidthForTotalWidth(this.getWidth());
        int scrollBarX = x + listWidth - this.scrollBar.getWidth() - bw - 2;
        int scrollBarY = this.entryWidgetStartY;

        this.scrollBar.setPosition(scrollBarX, scrollBarY);
        this.scrollBar.setHeight(this.listHeight);

        SearchBarWidget searchBarWidget = this.getSearchBarWidget();

        if (searchBarWidget != null)
        {
            searchBarWidget.setPosition(x, y + 2);
            searchBarWidget.setWidth(listWidth - bw * 2);
            y = searchBarWidget.getY() + searchBarWidget.getHeight() + 2;
        }

        if (this.headerWidget != null)
        {
            this.headerWidget.setPosition(x, y);
            this.headerWidget.setWidth(this.entryWidgetWidth);
        }
    }

    protected void updateScrollBarHeight()
    {
        final int count = this.getFilteredListEntryCount();
        int totalHeight = 0;

        if (this.visibleListEntries < count)
        {
            // There is no other way than to assume a fixed height here, since all the widgets don't exist at once
            totalHeight += count * this.entryWidgetFixedHeight;
        }
        else
        {
            for (int i = 0; i < count; ++i)
            {
                totalHeight += this.getHeightForListEntryWidget(i);
            }
        }

        this.scrollBar.setTotalHeight(Math.max(totalHeight, this.scrollBar.getHeight()));
    }

    protected int getListMaxWidthForTotalWidth(int width)
    {
        return width;
    }

    public boolean isSearchOpen()
    {
        return this.getSearchBarWidget() != null && this.getSearchBarWidget().isSearchOpen();
    }

    protected boolean hasFilter()
    {
        return this.getSearchBarWidget() != null && this.getSearchBarWidget().hasFilter();
    }

    protected String getFilterText()
    {
        return this.getSearchBarWidget() != null ? this.getSearchBarWidget().getFilter().toLowerCase(Locale.ROOT) : "";
    }

    @Nullable
    public BaseScreen getParentScreen()
    {
        return this.parentScreen;
    }

    public Padding getListPosition()
    {
        return listPosition;
    }

    public ScrollBarWidget getScrollbar()
    {
        return this.scrollBar;
    }

    @Nullable
    public SearchBarWidget getSearchBarWidget()
    {
        return this.searchBarWidget;
    }

    public void setListEntryWidgetFixedHeight(int height)
    {
        this.entryWidgetFixedHeight = height;
    }

    public void setEntryRefreshListener(@Nullable EventListener entryRefreshListener)
    {
        this.entryRefreshListener = entryRefreshListener;
    }

    public BaseListWidget setParentScreen(GuiScreen parent)
    {
        if (parent instanceof BaseScreen)
        {
            this.parentScreen = (BaseScreen) parent;
        }

        return this;
    }

    public void addDefaultSearchBar()
    {
        this.searchBarWidget = new SearchBarWidget(this.getX() + 2, this.getY() + 3,
                                                   this.getWidth() - 14, 14, 0, BaseIcon.SEARCH,
                                                   HorizontalAlignment.LEFT);
    }

    public void onGuiClosed()
    {
        for (BaseListEntryWidget widget : this.listWidgets)
        {
            widget.onAboutToDestroy();
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.onMouseClickedSearchBar(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (this.headerWidget != null && this.headerWidget.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        BaseListEntryWidget hoveredWidget = this.getHoveredListWidget(mouseX, mouseY);

        if (hoveredWidget != null &&
            hoveredWidget.isMouseOver(mouseX, mouseY) &&
            this.onEntryWidgetClicked(hoveredWidget, mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.headerWidget != null)
        {
            this.headerWidget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        for (BaseListEntryWidget listWidget : this.listWidgets)
        {
            listWidget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.getSearchBarWidget() != null &&
            this.getSearchBarWidget().onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        if (this.headerWidget != null &&
            this.headerWidget.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        if (super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        // The scroll event could be/should be distributed to the entry widgets here
        // It's not done (for now?) to prevent accidentally messing up stuff when scrolling over lists that have buttons

        if (GuiUtils.isMouseInRegion(mouseX, mouseY, this.getX(), this.getY(), this.entryWidgetWidth, this.listHeight))
        {
            int amount = MathHelper.clamp(3, 1, this.visibleListEntries);
            this.offsetSelectionOrScrollbar(mouseWheelDelta < 0 ? amount : -amount, false);
            return true;
        }

        return false;
    }

    protected boolean onEntryWidgetClicked(BaseListEntryWidget widget, int mouseX, int mouseY, int mouseButton)
    {
        return widget.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean onMouseClickedSearchBar(int mouseX, int mouseY, int mouseButton)
    {
        SearchBarWidget widget = this.getSearchBarWidget();

        if (widget != null)
        {
            boolean searchOpenPre = widget.isSearchOpen();
            String filterPre = widget.getFilter();

            if (widget.onMouseClicked(mouseX, mouseY, mouseButton))
            {
                // Toggled the search bar on or off, or cleared the filter with a right click
                if (widget.isSearchOpen() != searchOpenPre || filterPre.equals(widget.getFilter()) == false)
                {
                    this.refreshEntries();
                    this.resetScrollbarPosition();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (this.headerWidget != null && this.headerWidget.onKeyTyped(typedChar, keyCode))
        {
            return true;
        }

        for (BaseListEntryWidget widget : this.listWidgets)
        {
            if (widget.onKeyTyped(typedChar, keyCode))
            {
                return true;
            }
        }

        if (this.onKeyTypedSearchBar(typedChar, keyCode))
        {
            return true;
        }

        if (this.allowKeyboardNavigation)
        {
                 if (keyCode == Keyboard.KEY_UP)    this.offsetSelectionOrScrollbar(-1, true);
            else if (keyCode == Keyboard.KEY_DOWN)  this.offsetSelectionOrScrollbar( 1, true);
            else if (keyCode == Keyboard.KEY_PRIOR) this.offsetSelectionOrScrollbar(-this.visibleListEntries / 2, true);
            else if (keyCode == Keyboard.KEY_NEXT)  this.offsetSelectionOrScrollbar(this.visibleListEntries / 2, true);
            else if (keyCode == Keyboard.KEY_HOME)  this.offsetSelectionOrScrollbar(-this.getFilteredListEntryCount(), true);
            else if (keyCode == Keyboard.KEY_END)   this.offsetSelectionOrScrollbar(this.getFilteredListEntryCount(), true);
            else return super.onKeyTyped(typedChar, keyCode);

            return true;
        }

        return super.onKeyTyped(typedChar, keyCode);
    }

    protected boolean onKeyTypedSearchBar(char typedChar, int keyCode)
    {
        if (this.getSearchBarWidget() != null && this.getSearchBarWidget().onKeyTyped(typedChar, keyCode))
        {
            this.refreshEntries();
            this.resetScrollbarPosition();
            return true;
        }

        return false;
    }

    protected void offsetSelectionOrScrollbar(int amount, boolean changeSelection)
    {
        if (changeSelection == false)
        {
            this.scrollBar.offsetValue(amount);
            this.reCreateListEntryWidgets();
        }
    }

    public void resetScrollbarPosition()
    {
        this.scrollBar.setValue(0);
    }

    @Nullable
    protected BaseListEntryWidget getHoveredListWidget(int mouseX, int mouseY)
    {
        final int relativeY = mouseY - this.entryWidgetStartY;

        if (relativeY >= 0 && mouseY <= this.entryWidgetStartY + this.listHeight &&
            mouseX >= this.entryWidgetStartX &&
            mouseX < this.entryWidgetStartX + this.entryWidgetWidth)
        {

            if (this.areEntriesFixedHeight)
            {
                int relIndex = relativeY / this.entryWidgetFixedHeight;
                return relIndex < this.listWidgets.size() ? this.listWidgets.get(relIndex) : null;
            }
            else
            {
                for (BaseListEntryWidget widget : this.listWidgets)
                {
                    if (widget.isMouseOver(mouseX, mouseY))
                    {
                        return widget;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public BaseWidget getTopHoveredWidget(int mouseX, int mouseY, BaseWidget highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        highestFoundWidget = BaseWidget.getTopHoveredWidgetFromList(this.listWidgets, mouseX, mouseY, highestFoundWidget);
        return highestFoundWidget;
    }

    @Override
    public List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>(super.getAllTextFields());

        if (this.listWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.listWidgets)
            {
                textFields.addAll(widget.getAllTextFields());
            }
        }

        return textFields;
    }

    protected int getHeightForListEntryWidget(int listIndex)
    {
        if (this.areEntriesFixedHeight || listIndex >= this.listWidgets.size())
        {
            return this.entryWidgetFixedHeight;
        }
        else
        {
            return this.listWidgets.get(listIndex).getHeight();
        }
    }

    public void refreshEntries()
    {
        this.reCreateListEntryWidgets();
    }

    protected void onEntriesRefreshed()
    {
        if (this.entryRefreshListener != null)
        {
            this.entryRefreshListener.onEvent();
        }
    }

    protected void reCreateListEntryWidgets()
    {
        for (BaseListEntryWidget widget : this.listWidgets)
        {
            widget.onAboutToDestroy();
        }

        this.listWidgets.clear();
        this.visibleListEntries = 0;

        int usableHeight = this.listHeight;
        int usedHeight = 0;
        int x = this.entryWidgetStartX;
        int y = this.entryWidgetStartY;
        int listIndex = this.scrollBar.getValue();

        final int totalEntryCount = this.getFilteredListEntryCount();

        for ( ; listIndex < totalEntryCount; ++listIndex)
        {
            BaseListEntryWidget widget = this.createListEntryWidget(x, y, listIndex);

            if (widget == null)
            {
                break;
            }

            int widgetHeight = widget.getHeight();

            //System.out.printf("i: %d, usable: %d, used: %d, lh: %d, sy: %d\n", listIndex, usableHeight, usedHeight, this.listHeight, this.entryWidgetsStartY);
            if (usedHeight + widgetHeight > usableHeight)
            {
                break;
            }

            widget.setIsOdd((listIndex & 0x1) != 0);
            this.onSubWidgetAdded(widget);
            this.listWidgets.add(widget);
            ++this.visibleListEntries;

            usedHeight += widgetHeight;
            y += widgetHeight;
        }

        this.onListEntryWidgetsCreated();
    }

    /**
     * Called after the list entry widgets have been (re-)created
     */
    protected void onListEntryWidgetsCreated()
    {
        this.scrollBar.setMaxValue(this.getFilteredListEntryCount() - this.visibleListEntries);
        this.updateScrollBarHeight();
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        if (this.getSearchBarWidget() != null)
        {
            this.getSearchBarWidget().render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
        }

        super.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);

        RenderUtils.color(1f, 1f, 1f, 1f);

        this.scrollBar.render(mouseX, mouseY);

        // The value gets updated in the drawScrollBar() method above, if dragging
        if (this.scrollBar.getValue() != this.lastScrollbarPosition)
        {
            this.lastScrollbarPosition = this.scrollBar.getValue();
            this.reCreateListEntryWidgets();
        }

        // Draw the currently visible widgets
        for (int i = 0; i < this.listWidgets.size(); i++)
        {
            this.renderWidget(i, mouseX, mouseY, isActiveGui, hoveredWidgetId);
        }

        GlStateManager.disableLighting();
        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    protected void renderWidget(int widgetIndex, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        this.listWidgets.get(widgetIndex).render(mouseX, mouseY, isActiveGui, hoveredWidgetId, false);
    }

    @Override
    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        super.renderDebug(mouseX, mouseY, hovered, renderAll, infoAlways);
        BaseScreen.renderWidgetDebug(this.listWidgets, mouseX, mouseY, renderAll, infoAlways);
    }
}

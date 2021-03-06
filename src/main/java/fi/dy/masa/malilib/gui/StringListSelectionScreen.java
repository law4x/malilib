package fi.dy.masa.malilib.gui;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.button.BaseButton;
import fi.dy.masa.malilib.gui.button.ButtonActionListener;
import fi.dy.masa.malilib.gui.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class StringListSelectionScreen extends BaseListScreen<DataListWidget<String>>
{
    protected final ImmutableList<String> strings;
    protected final Consumer<List<String>> consumer;

    public StringListSelectionScreen(Collection<String> strings, Consumer<List<String>> consumer)
    {
        super(10, 30);

        this.strings = ImmutableList.copyOf(strings);
        this.consumer = consumer;
    }

    @Override
    protected int getListWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getListHeight()
    {
        return this.height - 60;
    }

    public List<String> getStrings()
    {
        return this.strings;
    }

    @Override
    protected DataListWidget<String> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        return new DataListWidget<>(listX, listY, listWidth, listHeight, this::getStrings);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = 12;
        int y = this.height - 32;

        x += this.createButton(x, y, ButtonListener.Type.OK) + 2;
        this.createButton(x, y, ButtonListener.Type.CANCEL);
    }

    private int createButton(int x, int y, ButtonListener.Type type)
    {
        ButtonListener listener = new ButtonListener(type, this);
        String label = type.getDisplayName();
        int width = this.getStringWidth(label) + 10;

        GenericButton button = new GenericButton(x, y, width, 20, label);
        this.addButton(button, listener);

        return width;
    }

    private static class ButtonListener implements ButtonActionListener
    {
        private final StringListSelectionScreen parent;
        private final Type type;

        public ButtonListener(Type type, StringListSelectionScreen parent)
        {
            this.parent = parent;
            this.type = type;
        }

        @Override
        public void actionPerformedWithButton(BaseButton button, int mouseButton)
        {
            if (this.type == Type.OK)
            {
                this.parent.consumer.accept(this.parent.getListWidget().getEntrySelectionHandler().getSelectedEntries());
            }
            else
            {
                BaseScreen.openGui(this.parent.getParent());
            }
        }

        public enum Type
        {
            OK      ("litematica.gui.button.ok"),
            CANCEL  ("litematica.gui.button.cancel");

            private final String translationKey;

            Type(String translationKey)
            {
                this.translationKey = translationKey;
            }

            public String getDisplayName(Object... args)
            {
                return StringUtils.translate(this.translationKey, args);
            }
        }
    }
}

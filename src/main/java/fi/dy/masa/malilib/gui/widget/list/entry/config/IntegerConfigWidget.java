package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.gui.callback.IntegerSliderCallback;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.SliderWidget;
import fi.dy.masa.malilib.gui.widget.IntegerTextFieldWidget;

public class IntegerConfigWidget extends NumericConfigWidget<IntegerConfig>
{
    protected final IntegerConfig integerConfig;
    protected final int initialValue;
    protected final String initialStringValue;

    public IntegerConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, IntegerConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, gui);

        this.integerConfig = config;
        this.initialValue = this.config.getIntegerValue();
        this.initialStringValue = String.valueOf(this.initialValue);

        this.sliderWidget = new SliderWidget(x, y, 60, 20, new IntegerSliderCallback(this.integerConfig, this.resetButton));

        this.textField.setTextValidator(new IntegerTextFieldWidget.IntValidator(this.config.getMinIntegerValue(), this.config.getMaxIntegerValue()));
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });
    }

    @Override
    protected String getCurrentValueAsString()
    {
        return String.valueOf(this.integerConfig.getIntegerValue());
    }

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (text.equals(this.initialStringValue) == false)
        {
            this.config.setValueFromString(text);
        }
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getIntegerValue() != this.initialValue;
    }
}

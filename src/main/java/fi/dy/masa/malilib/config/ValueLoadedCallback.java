package fi.dy.masa.malilib.config;

public interface ValueLoadedCallback<T>
{
    /**
     * Called after the config's value is loaded from file, or rather, from JSON, in the method
     * {@link fi.dy.masa.malilib.config.option.ConfigOption#setValueFromJsonElement(com.google.gson.JsonElement element, String configName)}
     * @param newValue
     */
    default void onValueLoaded(T newValue) {}
}

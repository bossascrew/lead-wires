package me.saharnooby.plugins.leadwires.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author saharNooby
 * @since 16:41 25.03.2020
 */
public final class MessageConfig {

	@Getter
	@Setter
	private MiniMessage componentParser;

	private final Map<String, String> messages = new HashMap<>();

	public MessageConfig(@NonNull File file) throws IOException, InvalidConfigurationException {
		this();
		YamlConfiguration config = new YamlConfiguration();
		config.load(file);
		for (String key : config.getKeys(false)) {
			this.messages.put(key, config.getString(key));
		}
	}

	public MessageConfig() {
		componentParser = MiniMessage.get();
	}

	public Component format(@NonNull String key, Object... args) {
		return formatMessage(this.messages.getOrDefault(key, "<red>" + key), args);
	}

	public Component formatMessage(@NonNull String message, Object... args) {
		List<Template> templates = new ArrayList<>();
		int index = 1;
		for (Object o : args) {
			templates.add(Template.of("param" + index, o.toString()));
			index++;
		}
		return componentParser.parse(message, templates);
	}
}

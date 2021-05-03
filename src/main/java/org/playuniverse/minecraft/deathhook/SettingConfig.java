package org.playuniverse.minecraft.deathhook;

import java.io.File;

import com.syntaxphoenix.syntaxapi.json.JsonArray;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.value.JsonBoolean;
import com.syntaxphoenix.syntaxapi.json.value.JsonString;

public class SettingConfig extends Config {

	private static final String CALENDAR = "\uD83D\uDCC6";
	private static final String SKULL = "\uD83D\uDC80";
	private static final String EMPTY = "\u200B";

	private boolean debug = true;
	private boolean seconds = false; // Show seconds in death message
	
	private String webhook = null;
	private JsonObject embed = null;

	public SettingConfig(File directory) {
		super(directory, "settings");
	}
	
	public boolean withSeconds() {
		return seconds;
	}

	public boolean isDebug() {
		return debug;
	}

	public String getWebhook() {
		return webhook;
	}

	public boolean hasWebhook() {
		return webhook != null;
	}

	public JsonObject getEmbed() {
		return embed;
	}

	public boolean hasEmbed() {
		return embed != null;
	}

	@Override
	protected void reload(JsonObject content) {
		JsonBoolean debug = content.has("debug") ? (JsonBoolean) content.get("debug")
				: new JsonBoolean(false);
		this.debug = debug.getValue();
		JsonBoolean seconds = content.has("seconds") ? (JsonBoolean) content.get("seconds")
				: new JsonBoolean(false);
		this.seconds = seconds.getValue();
		JsonString webhook = content.has("webhook") ? (JsonString) content.get("webhook")
				: new JsonString("");
		this.webhook = webhook.getValue().trim().isEmpty() ? null : webhook.getValue().trim();
		JsonObject embed = content.has("embed") ? (JsonObject) content.get("embed")
				: buildDefaultEmbed();
		if (embed.has("color", ValueType.STRING)) {
			embed.set("color", ColorHelper.fromHexColor((String) embed.get("color").getValue()).getRGB());
		}
		this.embed = embed;
		content.set("webhook", webhook);
		content.set("embed", embed);
		content.set("debug", debug);
		content.set("seconds", seconds);
	}

	@Override
	protected void postReload() {
		postModification(embed);
	}

	private String applyModification(String input) {
		return input.replace(":empty:", EMPTY).replace(":calendar:", CALENDAR).replace(":skull:", SKULL);
	}

	private void postModification(JsonObject object) {
		for (String key : object.keys()) {
			JsonValue<?> value = object.get(key);
			if (value.hasType(ValueType.OBJECT)) {
				postModification((JsonObject) value);
				continue;
			}
			if (value.hasType(ValueType.ARRAY)) {
				postModification((JsonArray) value);
				continue;
			}
			if (value.hasType(ValueType.STRING)) {
				object.set(key, new JsonString(applyModification((String) value.getValue())));
				continue;
			}
		}
	}

	private void postModification(JsonArray array) {
		int size = array.size();
		for (int index = 0; index < size; index++) {
			JsonValue<?> value = array.get(index);
			if (value.hasType(ValueType.OBJECT)) {
				postModification((JsonObject) value);
				continue;
			}
			if (value.hasType(ValueType.ARRAY)) {
				postModification((JsonArray) value);
				continue;
			}
			if (value.hasType(ValueType.STRING)) {
				array.remove(index);
				array.add(new JsonString(applyModification((String) value.getValue())));
				size--; // Shrink size to prevent double modifications to a string
				continue;
			}
		}
	}

	private JsonObject buildDefaultEmbed() {
		JsonObject object = new JsonObject();
		object.set("title", "DeathHook");
		object.set("description", ":empty:");
		object.set("color", 16711680);
		JsonObject thumbnail = new JsonObject();
		thumbnail.set("url", "https://crafatar.com/renders/head/$uuid?overlay");
		object.set("thumbnail", thumbnail);
		JsonArray fields = new JsonArray();
		JsonObject date = new JsonObject();
		date.set("name", ":calendar: Date");
		date.set("value", "$time - $date");
		fields.add(date);
		JsonObject name = new JsonObject();
		name.set("name", ":skull: $name");
		name.set("value", "$reason");
		fields.add(name);
		object.set("fields", fields);
		return object;
	}

}

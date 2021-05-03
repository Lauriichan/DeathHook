package org.playuniverse.minecraft.deathhook;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.utils.java.Strings;

public class MessageConfig extends Config {

	private final EnumMap<DeathCause, String> deathCause = new EnumMap<>(DeathCause.class);
	private final EnumMap<EntityType, String> entityType = new EnumMap<>(EntityType.class);

	public MessageConfig(File directory) {
		super(directory, "messages");
	}

	public String get(DeathCause cause) {
		if (cause == null) {
			return null;
		}
		return deathCause.containsKey(cause) ? deathCause.get(cause) : cause.message();
	}
	
	public String get(EntityType type) {
		if (type == null) {
			return null;
		}
		return entityType.containsKey(type) ? entityType.get(type) : generateName(type.name());
	}

	@Override
	protected void reload(JsonObject content) {
		JsonObject death = content.has("causes") ? (JsonObject) content.get("causes") : new JsonObject();
		boolean empty = death.isEmpty();
		for(DeathCause cause : DeathCause.values()) {
			if(empty) {
				death.set(cause.name(), cause.message());
				continue;
			}
			String name = cause.name();
			if(!death.has(name)) {
				death.set(name, cause.message());
				continue;
			}
			deathCause.put(cause, (String) death.get(name).getValue());
		}
		content.set("causes", death);
		JsonObject entity = content.has("entities") ? (JsonObject) content.get("entities") : new JsonObject();
		empty = entity.isEmpty();
		for(EntityType type : EntityType.values()) {
			if(empty) {
				entity.set(type.name(), generateName(type.name()));
				continue;
			}
			String name = type.name();
			if(!entity.has(name)) {
				entity.set(name, generateName(type.name()));
				continue;
			}
			entityType.put(type, (String) entity.get(name).getValue());
		}
		content.set("entities", entity);
	}
	
	// Idk, just wanted it in one line, I know its ugly xD
	private String generateName(String input) {
		return !(input = input.toLowerCase()).contains("_") ? Strings.firstLetterToUpperCase(input)
				: Arrays.stream(input.split("_")).map(Strings::firstLetterToUpperCase).collect(Collectors.joining(" "));
	}

}

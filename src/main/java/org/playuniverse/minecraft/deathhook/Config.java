package org.playuniverse.minecraft.deathhook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.json.io.JsonSyntaxException;
import com.syntaxphoenix.syntaxapi.json.io.JsonWriter;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;
import com.syntaxphoenix.syntaxapi.utils.java.Files;
import com.syntaxphoenix.syntaxapi.utils.java.Streams;

public abstract class Config {

	public static final JsonParser PARSER = new JsonParser();
	public static final JsonWriter WRITER = new JsonWriter().setPretty(true).setSpaces(true).setIndent(4);

	private final File file;
	private final String name;

	public Config(File directory, String name) {
		this.file = new File(directory, name + ".json");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}

	public void reload(boolean debug) {
		System.out.println("[DeathHook] Reloading " + name + "...");
		JsonObject content = null;
		if (file.exists()) {
			try {
				JsonValue<?> value = PARSER.fromString(Streams.toString(new FileInputStream(file)).replace("\r", ""));
				if(value != null && value.hasType(ValueType.OBJECT)) {
					content = (JsonObject) value;
				} else { // Why T^T This is the only else in the entire code base T^T
					System.out.println("[DeathHook] Loaded " + name + " file is invalid");
				}
			} catch (IOException | JsonSyntaxException e) {
				System.out.println("[DeathHook] Failed to load " + name + " from file");
				if (debug) {
					System.out.println(Exceptions.stackTraceToString(e));
				}
			}
		}
		if(content == null) {
			content = new JsonObject();
			System.out.println("[DeathHook] Resetting " + name); 
		}
		reload(content);
		try {
			WRITER.toFile(content, Files.createFile(file));
		} catch (IOException e) {
			System.out.println("[DeathHook] Failed to save " + name + " to file");
			if (debug) {
				System.out.println(Exceptions.stackTraceToString(e));
			}
			return;
		}
		try {
			postReload();
		} catch (Exception e) {
			System.out.println("[DeathHook] Failed to run " + name + " post modifications");
			if(debug) {
				System.out.println(Exceptions.stackTraceToString(e));
			}
			return;
		}
		System.out.println("[DeathHook] Reloaded " + name + " successfully");
	}

	protected void postReload() {
	}

	protected abstract void reload(JsonObject content);

}

package org.playuniverse.minecraft.deathhook;

import java.io.File;

public final class ConfigManager extends Thread {
	
	private static final int MAX_TICK = 60;
	
	private final SettingConfig settings;
	private final MessageConfig messages;
	
	private final long[] modified = new long[2];
	
	private boolean active = true;
	private int ticks = MAX_TICK;
	
	public ConfigManager(File directory) {
		this.settings = new SettingConfig(directory);
		this.messages = new MessageConfig(directory);
		setName("DeathHook - ConfigManager");
		start();
	}
	
	public SettingConfig getSettings() {
		return settings;
	}
	
	public MessageConfig getMessages() {
		return messages;
	}
	
	public void exit() {
		active = false;
	}
	
	@Override
	public void run() {
		while(active) {
			if(ticks-- != 0) {
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// Just ignore
				}
				continue;
			}
			ticks = MAX_TICK;
			boolean debug = settings.isDebug();
			File file = settings.getFile();
			if(file.lastModified() != modified[0] || !file.exists()) {
				settings.reload(debug);
				modified[0] = file.lastModified();
			}
			file = messages.getFile();
			if(file.lastModified() != modified[1] || !file.exists()) {
				messages.reload(debug);
				modified[1] = file.lastModified();
			}
		}
	}

}

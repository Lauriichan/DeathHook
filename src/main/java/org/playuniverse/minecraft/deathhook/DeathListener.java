package org.playuniverse.minecraft.deathhook;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.playuniverse.minecraft.deathhook.shaded.vcompat.net.EasyRequest;
import org.playuniverse.minecraft.deathhook.shaded.vcompat.net.EasyResponse;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.syntaxphoenix.syntaxapi.json.JsonArray;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.value.JsonString;
import com.syntaxphoenix.syntaxapi.net.http.RequestType;
import com.syntaxphoenix.syntaxapi.utils.java.Times;

public class DeathListener implements Listener {

	private final ExecutorService service = Executors.newCachedThreadPool();

	private final MessageConfig message;
	private final SettingConfig setting;

	public DeathListener(ConfigManager manager) {
		this.message = manager.getMessages();
		this.setting = manager.getSettings();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();
		EntityDamageEvent cause = player.getLastDamageCause();
		Location location = player.getLocation().clone();
		if (killer == null) {
			service.submit(() -> processNotification(player.getName(), player.getUniqueId(), location, null, cause));
			return;
		}
		service.submit(
				() -> processNotification(player.getName(), player.getUniqueId(), location, killer.getName(), cause));
	}

	private void processNotification(String name, UUID uniqueId, Location location, String killerName,
			EntityDamageEvent cause) {
		if (killerName != null) {
			performNotification(name, uniqueId, location, message.get(DeathCause.PLAYER).replace("$player", killerName));
			return;
		}
		if (cause != null) {
			DamageCause damage = cause.getCause();
			DeathCause death = DeathCause.fromBukkitCause(damage);
			if (damage == DamageCause.ENTITY_EXPLOSION || damage == DamageCause.ENTITY_SWEEP_ATTACK
					|| damage == DamageCause.ENTITY_ATTACK || damage == DamageCause.PROJECTILE) {
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) cause;
				EntityType entityType = event.getDamager().getType();
				if (damage == DamageCause.PROJECTILE) {
					death = DeathCause.fromProjectileType(entityType);
				}
				performNotification(name, uniqueId, location, message.get(death).replace("$entity", message.get(entityType)));
				return;
			}
			performNotification(name, uniqueId, location, message.get(death));
			return;
		}
		performNotification(name, uniqueId, location, message.get(DeathCause.NONE));
	}

	private void performNotification(String name, UUID uniqueId, Location location, String reason) {
		EasyRequest request = new EasyRequest(RequestType.POST);
		JsonArray array = new JsonArray();
		array.add(copyModification(setting.getEmbed(), applyFunction(name, uniqueId, location, reason)));
		request.data("embeds", array);
		try {
			EasyResponse response = request.run(setting.getWebhook() + "?wait=true");
			if (response.getCode() == 200) {
				return;
			}
			System.out.println("[DeathHook] Failed to post notification to discord");
			if (setting.isDebug()) {
				System.out.println("====");
				System.out.println();
				System.out.println("Code: " + response.getCode());
				System.out.println();
				System.out.println("====");
				System.out.println();
				Config.WRITER.toStream(response.getDataAsJson(), System.out);
				System.out.println();
				System.out.println("====");
			}
		} catch (IOException e) {
			System.out.println("[DeathHook] Unable to post notification to webhook!");
			System.out.println("[DeathHook] Reason: \"" + e.getMessage() + '"');
		}
	}

	private String getTime() {
		String time = Times.getTime(":");
		return setting.withSeconds() ? time : time.substring(0, time.length() - 3);
	}

	private Function<String, String> applyFunction(String name, UUID uniqueId, Location location, String reason) {
		return applyFunction(name, uniqueId.toString(), reason, getTime(), Times.getDate("."),
				location.getBlockX() + "", location.getBlockY() + "", location.getBlockZ() + "",
				location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
	}

	private Function<String, String> applyFunction(String name, String uniqueId, String reason, String time,
			String date, String x, String y, String z, String location) {
		return value -> value.replace("$name", name).replace("$uuid", uniqueId).replace("$reason", reason)
				.replace("$time", time).replace("$date", date).replace("$x", x).replace("$y", y).replace("$z", z)
				.replace("$location", location);
	}

	private JsonObject copyModification(JsonObject object, Function<String, String> modification) {
		JsonObject output = new JsonObject();
		for (String key : object.keys()) {
			JsonValue<?> value = object.get(key);
			if (value.hasType(ValueType.OBJECT)) {
				output.set(key, copyModification((JsonObject) value, modification));
				continue;
			}
			if (value.hasType(ValueType.ARRAY)) {
				output.set(key, copyModification((JsonArray) value, modification));
				continue;
			}
			if (value.hasType(ValueType.STRING)) {
				output.set(key, new JsonString(modification.apply((String) value.getValue())));
				continue;
			}
			output.set(key, value);
		}
		return output;
	}

	private JsonArray copyModification(JsonArray array, Function<String, String> modification) {
		JsonArray output = new JsonArray();
		int size = array.size();
		for (int index = 0; index < size; index++) {
			JsonValue<?> value = array.get(index);
			if (value.hasType(ValueType.OBJECT)) {
				output.add(copyModification((JsonObject) value, modification));
				continue;
			}
			if (value.hasType(ValueType.ARRAY)) {
				output.add(copyModification((JsonArray) value, modification));
				continue;
			}
			if (value.hasType(ValueType.STRING)) {
				output.add(new JsonString(modification.apply((String) value.getValue())));
				continue;
			}
			output.add(value);
		}
		return output;
	}

}

package org.playuniverse.minecraft.deathhook;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum DeathCause {

	NONE("was never found"), DOWNING("forgot to breath while being underwater"), FALLING("didn't saw the deep hole"),
	FALLING_BLOCK("forgot that sand can fall down"), SUFFOCATION("wanted to be a fossile"),
	FIRE("jumped down a chimney and didn't get back up"), BURNING("liked it to play with fire"),
	LAVA("tried to swim in hot sauce"), DRYOUT("forgot how to breath air"), DRAGON_BREATH("was spit on by a dragon"),
	BLOCK_EXPLOSION("didn't see the landmine"), CACTUS("wanted to cuddle a cactus"), CRAMMING("had too many friends"),
	FLY_INTO_WALL("looked on the map while flying"), HOT_FLOOR("liked the hot and glowing block in the nether"),
	LIGHTNING("wanted to charge their phone"), MAGIC("looked into the eyes of a witch"),
	MELTING("transformed into water"), POISON("picked the wrong glas"), STARVATION("was bad at cooking"),
	SUICIDE("hated this world"), PROJECTILE_ARROW("were unlucky because the apple on their hat was missed"),
	PROJECTILE_FIREBALL("was famous in hell"), PROJECTILE_SNOWBALL("ate too much snow"),
	PROJECTILE_EGG("lost to a chicken"), PROJECTILE_ENDER_PEARL("was split into quantum"),
	PROJECTILE_LLAMA_SPIT("is hated by llamas'"), PROJECTILE_DRAGON_FIREBALL("was famous in the end"),
	PROJECTILE_SHULKER_BULLET("wanted to fly"), PROJECTILE_WITHER_SKULL("played catch with a wither"),
	PROJECTILE_SPECTRAL_ARROW("were unlucky because the apple on their hat was missed"),
	PROJECTILE_TRIDENT("had a argument with neptune"), THORNS("loved spikes"),
	VOID("wanted to see the world from below"), WITHER("had too much contact with dark skeletons"),
	CUSTOM("<This gets ignored>"), ENTITY_ATTACK("was slained by $entity"),
	ENTITY_SWEEP_ATTACK("was hit by a $entity in a $random time combo"),
	ENTITY_EXPLOSION("made a big jump because of a $entity"), PLAYER("was welcomed by $player");

	private final String fallback;

	private DeathCause(String fallback) {
		this.fallback = fallback;
	}

	public String message() {
		return fallback;
	}

	public static DeathCause fromBukkitCause(DamageCause cause) {
		if (cause == null) {
			return NONE;
		}
		switch (cause) {
		case CONTACT:
			return CACTUS;
		case FALL:
			return FALLING;
		case FIRE_TICK:
			return BURNING;
		case PROJECTILE:
			return PROJECTILE_ARROW;
		default:
			try {
				return DeathCause.valueOf(cause.name());
			} catch (IllegalArgumentException ignore) {
				return NONE;
			}
		}
	}

	public static DeathCause fromProjectileType(EntityType type) {
		switch (type) {
		case ARROW:
			return PROJECTILE_ARROW;
		case SPECTRAL_ARROW:
			return PROJECTILE_SPECTRAL_ARROW;
		case FIREBALL:
		case SMALL_FIREBALL:
			return PROJECTILE_FIREBALL;
		case DRAGON_FIREBALL:
			return PROJECTILE_DRAGON_FIREBALL;
		case SNOWBALL:
			return PROJECTILE_SNOWBALL;
		case EGG:
			return PROJECTILE_EGG;
		case ENDER_PEARL:
			return PROJECTILE_ENDER_PEARL;
		case LLAMA_SPIT:
			return PROJECTILE_LLAMA_SPIT;
		case SHULKER_BULLET:
			return PROJECTILE_SHULKER_BULLET;
		case WITHER_SKULL:
			return PROJECTILE_WITHER_SKULL;
		case TRIDENT:
			return PROJECTILE_TRIDENT;
		default:
			return NONE;
		}
	}

}

package player;

import lombok.Data;
import lombok.NonNull;
import player.field.Job;
import player.field.KeyBinding;

import java.util.HashMap;
import java.util.Map;

@Data
public class Character {

    /**
     * Start constructor fields
     */
    @NonNull final Client client;
    @NonNull String name;
    @NonNull int id, gmLevel, level, hair, face;
    @NonNull int gender, skinColor;
    @NonNull Job job;
    @NonNull int ap, sp, fame, mapId, spawnpoint;
    @NonNull int strength, dexterity, intelligence, luck;
    @NonNull int health, maxHealth, mana, maxMana, exp;
    /**
     * End constructor fields
     */
    Map<Byte, Integer> equipment = new HashMap<>();
    final Pet[] pets = new Pet[3];
    final KeyBinding[] keyBindings = new KeyBinding[90];

    public void init() {
    }
}

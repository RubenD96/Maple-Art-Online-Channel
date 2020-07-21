package field.object.life;

import client.Character;
import client.inventory.ItemVariationType;
import client.inventory.item.templates.ItemTemplate;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import client.messages.IncEXPMessage;
import client.player.quest.Quest;
import client.player.quest.QuestState;
import field.object.FieldObjectType;
import field.object.drop.AbstractFieldDrop;
import field.object.drop.EnterType;
import field.object.drop.ItemDrop;
import field.object.drop.MesoDrop;
import lombok.Getter;
import lombok.Setter;
import managers.ItemManager;
import net.database.DropAPI;
import net.maple.SendOpcode;
import net.maple.packets.CharacterPackets;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class FieldMob extends AbstractFieldControlledLife {

    private final FieldMobTemplate template;
    @Setter private int hp, mp;
    @Setter private short home;
    @Setter private int time;

    public FieldMob(FieldMobTemplate template, boolean left) {
        this.template = template;
        moveAction = 3;
    }

    public void damage(Character chr, int damage) {
        synchronized (this) {
            hp -= damage;
        }

        float indicator = hp / (float) template.getMaxHP() * 100f;

        indicator = Math.min(100, indicator);
        indicator = Math.max(0, indicator);

        chr.write(showHpBar(indicator));

        if (hp <= 0) {
            kill(chr);
        }
    }

    public void kill(Character chr) {
        field.leave(this, getLeaveFieldPacket());
        chr.gainExp(template.getExp()); // todo share

        IncEXPMessage msg = new IncEXPMessage();
        msg.setLastHit(true);
        msg.setExp(template.getExp());
        chr.write(CharacterPackets.message(msg));

        if (chr.getRegisteredQuestMobs().contains(template.getId())) {
            chr.getQuests().values().stream()
                    .filter(quest -> quest.getState() == QuestState.PERFORM)
                    .filter(quest -> quest.getMobs().containsKey(template.getId()))
                    .forEach(quest -> quest.progress(template.getId()));
        }

        field.queueRespawn(template.getId(), time, System.currentTimeMillis() + (time * 1000));

        if (template.getDrops() == null) {
            template.setDrops(DropAPI.getMobDrops(template.getId()));
        }

        drop(chr);
    }

    public void drop(Character owner) {
        List<AbstractFieldDrop> drops = new ArrayList<>();
        template.getDrops().forEach(drop -> {
            if (drop.getQuest() != 0) {
                Quest quest = owner.getQuests().get(drop.getQuest());
                if (quest != null && quest.getState() != QuestState.PERFORM) {
                    return;
                }
            }
            if (Math.random() < (1 / (double) drop.getChance())) {
                if (drop.getId() == 0) { // meso
                    int amount = (int) (Math.random() * drop.getMax() + drop.getMin());
                    drops.add(new MesoDrop(owner.getId(), this, amount, drop.getQuest()));
                } else { // item
                    ItemTemplate template = ItemManager.getItem(drop.getId());
                    if (template != null) {
                        ItemSlot item = template.toItemSlot(ItemVariationType.NORMAL);

                        if (item instanceof ItemSlotBundle) {
                            ((ItemSlotBundle) item).setNumber((short) (Math.random() * drop.getMax() + drop.getMin()));
                        }
                        drops.add(new ItemDrop(owner.getId(), this, item, drop.getQuest()));
                    } else {
                        System.out.println("Invalid item drop " + drop.getId() + " from " + this.template.getId());
                    }
                }
            }
        });
        Rectangle bounds = field.getMapArea();

        drops.forEach(drop -> {
            int x = position.x + (drops.indexOf(drop) - (drops.size() - 1) / 2) * 28;
            int y = position.y;

            x = (int) Math.min(bounds.getMaxX() - 10, x);
            x = (int) Math.max(bounds.getMinX() + 10, x);

            drop.setPosition(new Point(x, y));
            drop.setExpire(System.currentTimeMillis() + 300000);
        });

        drops.forEach(drop -> field.enter(drop));
    }

    private Packet showHpBar(float indicator) {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.MOB_HP_INDICATOR);
        pw.writeInt(id);
        pw.write((byte) indicator);

        return pw.createPacket();
    }

    @Override
    protected Packet getChangeControllerPacket(boolean setAsController) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MOB_CHANGE_CONTROLLER);
        pw.writeBool(setAsController);
        pw.writeInt(id);
        if (setAsController) encode(pw, MobSummonType.REGEN);

        return pw.createPacket();
    }

    private void encode(PacketWriter pw, MobSummonType type) {
        pw.write(1);
        pw.writeInt(template.getId());
        pw.writeLong(0);
        pw.writeLong(0);

        pw.writePosition(position);
        pw.write(moveAction);
        pw.writeShort(foothold);
        pw.writeShort(home);

        pw.write(type.getType());
        if (type == MobSummonType.REVIVED || type.getType() >= 0) {
            pw.writeInt(0); // summon option
        }

        pw.write(0);
        pw.writeInt(0);
        pw.writeInt(0);
    }

    @Override
    public FieldObjectType getFieldObjectType() {
        return FieldObjectType.MOB;
    }

    public Packet getEnterFieldPacket(MobSummonType type) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MOB_ENTER_FIELD);
        pw.writeInt(id);
        encode(pw, type);

        return pw.createPacket();
    }

    @Override
    public Packet getEnterFieldPacket() {
        return getEnterFieldPacket(MobSummonType.REGEN);
    }

    @Override
    public Packet getLeaveFieldPacket() {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.MOB_LEAVE_FIELD);
        pw.writeInt(id);
        pw.write(1);

        return pw.createPacket();
    }
}

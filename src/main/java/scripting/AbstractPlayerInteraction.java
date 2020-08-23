package scripting;

import client.Character;
import client.Client;
import client.effects.field.TrembleFieldEffect;
import client.effects.user.QuestEffect;
import client.interaction.storage.ItemStorageInteraction;
import client.inventory.ItemVariationType;
import client.inventory.item.templates.ItemEquipTemplate;
import client.inventory.item.templates.ItemTemplate;
import client.inventory.slots.ItemSlotEquip;
import client.messages.IncEXPMessage;
import client.messages.broadcast.types.AlertMessage;
import client.messages.broadcast.types.NoticeWithoutPrefixMessage;
import client.messages.quest.PerformQuestRecordMessage;
import client.player.quest.Quest;
import field.object.life.FieldMob;
import field.object.life.FieldMobTemplate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import managers.ItemManager;
import managers.MobManager;
import net.maple.packets.CharacterPackets;
import net.maple.packets.GuildPackets;
import org.graalvm.collections.Pair;
import scripting.npc.NPCScriptManager;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("unused")
public abstract class AbstractPlayerInteraction {

    protected @NonNull final Client c;

    public Character getPlayer() {
        return c.getCharacter();
    }

    public void gainItem(int id, int quantity) {
        gainItemInternal(id, quantity);
        c.write(CharacterPackets.localEffect(new QuestEffect(id, quantity)));
    }

    public void massGainItem(int[][] items) {
        List<Pair<Integer, Integer>> entries = new ArrayList<>();
        Arrays.stream(items).forEach(item -> {
            gainItemInternal(item[0], item[1]);
            entries.add(Pair.create(item[0], item[1]));
        });
        c.write(CharacterPackets.localEffect(new QuestEffect(entries)));
    }

    private void gainItemInternal(int id, int quantity) {
        if (quantity > 0) {
            ItemTemplate item = ItemManager.getItem(id);
            if (item != null) {
                CharacterPackets.modifyInventory(getPlayer(),
                        i -> i.add(item, (short) quantity),
                        false);
            }
        } else {
            CharacterPackets.modifyInventory(getPlayer(),
                    i -> i.take(id, (short) -quantity),
                    false);
        }
    }

    public void gainMeso(int meso) {
        getPlayer().gainMeso(meso, true);
    }

    public boolean haveItem(int id, int quantity) {
        return getPlayer().getItemQuantity(id) >= quantity;
    }

    public void gainExp(int exp) {
        getPlayer().gainExp(exp);

        IncEXPMessage msg = new IncEXPMessage();
        msg.setExp(exp);
        msg.setOnQuest(true);
        getPlayer().write(CharacterPackets.message(msg));
    }

    public void warp(int id, String portal) {
        getPlayer().changeField(id, portal);
    }

    public void warp(int id) {
        getPlayer().changeField(id);
    }

    public int getMapId() {
        return getPlayer().getFieldId();
    }

    public void sendBlue(String message) {
        getPlayer().write(CharacterPackets.message(new NoticeWithoutPrefixMessage(message)));
    }

    public void setQuestProgress(int qid, int mob, String progress) {
        Quest quest = getPlayer().getQuests().get(qid);
        if (quest != null) {
            quest.progress(mob, Integer.parseInt(progress));
        }
    }

    public Quest getQuest(int id) {
        return getPlayer().getQuests().get(id);
    }

    public void openNpc(int id) {
        if (!NPCScriptManager.getInstance().converse(c, id)) {
            sendBlue("Npc does not have a script");
        }
    }

    public boolean isEquip(int id) {
        ItemTemplate template = ItemManager.getItem(id);
        if (template == null) return false;
        return template instanceof ItemEquipTemplate;
    }

    public ItemSlotEquip getEquipById(int id) {
        ItemTemplate template = ItemManager.getItem(id);
        if (template == null) return null;
        return (ItemSlotEquip) template.toItemSlot();
    }

    @SuppressWarnings({"unchecked"})
    public void gainStatItem(int id, Object obj) {
        AbstractMap<String, Integer> stats = (AbstractMap) obj;
        ItemSlotEquip equip = getEquipById(id);
        if (equip != null) {
            equip.setSTR(stats.get("STR").shortValue());
            equip.setDEX(stats.get("DEX").shortValue());
            equip.setLUK(stats.get("LUK").shortValue());
            equip.setINT(stats.get("INT").shortValue());
            equip.setPAD(stats.get("PAD").shortValue());
            equip.setMAD(stats.get("MAD").shortValue());
            equip.setACC(stats.get("ACC").shortValue());
            equip.setEVA(stats.get("EVA").shortValue());
            equip.setJump(stats.get("JUMP").shortValue());
            equip.setSpeed(stats.get("SPEED").shortValue());
            equip.setPDD(stats.get("PDD").shortValue());
            equip.setMDD(stats.get("MDD").shortValue());
            equip.setMaxHP(stats.get("HP").shortValue());
            equip.setMaxMP(stats.get("MP").shortValue());
            equip.setRUC(stats.get("SLOTS").byteValue());

            CharacterPackets.modifyInventory(getPlayer(), i -> i.add(equip), false);
        }
    }

    public void tremble(boolean heavy, int delay) {
        getPlayer().getField().broadcast(CharacterPackets.fieldEffect(new TrembleFieldEffect(heavy, delay)));
    }

    public void openNpcIn(int npc, int time, boolean dispose) {
        c.getCh().eventLoop().schedule(() -> {
            if (dispose) {
                c.setLastNpcClick(0);
                NPCScriptManager.getInstance().dispose(c);
            }
            NPCScriptManager.getInstance().converse(c, npc);
        }, time, TimeUnit.MILLISECONDS);
    }

    public void openNpcIn(int npc, int time) {
        openNpcIn(npc, time, true);
    }

    public void executeAfter(Function<AbstractPlayerInteraction, Void> func, int after) {
        c.getCh().eventLoop().schedule(() -> func.apply(this), after, TimeUnit.MILLISECONDS);
    }

    public void alert(String msg) {
        c.write(CharacterPackets.message(new AlertMessage(msg)));
    }

    public void openStorage(int npcId) {
        new ItemStorageInteraction(npcId, c.getStorage()).open(getPlayer());
    }

    public void changeGuildName(String name) {
        GuildPackets.changeGuildName(getPlayer(), name);
    }

    public void loadGuild() {
        if (getPlayer().getGuild() == null) return;
        c.write(GuildPackets.getLoadGuildPacket(getPlayer().getGuild()));
    }

    public FieldMobTemplate getMobTemplate(int id) {
        return MobManager.getMob(id);
    }
}

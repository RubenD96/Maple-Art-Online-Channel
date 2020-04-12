package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.inventory.ItemInventoryType;
import client.inventory.item.templates.ItemTemplate;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import managers.ItemManager;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static constants.ServerConstants.COMMAND_LIST;

public class UserChatHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        reader.readInteger(); // ?

        String msg = reader.readMapleString();
        boolean textBox = !reader.readBool();

        if (msg.equals("script")) {
            scriptExample();
            return;
        } else if (msg.split(" ")[0].equals("!eval")) {
            msg = msg.substring(6);
            // example use:
            // !eval c.getCharacter().gainMeso(100);
            eval(c, msg);
            return;
        } else if (msg.equals("pos")) {
            System.out.println("pos\n\t" + chr.getPosition());
            return;
        } else if (msg.split(" ")[0].equals("!item")) {
            int id = Integer.parseInt(msg.split(" ")[1]);
            AtomicInteger quantity = new AtomicInteger(1); // lmao
            if (msg.split(" ").length > 2) {
                quantity.set(Integer.parseInt(msg.split(" ")[2]));
            }
            ItemTemplate item = ItemManager.getItem(id);
            if (item != null) {
                CharacterPackets.modifyInventory(chr,
                        i -> i.add(item, (short) quantity.get()),
                        false);
            }
            return;
        } else if (msg.equals("inv")) {
            chr.getInventories().get(ItemInventoryType.ETC)
                    .getItems()
                    .forEach((slot, item) -> System.out.println(slot + "(" + item + ")"));
            return;
        } else if (msg.equals("help")) {
            for(List<String> s : COMMAND_LIST) {
                for (String x : s) {
                    chr.getField().broadcast(sendMessage(chr, x, textBox), null);
                }
            }
            return;
        }

        if(COMMAND_LIST.get(chr.getGmLevel()).contains(msg)) {
            System.out.println("Command Executed!");
            return;
        }

        chr.getField().broadcast(sendMessage(chr, msg, textBox), null);
    }

    public static void refreshCommandList() {
        try(Stream<Path> walk = Files.walk(Paths.get("scripts/command"))) {
            walk.filter(Files::isRegularFile)
                    .forEach(x -> {
                        String s = x.toString();
                        String trimmed = s.substring(s.indexOf("\\", s.indexOf("\\") + 1) + 1);

                        String level = trimmed.substring(0, trimmed.lastIndexOf('\\'));
                        String command = trimmed.substring(trimmed.lastIndexOf('\\') + 1, trimmed.lastIndexOf('.'));
                        switch(level) {
                            case "Admin":
                                COMMAND_LIST.get(2).add(command);
                            case "GM":
                                COMMAND_LIST.get(1).add(command);
                            case "Player":
                                COMMAND_LIST.get(0).add(command);
                                break;
                            default:
                                break;
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Packet sendMessage(Character chr, String msg, boolean textBox) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeHeader(SendOpcode.USER_CHAT);
        pw.writeInt(chr.getId());
        pw.writeBool(chr.isGM());
        pw.writeMapleString(msg);
        pw.writeBool(!textBox);

        return pw.createPacket();
    }

    private void scriptExample() {
        ScriptEngine engine = GraalJSScriptEngine.create();
        try {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(10);
            list.add(20);
            list.add(30);
            engine.put("list", list);
            engine.eval(
                    "execute();\n" +
                            "function execute() {\n" +
                            "   for (let value of list) {\n" +
                            "       console.log(value);\n" +
                            "   }\n" +
                            "}"
            );
        } catch (ScriptException se) {
            se.printStackTrace();
        }
    }

    private void eval(Client c, String command) {
        ScriptEngine engine = GraalJSScriptEngine.create();
        System.out.println("Evaluating:\n" + command);
        try {
            engine.put("c", c);
            engine.put("chr", c.getCharacter());
            engine.put("field", c.getCharacter().getField());
            engine.eval(command);
        } catch (ScriptException se) {
            se.printStackTrace();
        }
    }
}

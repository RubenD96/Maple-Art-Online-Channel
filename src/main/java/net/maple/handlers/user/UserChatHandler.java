package net.maple.handlers.user;

import client.Character;
import client.Client;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import scripting.shortcuts.CommandShortcut;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static constants.ServerConstants.COMMAND_FILE_LIST;
import static constants.ServerConstants.COMMAND_LIST;

public class UserChatHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        reader.readInteger(); // ?

        String msg = reader.readMapleString();
        String[] cmd = msg.split(" ");
        boolean textBox = !reader.readBool();

        if (COMMAND_LIST.get(chr.getGmLevel()).contains(cmd[0].substring(1)) && msg.charAt(0) == '!') {
            if (c.getEngines().get("cmd") == null) {
                c.getEngines().put("cmd", GraalJSScriptEngine.create());
            }

            try {
                String[] args = Arrays.copyOfRange(cmd, 1, cmd.length);

                c.getEngines().get("cmd").put("cs", new CommandShortcut(c, args));

                if (cmd[0].substring(1).equals("eval")) {
                    c.getEngines().get("cmd").eval(msg.substring(6));
                    return;
                }

                c.getEngines().get("cmd").eval(COMMAND_FILE_LIST.get(cmd[0].substring(1)));

            } catch (ScriptException e) {
                e.printStackTrace();
            }

            chr.write(sendMessage(chr, "Successfully Executed Command!", textBox));
            return;
        }

        chr.getField().broadcast(sendMessage(chr, msg, textBox), null);
    }

    public static void refreshCommandList() {
        try (Stream<Path> walk = Files.walk(Paths.get("scripts/command"))) {
            walk.filter(Files::isRegularFile)
                    .forEach(x -> {
                        String s = x.toString();
                        String trimmed = s.substring(s.indexOf("\\", s.indexOf("\\") + 1) + 1);

                        String level = trimmed.substring(0, trimmed.lastIndexOf('\\'));
                        String command = trimmed.substring(trimmed.lastIndexOf('\\') + 1, trimmed.lastIndexOf('.'));

                        try {
                            COMMAND_FILE_LIST.put(command, new String(Files.readAllBytes(x)));
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }

                        switch (level) {
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

package net.maple.handlers.user;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import client.Character;
import client.Client;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;

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
        }

        chr.getField().broadcast(sendMessage(chr, msg, textBox), null);
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

    private static void scriptExample() {
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
}

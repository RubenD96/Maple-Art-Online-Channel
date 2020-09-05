package scripting.npc;

import client.Client;
import scripting.AbstractScriptManager;

import javax.script.Invocable;
import java.util.HashMap;
import java.util.Map;

public class NPCScriptManager extends AbstractScriptManager {

    private final Map<Client, NPCConversationManager> cms = new HashMap<>();
    private final Map<Client, Invocable> scripts = new HashMap<>();
    private static final NPCScriptManager instance = new NPCScriptManager();

    private NPCScriptManager() {

    }

    public Map<Client, NPCConversationManager> getCms() {
        return cms;
    }

    public static NPCScriptManager getInstance() {
        return instance;
    }

    public boolean converse(Client c, int npc) {
        return converse(c, npc, null, 1, -1);
    }

    public void converse(Client c, int mode, int selection) {
        converse(c, cms.get(c).npcId, null, mode, selection);
    }

    public boolean converse(Client c, int npc, String fileName, int mode, int selection) {
        try {
            if (scripts.get(c) == null) {
                NPCConversationManager cm = new NPCConversationManager(c, npc);
                if (cms.containsKey(c)) {
                    dispose(c);
                }
                if (c.canClickNPC()) {
                    cms.put(c, cm);
                    Invocable iv = null;
                    if (fileName != null) {
                        iv = getInvocable("npc/" + fileName + ".js", c);
                    }
                    if (iv == null) {
                        iv = getInvocable("npc/" + npc + ".js", c);
                    }
                    if (iv == null || NPCScriptManager.getInstance() == null) {
                        dispose(c);
                        return false;
                    }
                    engine.put("cm", cm);
                    scripts.put(c, iv);
                    c.setLastNpcClick(System.currentTimeMillis());
                    try {
                        iv.invokeFunction("init");
                    } catch (final NoSuchMethodException ignored) {

                    } finally {
                        iv.invokeFunction("converse", mode, selection);
                    }
                } else {
                    c.getCharacter().enableActions();
                }
            } else {
                c.setLastNpcClick(System.currentTimeMillis());
                scripts.get(c).invokeFunction("converse", mode, selection);
            }
            return true;
        } catch (final Exception e) {
            //System.err.println(e.getMessage());
            e.printStackTrace();
            dispose(c);
            return false;
        }
    }

    public void dispose(NPCConversationManager cm) {
        Client c = cm.getC();
        //c.getCharacter().setCS(false);
        //c.getCharacter().setNpcCooldown(System.currentTimeMillis());
        cms.remove(c);
        scripts.remove(c);

        /*if (cm.getScriptName() != null) {
            resetContext("npc/" + cm.getScriptName() + ".js", c);
        } else {*/
        resetContext("npc/" + cm.getNpcId() + ".js", c);
        //}
    }

    public void dispose(Client c) {
        if (cms.get(c) != null) {
            dispose(cms.get(c));
        }
    }
}

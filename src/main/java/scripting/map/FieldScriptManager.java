package scripting.map;

import client.Client;
import field.Field;
import scripting.AbstractScriptManager;

import javax.script.Invocable;

public class FieldScriptManager extends AbstractScriptManager {

    private static FieldScriptManager instance;

    public static FieldScriptManager getInstance() {
        if (instance == null) {
            instance = new FieldScriptManager();
        }
        return instance;
    }

    private FieldScriptManager() {

    }

    public void execute(Client c, Field field, String script) {
        try {
            FieldScriptMethods map = new FieldScriptMethods(c, field);
            Invocable iv = getInvocable("map/" + script + ".js", c);
            if (iv == null) {
                System.out.println("Mapscript " + script + " is uncoded. (" + field.getId() + ")");
                return;
            }
            engine.put("field", map);
            c.setLastNpcClick(System.currentTimeMillis());
            iv.invokeFunction("execute");
        } catch (final Exception e) {
            //System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

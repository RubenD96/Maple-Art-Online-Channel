package scripting.map;

import client.Client;
import field.Field;
import lombok.Getter;
import scripting.AbstractScriptManager;

import javax.script.Invocable;

public class FieldScriptManager extends AbstractScriptManager {

    private static final @Getter FieldScriptManager instance = new FieldScriptManager();

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
            iv.invokeFunction("execute");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}

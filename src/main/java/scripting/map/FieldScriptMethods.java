package scripting.map;

import client.Client;
import field.Field;
import scripting.AbstractPlayerInteraction;

public class FieldScriptMethods extends AbstractPlayerInteraction {

    private final Field field;

    public Field getField() {
        return field;
    }

    public FieldScriptMethods(Client c, Field field) {
        super(c);
        this.field = field;
    }
}

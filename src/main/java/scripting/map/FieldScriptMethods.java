package scripting.map;

import client.Client;
import field.Field;
import lombok.NonNull;
import scripting.AbstractPlayerInteraction;

public class FieldScriptMethods extends AbstractPlayerInteraction {

    private final Field field;

    public FieldScriptMethods(@NonNull Client c, Field field) {
        super(c);
        this.field = field;
    }
}

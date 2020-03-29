package field;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FieldManager {

    Map<Integer, Field> fields = new HashMap<>();

    public synchronized Field getField(int id) {
        Field field = fields.get(id);
        if (field == null) {
            field = new Field(id);
            field.init();
            fields.put(id, field);
        }
        return field;
    }
}

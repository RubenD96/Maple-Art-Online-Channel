package manager;

import field.Field;
import lombok.Data;
import lombok.EqualsAndHashCode;
import manager.flag.FieldFlag;
import util.packet.PacketReader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class FieldManager extends AbstractManager {

    Map<Integer, Field> fields = new HashMap<>();

    public synchronized Field getField(int id) {
        Field field = fields.get(id);
        if (field == null) {
            field = new Field(id);
            field.init();
            loadFieldData(field);
            fields.put(id, field);
        }
        return field;
    }

    private void loadFieldData(Field field) {
        PacketReader r = getFieldData("wz/Map/" + field.getId() + ".mao");
        int flags = r.readInteger();

        if (containsFlag(flags, FieldFlag.ID))
            r.readInteger();

        if (containsFlag(flags, FieldFlag.RETURN_MAP))
            field.setReturnMap(r.readInteger());

        if (containsFlag(flags, FieldFlag.MAP_AREA))
            field.setMapArea(r.readRectangle());

        if (containsFlag(flags, FieldFlag.FOOTHOLDS)) {
            short size = r.readShort();
            for (int i = 0; i < size; i++) {
                int fhId = r.readInteger();
                Point point1 = r.readPoint();
                Point point2 = r.readPoint();
            }
        }

        if (containsFlag(flags, FieldFlag.FORCED_RETURN))
            field.setForcedReturnMap(r.readInteger());

        if (containsFlag(flags, FieldFlag.FIELD_LIMIT))
            field.setFieldLimit(r.readInteger());

        if (containsFlag(flags, FieldFlag.NAME))
            field.setName(r.readMapleString());

        if (containsFlag(flags, FieldFlag.ON_ENTER))
            field.setScript(r.readMapleString());

        if (containsFlag(flags, FieldFlag.PORTALS)) {
            short size = r.readShort();
            for (int i = 0; i < size; i++) {
                String name = r.readMapleString();
                String target = r.readMapleString();
                boolean hasScript = r.readBool();
                if (hasScript) {
                    String script = r.readMapleString();
                }
                Point pos = r.readPoint();
                int id = r.readInteger();
                int targetMap = r.readInteger();
                int type = r.readInteger();
            }
        }

        if (containsFlag(flags, FieldFlag.AREAS)) {
            short size = r.readShort();
            for (int i = 0; i < size; i++) {
                r.readRectangle();
            }
        }

        if (containsFlag(flags, FieldFlag.LIFE)) {
            short size = r.readShort();
            for (int i = 0; i < size; i++) {
                int id = r.readInteger();
                int time = r.readInteger();
                int x = r.readInteger();
                int y = r.readInteger();
                int f = r.readInteger();
                int hide = r.readInteger();
                int fh = r.readInteger();
                int cy = r.readInteger();
                int rx0 = r.readInteger();
                int rx1 = r.readInteger();
                String type = r.readMapleString();
            }
        }
    }

    public boolean containsFlag(int flags, FieldFlag flag) {
        return (flags & flag.getValue()) == flag.getValue();
    }
}

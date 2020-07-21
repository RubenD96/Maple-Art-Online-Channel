package field.object.drop;

import lombok.Data;
import lombok.NonNull;

@Data
public class DropEntry {

    @NonNull private int id, chance, min, max, quest;
}

package field.object.drop;

import lombok.Data;
import lombok.NonNull;

@Data
public class DropEntry {

    @NonNull private int id, min, max, quest;
    @NonNull private double chance;
}

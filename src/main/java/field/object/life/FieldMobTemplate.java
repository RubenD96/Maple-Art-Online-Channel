package field.object.life;

import field.object.drop.DropEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class FieldMobTemplate {

    private final int id;
    private String name;
    private MoveAbilityType moveType;
    private List<DropEntry> drops;

    private short level;
    private int exp, maxHP, maxMP;
    private boolean boss;

    @Override
    public String toString() {
        return "FieldMobTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", moveType=" + moveType +
                ", level=" + level +
                ", exp=" + exp +
                ", maxHP=" + maxHP +
                ", maxMP=" + maxMP +
                ", boss=" + boss +
                '}';
    }
}

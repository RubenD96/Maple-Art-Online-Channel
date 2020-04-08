package field.object.life;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FieldMobTemplate {

    @NonNull private int id;
    private String name;
    private MoveAbilityType moveType;

    private short level;
    private int exp, maxHP, maxMP;
}

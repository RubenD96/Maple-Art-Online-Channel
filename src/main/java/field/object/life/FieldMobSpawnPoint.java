package field.object.life;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@Getter
@RequiredArgsConstructor
public class FieldMobSpawnPoint {

    private final int id;
    private final Point point;
    private final int rx0, rx1, cy, time;
    private final short fh;
}

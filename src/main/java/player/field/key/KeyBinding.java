package player.field.key;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KeyBinding {
    @NonNull byte type;
    @NonNull int action;
}

package scripting.npc;

import client.Character;
import client.Client;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AbstractPlayerInteraction {

    @NonNull final Client c;

    public Character getPlayer() {
        return c.getCharacter();
    }
}

package field;

import lombok.Data;
import lombok.NonNull;
import player.Character;
import util.packet.Packet;

import java.util.ArrayList;
import java.util.List;

@Data
public class Field {

    @NonNull final int id;
    final List<Character> characters = new ArrayList<>();

    public void broadcast(Packet packet, Character source) {
        characters.stream()
                .filter(chr -> !chr.equals(source))
                .forEach(chr -> chr.write(packet));
    }
}

package client.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class Beauty {

    private final int id, gender;
    private @Setter @NonNull boolean enabled;

    @Override
    public String toString() {
        return "Beauty{" +
                "id=" + id +
                ", gender=" + gender +
                ", enabled=" + enabled +
                '}';
    }
}

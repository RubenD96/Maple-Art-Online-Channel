package net.server;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
public class MigrateInfo {

    private @NonNull int aid, channel;
    private @NonNull String ip;
    private int cid;
}

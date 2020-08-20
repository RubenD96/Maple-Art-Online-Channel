package net.server;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class MigrateInfo {

    private @NonNull int aid, channel;
    private @NonNull String ip;
    private int cid;
    private boolean cashShop;

    @Override
    public String toString() {
        return "MigrateInfo{" +
                "aid=" + aid +
                ", channel=" + channel +
                ", ip='" + ip + '\'' +
                ", cid=" + cid +
                ", cashShop=" + cashShop +
                '}';
    }
}

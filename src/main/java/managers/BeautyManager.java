package managers;

import client.player.Beauty;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public class BeautyManager {

    private @Getter final static Map<Integer, Beauty> hairs = new LinkedHashMap<>();
    private @Getter final static Map<Integer, Beauty> faces = new LinkedHashMap<>();
}

package managers;

import client.player.Beauty;

import java.util.LinkedHashMap;
import java.util.Map;

public class BeautyManager {

    private final static Map<Integer, Beauty> hairs = new LinkedHashMap<>();
    private final static Map<Integer, Beauty> faces = new LinkedHashMap<>();

    public static Map<Integer, Beauty> getHairs() {
        return hairs;
    }

    public static Map<Integer, Beauty> getFaces() {
        return faces;
    }
}

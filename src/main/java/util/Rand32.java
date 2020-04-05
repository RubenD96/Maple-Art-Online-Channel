package util;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

/**
 * Ripped from C# Edelstein
 *
 * @author Kaioru
 */
public class Rand32 {
    @Getter @Setter private long Seed1;
    @Getter @Setter private long Seed2;
    @Getter @Setter private long Seed3;
    private long _prevSeed1;
    private long _prevSeed2;
    private long _prevSeed3;

    private final Object _lock = new Object();

    public Rand32(long seed1, long seed2, long seed3) {
        Seed1 = seed1;
        Seed2 = seed2;
        Seed3 = seed3;
        _prevSeed1 = seed1;
        _prevSeed2 = seed2;
        _prevSeed3 = seed3;
    }

    public static Rand32 Create() {
        var rand = new Random();
        return new Rand32(
                (long) rand.nextInt(),
                (long) rand.nextInt(),
                (long) rand.nextInt()
        );
    }

    public int Random() {
        synchronized (_lock) {
            _prevSeed1 = Seed1;
            _prevSeed2 = Seed2;
            _prevSeed3 = Seed3;

            var result1 = (Seed1 << 12) ^ (Seed1 >> 19) ^ ((Seed1 >> 6) ^ Seed1 << 12) & 0x1FFF;
            var result2 = 16 * Seed2 ^ (Seed2 >> 25) ^ ((16 * Seed2) ^ (Seed2 >> 23)) & 0x7F;
            var result3 = (Seed3 >> 11) ^ (Seed3 << 17) ^ ((Seed3 >> 8) ^ (Seed3 << 17)) & 0x1FFFFF;

            Seed1 = result1;
            Seed2 = result2;
            Seed3 = result3;
            return (int) (result1 ^ result2 ^ result3);
        }
    }

    public int PrevRandom() {
        synchronized (_lock) {
            return (int) (
                    16 * (((_prevSeed3 & 0xFFFFFFF0) << 13) ^
                            (_prevSeed2 ^ ((_prevSeed1 & 0xFFFFFFFE) << 8)) & 0xFFFFFFF8) ^
                            ((_prevSeed1 & 0x7FFC0 ^ ((_prevSeed3 & 0x1FFFFF00 ^
                                    ((_prevSeed3 ^
                                            ((_prevSeed1 ^ (((_prevSeed2 >> 2) ^ _prevSeed2 & 0x3F800000) >> 4)) >>
                                                    8)) >>
                                            3)) >> 2)) >> 6));
        }
    }
}
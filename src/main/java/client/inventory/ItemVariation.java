package client.inventory;

import util.Rand32;

/**
 * Ripped from C# Edelstein
 *
 * @author Kaioru
 */
public class ItemVariation {

    private final Rand32 rand;
    private final ItemVariationType type;

    public ItemVariation(Rand32 rand, ItemVariationType type) {
        this.rand = rand;
        this.type = type;
    }

    public int get(int a) {
        if (a <= 0) return a;
        if (type != ItemVariationType.GACHAPON) {
            var v9 = a / 10 + 1;
            if (v9 >= 5) v9 = 5;
            var v10 = 1 << (v9 + 2);
            var v11 = v10 > 0 ? rand.Random() % v10 : rand.Random();
            var v13 = v11 >> 1;
            var v14 = ((v13 >> 3) & 1)
                    + ((v13 >> 2) & 1)
                    + ((v13 >> 1) & 1)
                    + (v13 & 1)
                    + (v11 & 1)
                    - 2
                    + ((v13 >> 4) & 1)
                    + ((v13 >> 5) & 1);
            if (v14 <= 0)
                v14 = 0;
            if (type == ItemVariationType.NORMAL) {
                var v15 = (rand.Random() & 1) == 0;
                if (v15) return a - v14;
            } else {
                var v16 = rand.Random() % 10 < (type == ItemVariationType.BETTER ? 3 : 1);
                if (type != ItemVariationType.GREAT)
                    return a;
                if (v16) return a;
            }

            return a + v14;
        }

        var v3 = a / 5 + 1;
        if (v3 >= 7) v3 = 7;
        var v4 = 1 << (v3 + 2);
        var v17 = v3 + 2;
        var v5 = v4 > 0 ? rand.Random() % v4 : rand.Random();
        var v7 = -2;

        if (v17 > 0) {
            for (var i = 0; i < v17; i++) {
                v7 += v5 & 1;
                v5 >>= 1;
            }
        }

        var v8 = (rand.Random() & 1) > 0 ? a + v7 : a - v7;
        if (v8 <= 0) v8 = 0;
        return v8;
    }
}

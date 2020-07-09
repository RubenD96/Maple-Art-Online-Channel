execute = () => {
    let chr  = cs.getChr();
    chr.setHealth(chr.getTrueMaxHealth());
    chr.setMana(chr.getTrueMaxMana());
};

execute();
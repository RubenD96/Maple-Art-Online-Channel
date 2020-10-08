function onHit() {
    console.log(`onHit (${mob.getMob().getTemplate().getId()})`);
}

function onDeath() {
    console.log(`onDeath (${mob.getMob().getTemplate().getId()})`);
}
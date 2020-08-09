execute = () => {
    const players = cs.getChr().getChannel().getCharacters();
    for (let player of players) {
        console.log(`${player.getName()} - ${player.getFieldId()}`);
    }
    cs.kickMe();
}

execute();
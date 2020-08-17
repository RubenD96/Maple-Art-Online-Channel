execute = () => {
    const players = cs.getChr().getChannel().getCharacters();
    for (let player of players) {
        cs.sendBlue(`${player.getName()} - ${player.getFieldId()}`)
    }
}

execute();
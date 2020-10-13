execute = () => {
    const players = cs.getServer().getCharacters();
    for (let player of players) {
        cs.sendBlue(`${player.getName()} - ${player.getFieldId()}`)
    }
}

execute();
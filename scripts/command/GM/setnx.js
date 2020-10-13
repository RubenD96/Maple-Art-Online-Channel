execute = () => {
    const args = cs.getArgs();

    if (args.length === 2) {
        const player = cs.getServer().getCharacter(args[0]);
        if (player !== null) {
            const amount = args[1];
            player.getClient().setCash(amount);
        } else {
            cs.sendBlue("Player is not online.");
        }
    }
};

execute();
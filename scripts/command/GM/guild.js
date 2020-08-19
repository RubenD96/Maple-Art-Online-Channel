execute = () => {
    const args = cs.getArgs();

    if (args.length > 0) {
        let command = args[0];
        switch (command) {
            case "name":
                let value = args[1];
                cs.changeGuildName(value);
                break;
            case "load":
                cs.loadGuild();
                break;
        }
    }
};

execute();
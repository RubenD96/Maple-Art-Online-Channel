execute = () => {
    const args = cs.getArgs();

    const quest = cs.getQuest(args[0]);
    cs.sendBlue(quest.getMobs().toString());
};

execute();
execute = () => {
    const args = cs.getArgs();

    if (args.length === 3) {
        let quest = args[0];
        let mob = args[1];
        let progress = "" + args[2];
        while (progress.length < 3) {
            progress = "0" + progress;
        }
        cs.setQuestProgress(quest, mob, progress);
    }
};

execute();
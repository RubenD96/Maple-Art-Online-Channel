execute = () => {
    let args = cs.getArgs();

    if (args.size < 1) {
        switch (args[0]) {
            case "export":
                cs.exportReplay();
                break;
            case "start":
                cs.startReplay();
                break;
            case "stop":
                cs.stopReplay();
                break;
            default:
                cs.sendBlue(`Invalid parameter ${args[0]}`);
                break;
        }
    }
};

execute();
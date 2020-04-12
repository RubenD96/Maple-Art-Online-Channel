execute = () => {
  const args = cs.getArgs();

  if (args.length == 1) {
    cs.reloadScripts(args[0]);
  }
}

execute();
execute = () => {
  const args = cs.getArgs();

  if(args.length > 1) {
    cs.dropItem(args[0], args[1]);
  } else {
    cs.dropItem(args[0], 1);
  }
}

execute();
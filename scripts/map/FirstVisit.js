function execute() {
    if (!field.getPlayer().isTownUnlocked(field.getMapId())) {
        field.getPlayer().addTown(field.getMapId());
        field.sendBlue("The portal master has updated your progress!");
    }
}
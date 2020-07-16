function execute() {
    if (!field.getPlayer().isTownUnlocked(field.getMapId())) {
        field.getPlayer().addTown(field.getMapId());
        //field.getPlayer().dropMessage(6, "The portal master has updated your progress!");
    }
}
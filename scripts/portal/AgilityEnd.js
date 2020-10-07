function execute() {
    let chr = portal.getPlayer();
    chr.getMoveCollections().get(portal.getMapId()).export();
    portal.getMap().startReplay();
    portal.enter();
}
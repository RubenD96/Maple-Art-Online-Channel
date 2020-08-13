function execute() {
    if (portal.getPlayer().getChannel().getChannelId() + 1 === 1) {
        portal.enter();
    } else {
        portal.alert("You can only enter this map in channel 1!");
    }
}
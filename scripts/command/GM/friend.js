execute = () => {
    const args = cs.getArgs();

    if (args.length === 2) {
        let chr = cs.getChr();
        let cid = args[0];
        let channel = args[1];
        chr.write(chr.getFriendList().getFriendChannelChangePacket(cid, channel));
    }
};

execute();
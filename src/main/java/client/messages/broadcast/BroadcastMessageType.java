package client.messages.broadcast;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BroadcastMessageType {

    NOTICE(0x00),
    ALERT(0x01),
    SPEAKERCHANNEL(0x02),
    SPEAKERWORLD(0x03),
    SLIDE(0x04),
    EVENT(0x05),
    NOTICE_WITHOUT_PREFIX(0x06),
    UTIL_DLG_EX(0x07),
    ITEM_SPEAKER(0x08),
    SPEAKER_BRIDGE(0x09),
    ART_SPEAKER_WORLD(0x0A),
    BLOW_WEATHER(0x0B),
    GACHAPON_ANNOUNCE(0x0C),
    GACHAPON_ANNOUNCE_OPEN(0x0D),
    GACHAPON_ANNOUNCE_COPY(0x0E),
    ULIST_CLIP(0x0F),
    FREEMARKET_CLIP(0x10),
    DESTROY_SHOP(0x11),
    CASHSHOP_AD(0x12),
    HEART_SPEAKER(0x13),
    SKULL_SPEAKER(0x14);

    @NonNull @Getter final int value;
}

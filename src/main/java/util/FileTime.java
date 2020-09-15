package util;

import util.packet.PacketWriter;

import java.util.Date;

/**
 * @author Kaz Voeten
 */
@Deprecated
public class FileTime {

    public int ftHighTime, ftLowTime;

    public FileTime(boolean bMaxTime) {
        long u64BitLong = bMaxTime ? 150842304000000000L : 94354848000000000L; //Max: 1/1/2079, Min: 1/1/1900
        this.ftHighTime = (int) (u64BitLong >> 32);
        this.ftLowTime = (int) u64BitLong;
    }

    public void Encode(PacketWriter pw) {
        pw.writeInt(ftHighTime);
        pw.writeInt(ftLowTime);
    }

    public FileTime(Date pDate) {
        long u64BitLong = pDate.getTime();

        u64BitLong += 11644473600000L; //add time between 1601 and 1970
        u64BitLong *= 10000L; //multiply time to nanoseconds

        this.ftHighTime = (int) (u64BitLong >> 32);
        this.ftLowTime = (int) u64BitLong;
    }

    public static Date GetDate(int ftHighTime, int ftLowTime) {

        long u64BitLong = (long) ftHighTime << 32 | ftLowTime & 0xffffffffL;

        u64BitLong /= 10000L; //devide time to ms from ns
        u64BitLong -= 11644473600000L; //remove time between 1601 and 1970

        return new Date(u64BitLong);
    }
}
package service;

import java.security.SecureRandom;

public final class TimeUUIDUtils {

    private static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    public static java.util.UUID fromTimestampMs(long time) {
        SecureRandom ng = TimeUUIDUtils.Holder.numberGenerator;
        byte[] randomBytes = new byte[8];
        ng.nextBytes(randomBytes);
        return new java.util.UUID(createTime(time), bytesToLong(randomBytes));
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    private static long createTime(long currentTime) {
        long time;
        // UTC time
        long timeToUse = currentTime;
        // time low
        time = timeToUse << 32;
        // time mid
        time |= (timeToUse & 0xFFFF00000000L) >> 16;
        // time hi and version
        time |= 0x1000 | ((timeToUse >> 48) & 0x0FFF); // version 1
        return time;
    }
}
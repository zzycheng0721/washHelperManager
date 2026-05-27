package com.washhelper.util;

public final class TenantUtil {
    private TenantUtil() {
    }

    public static Long resolve(Long shopId, String shopIdHeader) {
        if (shopId != null) {
            return shopId;
        }
        if (shopIdHeader != null && !shopIdHeader.isBlank()) {
            return Long.valueOf(shopIdHeader);
        }
        return 1L;
    }
}

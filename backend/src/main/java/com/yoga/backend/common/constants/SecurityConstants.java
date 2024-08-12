package com.yoga.backend.common.constants;

public class SecurityConstants {

    public static final String JWT_KEY = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final String JWT_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";

    public static final long ACCESS_TOKEN_EXPIRATION = 28800000; // 8시간 (8 * 60 * 60 * 1000 밀리초)
    public static final long REFRESH_TOKEN_EXPIRATION = 2592000000L; // 30일 (30 * 24 * 60 * 60 * 1000 밀리초)

}

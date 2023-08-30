package com.nowcoder.mycommunity.util;


public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";

    // a entity's like
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // a user's like
    // like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // entities followed by someone
    // followee:userId:entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // People who follow this user
    // follower:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // kaptcha
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    // a single day's UV
    public static String getUVKey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    // a series of days' UV
    public static String getUVKey(String startDate, String endDate){
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    // get the DAU of a single day
    public static String getDAUKey(String date){
        return PREFIX_DAU + SPLIT + date;
    }

    // get the DAU of a series of days
    public static String getDAUKey(String startDate, String endDate){
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    public static String getPostScoreKey(){
        return PREFIX_POST + SPLIT + "score";
    }
}

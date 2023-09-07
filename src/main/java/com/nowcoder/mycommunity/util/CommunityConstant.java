package com.nowcoder.mycommunity.util;

public interface CommunityConstant {
    /**
     * activate account success
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * repeat activate
     */
    int ACTAVATION_REPEATE = 1;

    /**
     * activation failed
     */
    int ACTAVATION_FAILED = 2;

    /**
     * The default login certificate expiration time
     * 12 hours
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * the customize login certificate expiration time
     * 100 days
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * entity type:post
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * entity type:comment
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * entity type:user
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * topic: comment
     */
    String TOPIC_COMMENT = "comment";

    /**
     * topic:like
     */
    String TOPIC_LIKE = "like";

    /**
     * topic:follow
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * topic:share
     */
    String TOPIC_SHARE = "share";

    /**
     * system user ID
     */
    int SYSTEM_USER_ID = 1;

    /**
     * authority: normal user
     */
    String AUTHORITY_USER = "user";

    /**
     * authority: administrator
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * authority: moderator
     */
    String AUTHORITY_MODERATOR = "moderator";
}

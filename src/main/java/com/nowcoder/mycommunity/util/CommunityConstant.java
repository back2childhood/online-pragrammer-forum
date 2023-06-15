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
     * entity type:posr
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * entity type:comment
     */
    int ENTITY_TYPE_COMMENT = 2;
}

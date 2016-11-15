package com.madhouse.performad.common.mq;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by oneal on 15/9/7.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageSupport {
	/**
	 * thread pool size for current channel/tube<BR>
	 * default value is 1.
	 * @return
	 */
    int threadPoolSize() default 1; 
    /**
     * limit seconds, used for limit access count in target seconds.<BR>
     * default value is 0, means no need to limit.
     * @return
     */
    long limitSecondsWithIn() default 0;
    /**
     * max counts, used for limit access count in target seconds.<BR>
     * default value is 0, means no need to limit.
     * @return
     */
    long maxCountsInLimitSeconds() default 0;
}

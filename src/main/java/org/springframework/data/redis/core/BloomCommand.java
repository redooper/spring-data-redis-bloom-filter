package org.springframework.data.redis.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Auther: Jackie
 * @Date: 2019-07-26 11:06
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum BloomCommand {

    RESERVE("BF.RESERVE"),
    ADD("BF.ADD"),
    MADD("BF.MADD"),
    EXISTS("BF.EXISTS"),
    MEXISTS("BF.MEXISTS");

    private String command;

}

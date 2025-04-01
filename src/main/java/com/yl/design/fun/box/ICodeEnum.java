package com.yl.design.fun.box;

/**
 * @author suiwp
 * @date 2025/4/1 09:42
 */
public interface ICodeEnum<E extends Enum<E>> {
    default String formatOfActType(String actType) {
        return String.format("%s >>> %s", actType, this.name());
    }

    String name();

    enum Default implements ICodeEnum<Default> {
        OK
    }
}

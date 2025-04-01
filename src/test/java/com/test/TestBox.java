package com.test;

import com.yl.design.fun.box.Box;
import com.yl.design.fun.box.ErrBox;
import com.yl.design.fun.box.ICodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author suiwp
 * @date 2025/4/1 11:17
 */
public class TestBox {
    public static void main(String[] args) {
        Box<Void> test = Box.err(ErrCode.NONE_DATA_ERR, "生成文件IO异常", new Exception("bbbbbb"));
        ErrBox<Void> errBox = test.toErrBox();
//        ErrBox<Object> errBox = ErrBox.throwBox(ErrCode.ERR_IO, "生成文件IO异常", new Exception("aaa"));
        Logger aaa = LoggerFactory.getLogger("aaa");
        errBox.logErr(aaa, errBox.prefix("测试任务"));
        ErrBox.EMPTY.logErr(aaa, ErrBox.EMPTY.prefix("测试任务2"));

    }


    enum ErrCode implements ICodeEnum<ErrCode> {
        NONE_DATA_ERR,
        IO_ERR,

    }
}

package com.yl;

import com.yl.design.fun.box.Box;
import com.yl.design.fun.box.ErrBox;
import com.yl.design.fun.box.ICodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        //TIP 当文本光标位于高亮显示的文本处时按 <shortcut actionId="ShowIntentionActions"/>
        // 查看 IntelliJ IDEA 建议如何修正。
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP 按 <shortcut actionId="Debug"/> 开始调试代码。我们已经设置了一个 <icon src="AllIcons.Debugger.Db_set_breakpoint"/> 断点
            // 但您始终可以通过按 <shortcut actionId="ToggleLineBreakpoint"/> 添加更多断点。
            System.out.println("i = " + i);
        }
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
package com.yl.design.fun.box;

import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * @author suiwp
 * @date 2024/9/19 10:21
 */
public abstract class ErrBox<T> extends Box<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final ErrBox<Void> EMPTY = new ErrMsgBox<>(ICodeEnum.Default.OK, "");

    private ErrBox() {
    }

    protected static <T> ErrBox<T> throwBox(ICodeEnum<?> errCode, String msg, Throwable throwable) {
        return new ErrThrowBox<>(errCode, msg, throwable);
    }

    protected static <T> ErrBox<T> msgBox(ICodeEnum<?> errCode, String msg) {
        return new ErrMsgBox<>(errCode, msg);
    }

    @Override
    public ErrBox<T> toErrBox() {
        return this;
    }

    @Override
    public boolean isErr() {
        return true;
    }

    @Override
    public boolean isRight() {
        return false;
    }

    @Override
    public T getVal() {
        return null;
    }

    public abstract String getMsg();

    public abstract void logErr(org.slf4j.Logger logger, String logMsg);

    public abstract String getThrowStackStr();

    /**
     * format msg with flag <pre>
     *     errFlag + ErrCode + msg + throwableMsg(if exist)
     *     </pre>
     * eg： 测试任务 >>> ERR_IO。 生成文件IO异常. SftpIoErr  : java.lang.IOException.
     *
     * @param errFlag errTitle
     * @return err Msg
     */
    public abstract String prefix(String errFlag);

    private static class ErrMsgBox<T> extends ErrBox<T> implements Serializable {
        public final String msg;
        public final ICodeEnum<?> errCode;

        public ErrMsgBox(ICodeEnum<?> errCode, String msg) {
            this.msg = msg;
            this.errCode = errCode;
        }

        @Override
        public String getMsg() {
            return msg;
        }

        @Override
        public void logErr(Logger logger, String logMsg) {
            logger.error(logMsg);
        }

        @Override
        public String getThrowStackStr() {
            return msg;
        }

        @Override
        public String prefix(String errFlag) {
            return String.format("%s。 %s.", errCode.formatOfActType(errFlag), msg);
        }
    }

    private static class ErrThrowBox<T> extends ErrBox<T> implements Serializable {
        public final Throwable throwable;
        public final String msg;
        public final ICodeEnum<?> errCode;
        private String throwStackStr;
        private String msgWithThrow;

        public ErrThrowBox(ICodeEnum<?> errCode, String msg, Throwable throwable) {
            this.throwable = throwable;
            this.msg = msg;
            this.errCode = errCode;
        }

        @Override
        public String getMsg() {
            return msg;
        }

        @Override
        public void logErr(Logger logger, String logMsg) {
            logger.error(logMsg, throwable);
        }

        @Override
        public String getThrowStackStr() {
            if (throwStackStr != null) {
                return throwStackStr;
            }
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                throwable.printStackTrace(pw);
                throwStackStr = sw.toString();
                return throwStackStr;
            }
        }

        @Override
        public String prefix(String errFlag) {
            if (msgWithThrow != null) {
                return String.format("%s。 %s.", errCode.formatOfActType(errFlag), msgWithThrow);
            }
            msgWithThrow = String.format("%s.  %s : %s", msg, throwable.getMessage(), throwable.getClass().getName());
            return String.format("%s。 %s.", errCode.formatOfActType(errFlag), msgWithThrow);
        }
    }


}

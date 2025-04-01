package com.yl.design.fun.result;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * @author suiwp
 * @date 2025/4/1 10:54
 */
public class Result<E extends Enum<E>, V> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String msg;
    private final V val;
    private final Throwable th;
    private final E errCode;
    private String msgOfThrowable;

    private Result(V val, E errCode, String msg, Throwable th) {
        this.val = val;
        this.errCode = errCode;
        this.msg = msg;
        this.th = th;
    }


    public static final Result<?, Void> EMPTY_VAL = Result.val(null);

    public static Result<?, Void> emptyVal() {
        return EMPTY_VAL;
    }

    enum Default {
        OK,
    }


    /**
     * Constructs a {@link Result}
     *
     * <pre>{@code
     * // Creates Result instance initiated with value 1
     * Result<?, Integer> either = Result.val(1);
     * }</pre>
     *
     * @param val The value.
     * @param <V> Type of right value.
     * @return A new {@code Right} instance.
     */
    public static <E extends Enum<E>, V> Result<E, V> val(V val) {
        return new Result(val, Default.OK, null, null);
    }

    /**
     * Constructs a {@link Result}
     *
     * <pre>{@code
     * // Creates Either instance initiated with left value "error message"
     * Either<String, ?> either = Either.left("error message");
     * }</pre>
     *
     * @param errCode The errCode.
     * @param msg     msg value.
     * @return A new {@code Left} instance.
     */
    public static <E extends Enum<E>, T> Result<E, T> err(E errCode, String msg) {
        return new Result(null, errCode, msg, null);
    }

    /**
     * Constructs a {@link Result}
     *
     * <pre>{@code
     * // Creates Either instance initiated with left value "error message"
     * Either<String, ?> either = Either.left("error message");
     * }</pre>
     *
     * @return A new {@code Err} instance.
     */
    public static <E extends Enum<E>, T> Result<E, T> err(E errCode, String msg, Throwable th) {
        return new Result(null, errCode, msg, th);
    }

    /**
     * Narrows a widened {@code Result<? extends L, ? extends R>} to {@code Result<L, R>}
     * by performing a type-safe cast. This is eligible because immutable/read-only
     * collections are covariant.
     *
     * <pre>{@code
     * // It's ok, Integer inherits from Number
     * Result<Number> answer = Result.val(42);
     *
     * // RuntimeException is an Exception
     * Result<?> failed = Result.err(ErrCode.Err, "xx", new RuntimeException("Vogon poetry recital"));
     * }</pre>
     *
     * @param result A {@code Result}.
     * @param <R>    Type of right value.
     * @return the given {@code box} instance as narrowed type {@code Result<R>}.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>, R> Result<E, R> narrow(Result<E, ? extends R> result) {
        return (Result<E, R>) result;
    }

    @SuppressWarnings("unchecked")
    public <R> Result<E, R> narrow() {
        return (Result<E, R>) this;
    }

    /**
     * Returns whether this Result is a ErrResult.
     *
     * <pre>{@code
     * // prints "true"
     * System.out.println(Result.err("error").isErr());
     *
     * // prints "false"
     * System.out.println(Result.val(42).isErr());
     * }</pre>
     *
     * @return true, if this is a ErrResult, false otherwise
     */
    public boolean isErr() {
        return errCode != null;
    }

    /**
     * Returns whether this Either is a Right.
     *
     * <pre>{@code
     * // prints "true"
     * System.out.println(Result.val(42).isRight());
     *
     * // prints "false"
     * System.out.println(Result.err("error").isRight());
     * }</pre>
     *
     * @return true, if this is a Right, false otherwise
     */
    public boolean isRight() {
        return errCode == null;
    }

    public V getVal() {
        return val;
    }

    public static class DealErr {
        public static <E extends Enum<E>, V> String getErrMsg(Result<E, V> result) {
            return result.msg;
        }

        public static <E extends Enum<E>, V> String getLogMsg(String logPrefix, Result<E, V> result) {
            return String.format("【%s】>>> %s", logPrefix, result.msg);
        }

        public static <E extends Enum<E>, V> E getErrCode(Result<E, V> result) {
            return result.errCode;
        }

        public static <E extends Enum<E>, V> String getLogMsgOfThrowable(String logPrefix, Result<E, V> result) {
            return String.format("%s.\t%s", getLogMsg(logPrefix, result), getMsgOfThrowable(result));
        }

        public static String getMsgOfThrowable(Result<?, ?> result) {
            return result.msgOfThrowable == null
                    ? result.msgOfThrowable = dealThrowableMsg(result)
                    : result.msgOfThrowable;
        }

        private static String dealThrowableMsg(Result<?, ?> result) {
            if (result.th != null) {
                StringWriter sw = new StringWriter();
                sw.append(result.th.getClass().getName());
                sw.append(" : ");
                sw.append(result.th.getMessage());
                sw.append("\n");
                try (PrintWriter pw = new PrintWriter(sw)) {
                    result.th.printStackTrace(pw);
                    return sw.toString();
                }
            } else {
                return "";
            }
        }


    }

}

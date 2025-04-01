package com.yl.design.fun.box;

import java.io.Serializable;

/**
 * @author suiwp
 * @date 2024/9/19 10:40
 */
public abstract class Box<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Box<Void> EMPTY_VAL = Box.val(null);

    public static Box<Void> emptyVal() {
        return EMPTY_VAL;
    }

    /**
     * Constructs a {@link Val}
     *
     * <pre>{@code
     * // Creates Box instance initiated with value 1
     * Box<?, Integer> either = Box.val(1);
     * }</pre>
     *
     * @param val The value.
     * @param <R> Type of right value.
     * @return A new {@code Right} instance.
     */
    public static <R> Box<R> val(R val) {
        return new Val<>(val);
    }

    /**
     * Constructs a {@link ErrBox}
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
    public static <T> Box<T> err(ICodeEnum<?> errCode, String msg) {
        return ErrBox.msgBox(errCode, msg);
    }

    /**
     * Constructs a {@link ErrBox}
     *
     * <pre>{@code
     * // Creates Either instance initiated with left value "error message"
     * Either<String, ?> either = Either.left("error message");
     * }</pre>
     *
     * @return A new {@code Err} instance.
     */
    public static <T> Box<T> err(ICodeEnum<?> errCode, String msg, Throwable throwable) {
        return ErrBox.throwBox(errCode, msg, throwable);
    }

    /**
     * Narrows a widened {@code Box<? extends L, ? extends R>} to {@code Box<L, R>}
     * by performing a type-safe cast. This is eligible because immutable/read-only
     * collections are covariant.
     *
     * <pre>{@code
     * // It's ok, Integer inherits from Number
     * Box<Number> answer = Box.val(42);
     *
     * // RuntimeException is an Exception
     * Box<?> failed = Box.err(ErrCode.Err, "xx", new RuntimeException("Vogon poetry recital"));
     * }</pre>
     *
     * @param box A {@code Box}.
     * @param <R> Type of right value.
     * @return the given {@code box} instance as narrowed type {@code Box<R>}.
     */
    @SuppressWarnings("unchecked")
    public static <R> Box<R> narrow(Box<? extends R> box) {
        return (Box<R>) box;
    }

    @SuppressWarnings("unchecked")
    public <R extends T> Box<R> narrow() {
        return (Box<R>) this;
    }


    /**
     * Returns the left value.
     *
     * <pre>{@code
     * // prints "error"
     * System.out.println(Box.err("error").toErrBox());
     *
     * // throws NoSuchElementException
     * System.out.println(Box.val(42).toErrBox());
     * }</pre>
     *
     * @return The left value.
     */
    public abstract ErrBox<T> toErrBox();

    /**
     * Returns whether this Box is a ErrBox.
     *
     * <pre>{@code
     * // prints "true"
     * System.out.println(Box.err("error").isErr());
     *
     * // prints "false"
     * System.out.println(Box.val(42).isErr());
     * }</pre>
     *
     * @return true, if this is a ErrBox, false otherwise
     */
    public abstract boolean isErr();

    /**
     * Returns whether this Either is a Right.
     *
     * <pre>{@code
     * // prints "true"
     * System.out.println(Box.val(42).isRight());
     *
     * // prints "false"
     * System.out.println(Box.err("error").isRight());
     * }</pre>
     *
     * @return true, if this is a Right, false otherwise
     */
    public abstract boolean isRight();

    public abstract T getVal();

    private static class Val<T> extends Box<T> implements Serializable {
        private static final long serialVersionUID = 1L;
        private final T val;

        private Val(T val) {
            this.val = val;
        }

        @Override
        public ErrBox<T> toErrBox() {
            return (ErrBox<T>) ErrBox.EMPTY;
        }

        @Override
        public boolean isErr() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public T getVal() {
            return val;
        }
    }

}

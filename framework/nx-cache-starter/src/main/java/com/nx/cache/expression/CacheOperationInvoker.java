package com.nx.cache.expression;

/**
 * 抽象的调用缓存操作方法
 * <p>不提供传输已检查异常的方法，但提供了一个特殊异常，该异常应该用于包装底层调用引发的任何异常。
 * 调用者应特别处理此异常类型。
 *
 * 
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月16日
 */
public interface CacheOperationInvoker {

    /**
     * 调用此实例定义的缓存操作.
     * Wraps any exception that is thrown during the invocation in a {@link ThrowableWrapperException}.
     *
     * @return the result of the operation
     * @throws ThrowableWrapperException if an error occurred while invoking the operation
     */
    Object invoke() throws ThrowableWrapperException;

    /**
     * Wrap any exception thrown while invoking {@link #invoke()}.
     */
    class ThrowableWrapperException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		private final Throwable original;

        public ThrowableWrapperException(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }

}

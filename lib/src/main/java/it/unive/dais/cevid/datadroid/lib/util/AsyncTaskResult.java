package it.unive.dais.cevid.datadroid.lib.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutionException;

public class AsyncTaskResult<R> {

    protected final AsyncTask<?, ?, Result<R>> task;

    public AsyncTaskResult(AsyncTask<?, ?, Result<R>> t) {
        this.task = t;
    }

    public Result<R> get() throws ExecutionException, InterruptedException {
        return task.get();
    }

    public boolean hasResult() throws ExecutionException, InterruptedException {
        return get().hasResult();
    }

    @Nullable
    public R getResult() throws ExecutionException, InterruptedException {
        return get().getResult();
    }

    @Nullable
    public Exception getException() throws ExecutionException, InterruptedException {
        return get().getException();
    }

    static class Result<R> {

        @Nullable
        private R result;
        @Nullable
        private Exception exn;

        Result(@Nullable R x) {
            result = x;
            exn = null;
        }

        Result(@NonNull Exception e) {
            exn = e;
            result = null;
        }

        public boolean hasResult() {
            return exn != null;
        }

        @Nullable
        public R getResult() {
            return result;
        }

        @NonNull
        public Exception getException() {
            if (exn != null) return exn;
            else throw new RuntimeException("AsyncTask has result and no exception");
        }
    }

    // async task quick wrappers
    //

    @SuppressWarnings("unchecked")
    @NonNull
    public static <T, R> AsyncTaskResult<R> run(@NonNull Function<T, R> f, @Nullable T x) {
        return new AsyncTaskResult<>(new AsyncTask<T, Void, AsyncTaskResult.Result<R>>() {
            @Override
            protected AsyncTaskResult.Result<R> doInBackground(T... x) {
                try {
                    return new AsyncTaskResult.Result<>(f.apply(x[0]));
                } catch (Exception e) {
                    return new AsyncTaskResult.Result<>(e);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, x));
    }

    public static <R> AsyncTaskResult<R> run(@NonNull Function<Void, R> f) {
        return run(f, null);
    }

    public static void run(Runnable r) {
        run(x -> {
            r.run();
            return null;
        });
    }

}

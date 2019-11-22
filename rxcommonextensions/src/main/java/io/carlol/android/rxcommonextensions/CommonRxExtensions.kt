package io.carlol.android.rxcommonextensions

import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.Semaphore

/**
 * Created on 30/01/19.
 */

/**
 * Wrap Single emission in a Result object to prevent onError trigger
 */
fun <T> Single<T>.toResult(): Single<Result<T>> {
    return map { Result.success(it) }
            .onErrorReturn { Result.failure(it) }
}

/**
 * Wrap Maybe emission in a Result object to prevent onError trigger
 */
fun <T> Maybe<T>.toResult(): Maybe<Result<T>> {
    return map { Result.success(it) }
            .onErrorReturn { Result.failure(it) }
}

/**
 * Wrap Observable emission in a Result object to prevent onError trigger
 */
fun <T> Observable<T>.toResult(): Observable<Result<T>> {
    return map { Result.success(it) }
            .onErrorReturn { Result.failure(it) }
}

/**
 * Wrap Flowable emission in a Result object to prevent onError trigger
 */
fun <T> Flowable<T>.toResult(): Flowable<Result<T>> {
    return map { Result.success(it) }
            .onErrorReturn { Result.failure(it) }
}

/**
 * Subscribe to Completable ignoring failure trigger
 */
fun Completable.subscribeNoCallbacks(): Disposable {
    return subscribe({}, { Timber.d("Not catched Rx error: $it") })
}

/**
 * Subscribe to Single ignoring failure trigger
 */
fun <T> Single<T>.subscribeNoCallbacks(): Disposable {
    return subscribe({}, { Timber.d("Not catched Rx error: $it") })
}

/**
 * Subscribe to Maybe ignoring failure trigger
 */
fun <T> Maybe<T>.subscribeNoCallbacks(): Disposable {
    return subscribe({}, { Timber.d("Not catched Rx error: $it") })
}

/**
 * Subscribe to Observable ignoring failure trigger
 */
fun <T> Observable<T>.subscribeNoCallbacks(): Disposable {
    return subscribe({}, { Timber.d("Not catched Rx error: $it") })
}

/**
 * Subscribe to Flowable ignoring failure trigger
 */
fun <T> Flowable<T>.subscribeNoCallbacks(): Disposable {
    return subscribe({}, { Timber.d("Not catched Rx error: $it") })
}


/**
 * Filter if onError
 */
fun Completable.filterOnlySuccess(): Completable {
    return onErrorComplete()
}

fun <T> Single<T>.filterOnlySuccess(): Maybe<T> {
    return toResult()
            .filter { result ->
                result.isSuccess.also { if (!it) Timber.d("Exception filtered: ${result.exceptionOrNull()?.message.orEmpty()}") }
            }
            .map { it.getOrNull() }
}


fun <T> Maybe<T>.filterOnlySuccess(): Maybe<T> {
    return toResult()
            .filter { result ->
                result.isSuccess.also { if (!it) Timber.d("Exception filtered: ${result.exceptionOrNull()?.message.orEmpty()}") }
            }
            .map { it.getOrNull() }
}

fun <T> Observable<T>.filterOnlySuccess(): Observable<T> {
    return toResult()
            .filter { result ->
                result.isSuccess.also { if (!it) Timber.d("Exception filtered: ${result.exceptionOrNull()?.message.orEmpty()}") }
            }
            .map { it.getOrNull() }
}

fun <T> Flowable<T>.filterOnlySuccess(): Flowable<T> {
    return toResult()
            .filter { result ->
                result.isSuccess.also { if (!it) Timber.d("Exception filtered: ${result.exceptionOrNull()?.message.orEmpty()}") }
            }
            .map { it.getOrNull() }
}

fun <T> Observable<T>.toSingle() = take(1).singleOrError()


fun <T> BehaviorSubject<T>.refresh() {
    if (value != null) onNext(value!!)
}

fun <T> Observable<T>.waitSemaphore(semaphore: Semaphore, isSingleStep: Boolean = false, scheduler: Scheduler = Schedulers.newThread()): Observable<T> {
    return Observable.defer {
        try {
            semaphore.acquire()
        } catch (e: Exception) {
            semaphore.release()
        }
        this
    }.doOnNext {
        if (isSingleStep) {
            semaphore.release()
        }
    }.doOnComplete {
        if (!isSingleStep) {
            semaphore.release()
        }
    }.doOnError {
        semaphore.release()
    }.doOnDispose {
        semaphore.release()
    }.subscribeOn(scheduler)
}

fun <T> Single<T>.waitSemaphore(semaphore: Semaphore, scheduler: Scheduler = Schedulers.newThread()): Single<T> {
    return Single.defer {
        try {
            semaphore.acquire()
        } catch (e: Exception) {
            semaphore.release()
        }
        this
    }.doOnSuccess {
        semaphore.release()
    }.doOnError {
        semaphore.release()
    }.doOnDispose {
        semaphore.release()
    }.subscribeOn(scheduler)
}
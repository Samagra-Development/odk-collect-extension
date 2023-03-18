package org.odk.collect.android.events

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * This is the base class for all the event exporters. Extend this class
 * to create more concrete event exporters, for example form events.
 */
open class ODKEventBus<T : Any> {

    protected val state: PublishSubject<T> = PublishSubject.create()

    fun getState(): Observable<T> {
        return state.hide()
    }
}
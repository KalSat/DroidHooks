package io.droidhooks.bindingutil

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import org.jetbrains.anko.sdk27.coroutines.onAttachStateChangeListener
import kotlin.reflect.KFunction1
import kotlin.reflect.KMutableProperty0

fun <T> LiveData<T>.observe(owner: LifecycleOwner, onChanged: (T?) -> Unit) {
    observe(owner, Observer { onChanged(it) })
}


fun <T : Any, U : T> bind(owner: LifecycleOwner, setter: (T) -> Unit,
                          field: NonNullLiveData<U>) =
        field.observe(owner) { setter(it) }

fun <T : Any, U : Any> bind(owner: LifecycleOwner, setter: (T) -> Unit,
                            field: NonNullLiveData<U>, converter: (U) -> T) =
        field.observe(owner) { setter(converter(it)) }

fun <T : Any, U : T> bind(owner: LifecycleOwner, prop: KMutableProperty0<T>,
                          field: NonNullLiveData<U>) =
        field.observe(owner) { prop.set(it) }

fun <T : Any, U : Any> bind(owner: LifecycleOwner, prop: KMutableProperty0<T>,
                            field: NonNullLiveData<U>, converter: (U) -> T) =
        field.observe(owner) { prop.set(converter(it)) }


fun <T : View> T.bindIf(owner: LifecycleOwner, data: NonNullLiveData<Boolean>): T {
    bind(owner, this::setVisibility, data) { if (it) View.VISIBLE else View.GONE }
    return this
}

fun <T : View> T.bindShow(owner: LifecycleOwner, data: NonNullLiveData<Boolean>): T {
    bind(owner, this::setVisibility, data) { if (it) View.VISIBLE else View.INVISIBLE }
    return this
}

fun <T : ViewGroup> T.addChildren(vararg children: View) {
    for (child in children) {
        addView(child)
    }
}


fun EditText.setTextSafely(text: CharSequence?) {
    if (text.toString() == this.text.toString()) {
        return
    }

    this.setText(text)
}

// hooks
fun <T> useState(initialValue: T): Pair<NonNullLiveData<T>, KFunction1<T, Unit>> {
    val state = NonNullLiveData(initialValue)
    return Pair(state, state::postValue)
}

fun <T : View> T.useEffect(create: () -> (() -> Unit)?) {
    onAttachStateChangeListener {
        var destroy: (() -> Unit)? = null
        onViewAttachedToWindow {
            destroy = create()
        }
        onViewDetachedFromWindow {
            destroy?.invoke()
        }
    }
}

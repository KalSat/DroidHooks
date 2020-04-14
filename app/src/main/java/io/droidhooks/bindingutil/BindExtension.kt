package io.droidhooks.bindingutil

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlin.reflect.KMutableProperty0

fun <T> LiveData<T>.observe(owner: LifecycleOwner, onChanged: (T?) -> Unit) {
    observe(owner, Observer { onChanged(it) })
}


fun <T : Any, U : T> bind(lifecycleOwner: LifecycleOwner, setter: (T) -> Unit,
                          field: NonNullLiveData<U>) =
        field.observe(lifecycleOwner) { setter(it) }

fun <T : Any, U : Any> bind(lifecycleOwner: LifecycleOwner, setter: (T) -> Unit,
                            field: NonNullLiveData<U>, converter: (U) -> T) =
        field.observe(lifecycleOwner) { setter(converter(it)) }

fun <T : Any, U : T> bind(lifecycleOwner: LifecycleOwner, prop: KMutableProperty0<T>,
                          field: NonNullLiveData<U>) =
        field.observe(lifecycleOwner) { prop.set(it) }

fun <T : Any, U : Any> bind(lifecycleOwner: LifecycleOwner, prop: KMutableProperty0<T>,
                            field: NonNullLiveData<U>, converter: (U) -> T) =
        field.observe(lifecycleOwner) { prop.set(converter(it)) }


inline fun <T : View> T.bindIf(lifecycleOwner: LifecycleOwner, data: NonNullLiveData<Boolean>): T {
    data.observe(lifecycleOwner) { this.visibility = if (data.value) View.VISIBLE else View.GONE }
    return this
}

inline fun <T : View> T.bindShow(lifecycleOwner: LifecycleOwner, data: NonNullLiveData<Boolean>): T {
    data.observe(lifecycleOwner) { this.visibility = if (data.value) View.VISIBLE else View.INVISIBLE }
    return this
}

inline fun <T : ViewGroup> T.addChildren(vararg children: View) {
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

package io.droidhooks.bindingutil

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

open class NonNullLiveData<T>(value: T) : LiveData<T>() {

    init {
        setValue(value)
    }

    public override fun postValue(value: T) = super.postValue(value)

    public final override fun setValue(value: T) = super.setValue(value)

    override fun getValue(): T = super.getValue()!!

    fun observe(owner: LifecycleOwner, onChanged: (T) -> Unit) =
            super.observe(owner, Observer { value -> value?.let { onChanged(it) } })

}

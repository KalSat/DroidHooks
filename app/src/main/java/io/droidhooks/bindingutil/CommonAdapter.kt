package io.droidhooks.bindingutil

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class CommonAdapter<T>(
        lifecycleOwner: LifecycleOwner,
        private val dateList: LiveList<T>,
        private val viewCreator: (data: NonNullLiveData<T>, index: NonNullLiveData<Int>, type: Int) -> View
) : RecyclerView.Adapter<CommonAdapter.InnerViewHolder<T>>() {

    private var getItemType: ((position: Int) -> Int)? = null

    constructor(lifecycleOwner: LifecycleOwner, dateList: LiveList<T>,
                viewCreator: (data: NonNullLiveData<T>, index: NonNullLiveData<Int>, type: Int) -> View,
                getItemType: ((position: Int) -> Int)? = null) : this(lifecycleOwner, dateList, viewCreator) {
        this.getItemType = getItemType
    }

    init {
        dateList.observe(lifecycleOwner, Observer { this.notifyDataSetChanged() })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder<T> {
        val data = InnerLiveData<T>()
        val position = InnerLiveData<Int>()
        val view = viewCreator(data, position, viewType)
        return InnerViewHolder(view, data, position)
    }

    override fun getItemCount(): Int = dateList.value.size

    override fun onBindViewHolder(holder: InnerViewHolder<T>, position: Int) =
            holder.bindView(dateList.value[position], position)

    override fun getItemViewType(position: Int): Int =
            getItemType?.let {
                it(position)
            } ?: run {
                super.getItemViewType(position)
            }

    class InnerViewHolder<T>(
            itemView: View,
            private val data: NonNullLiveData<T>,
            private val position: NonNullLiveData<Int>
    ) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: T?, position: Int) {
            if (data == null) {
                return
            }
            this.data.value = data
            this.position.value = position
        }
    }

    private class InnerLiveData<T>(value: T) : NonNullLiveData<T>(value) {
        constructor() : this(null as T)
    }

}

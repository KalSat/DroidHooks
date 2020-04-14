package io.droidhooks.bindingutil

open class LiveList<E>(value: MutableList<E>) : NonNullLiveData<MutableList<E>>(value), MutableList<E> {

    override val size: Int get() = value.size

    override fun isEmpty(): Boolean = value.isEmpty()

    override fun contains(element: E): Boolean = value.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = value.containsAll(elements)

    override fun get(index: Int): E = value[index]

    override fun indexOf(element: E): Int = value.indexOf(element)

    override fun lastIndexOf(element: E): Int = value.lastIndexOf(element)

    override fun iterator(): MutableIterator<E> = value.iterator()

    override fun listIterator(): MutableListIterator<E> = value.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E> = value.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = value.subList(fromIndex, toIndex)


    override fun add(element: E): Boolean = value.add(element).also { value = value }

    override fun add(index: Int, element: E) = value.add(index, element).also { value = value }

    override fun addAll(index: Int, elements: Collection<E>): Boolean =
            value.addAll(index, elements).also { value = value }

    override fun addAll(elements: Collection<E>): Boolean = value.addAll(elements).also { value = value }

    override fun clear() = value.clear().also { value = value }

    override fun remove(element: E): Boolean = value.remove(element).also { value = value }

    override fun removeAll(elements: Collection<E>): Boolean = value.removeAll(elements).also { value = value }

    override fun removeAt(index: Int): E = value.removeAt(index).also { value = value }

    override fun retainAll(elements: Collection<E>): Boolean = value.retainAll(elements).also { value = value }

    override fun set(index: Int, element: E): E = value.set(index, element).also { value = value }

}

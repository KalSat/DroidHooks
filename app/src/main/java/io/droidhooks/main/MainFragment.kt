package io.droidhooks.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.droidhooks.R
import io.droidhooks.bindingutil.*
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

class MainFragment : Fragment() {
    companion object {
        private const val TAG = "MainFragment"
    }

    private lateinit var mContainer: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val contentView = UI {
            mContainer = verticalLayout {
                lparams(matchParent, matchParent) {
                    padding = dip(16)
                }
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }.view

        mContainer.addChildren(
                switchButton(),
                weekList()
        )

        return contentView
    }

    private fun switchButton(): View {
        val switch: NonNullLiveData<Boolean> = NonNullLiveData(true)

        return UI {
            linearLayout {

                button {
                    bind(owner, ::setText, switch) {
                        if (it) "ON" else "OFF"
                    }
                    onClick {
                        switch.postValue(!switch.value)
                    }
                }.lparams(wrapContent, wrapContent) {
                    marginStart = dip(32)
                }

                space {}.lparams(0, wrapContent) {
                    weight = 1f
                }

                imageView {
                    imageResource = R.mipmap.ic_launcher
                    bindIf(owner, switch)
                }.lparams(wrapContent, wrapContent) {
                    marginEnd = dip(32)
                }

            }
        }.view
    }

    private fun weekList(): View {
        val weekList: LiveList<String> = LiveList(mutableListOf(
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday",
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        ))

        return UI {
            relativeLayout {
                lparams(wrapContent, wrapContent)

                button("ADD") {
                    id = R.id.btn_add
                    onClick {
                        weekList.add("Weekday")
                    }
                }.lparams(wrapContent, wrapContent) {
                    alignParentTop()
                    alignParentStart()
                    marginStart = dip(32)
                }

                button("REMOVE") {
                    onClick {
                        weekList.removeAt(weekList.size - 1)
                    }
                }.lparams(wrapContent, wrapContent) {
                    alignParentTop()
                    alignParentEnd()
                    marginEnd = dip(32)
                }

                recyclerView {
                    backgroundColor = 0x7f66ccff
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

                    adapter = CommonAdapter(owner, weekList) { week, index, _ ->
                        UI {
                            linearLayout {
                                lparams(matchParent, dip(80))
                                gravity = Gravity.CENTER
                                bind(owner, ::setBackgroundColor, index) {
                                    if (it == 0 || it == weekList.size - 1) 0x7fff0000 else 0
                                }
                                onClick {}

                                textView {
                                    bind(owner, ::setText, week)
                                }
                            }
                        }.view
                    }
                }.lparams(matchParent, matchParent) {
                    bottomOf(R.id.btn_add)
                    topMargin = dip(10)
                }
            }
        }.view
    }

}

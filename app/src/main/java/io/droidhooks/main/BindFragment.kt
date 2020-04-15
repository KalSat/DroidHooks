package io.droidhooks.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.droidhooks.bindingutil.*
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import kotlin.concurrent.timer

class BindFragment : Fragment() {
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
        )

        return contentView
    }

    fun welcome(props: Map<String, Any>): View {
        return UI {
            textView("Hello, ${props["name"]}") { }
        }.view
    }

    fun simpleWeekList(): View {
        val weekList: LiveList<String> = LiveList(mutableListOf(
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        ))

        return UI {
            recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = CommonAdapter(owner, weekList) { week, _, _ ->
                    UI {
                        textView {
                            bind(owner, ::setText, week)
                        }
                    }.view
                }
            }
        }.view
    }

    fun counter(props: Map<String, Any>): View {
        val (count, setCount) = useState(0)

        return UI {
            verticalLayout {
                textView {
                    bind(owner, ::setText, count) {
                        "You clicked $it times"
                    }
                }
                button("Click me") {
                    onClick { setCount(count.value + 1) }
                }
            }
        }.view
    }

    fun timer(props: Map<String, Any>): View {
        val (count, setCount) = useState(0)

        return UI {
            textView {
                bind(owner, ::setText, count) { "$it" }

                useEffect {
                    val timer = timer(period = 1000) {
                        setCount(count.value + 1)
                    }
                    return@useEffect { timer.cancel() }
                }
            }
        }.view
    }

}

package io.droidhooks

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import io.droidhooks.main.MainFragment
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.titleResource
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.themedAppBarLayout

class MainActivity : AppCompatActivity() {

    private lateinit var mFragmentManager: FragmentManager
    private val mCategoriesFragment = MainFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var toolbarView: Toolbar? = null
        verticalLayout {
            themedAppBarLayout(R.style.AppTheme_AppBarOverlay) {

                toolbarView = toolbar {
                    backgroundColor = colorAttr(R.attr.colorPrimary)
                    titleResource = R.string.app_name
                    popupTheme = R.style.AppTheme_PopupOverlay
                }.lparams(matchParent, dimenAttr(R.attr.actionBarSize))

            }.lparams(matchParent, wrapContent)

            frameLayout {
                id = R.id.container
            }.lparams(matchParent, dip(0)) {
                weight = 1f
            }
        }

        setSupportActionBar(toolbarView)

        mFragmentManager = supportFragmentManager
        mFragmentManager.beginTransaction()
                .add(R.id.container, mCategoriesFragment)
                .commit()
    }

}

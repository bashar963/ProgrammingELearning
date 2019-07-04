package com.devbashar.programminglearning.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.fragments.ClassWorkFragment
import com.devbashar.programminglearning.fragments.DiscussionFragment
import com.devbashar.programminglearning.fragments.MembersFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_class_room.*

class ClassRoom : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme2)
        setContentView(R.layout.activity_class_room)
        initToolbar()
        val adapter = TabbedAdapter(supportFragmentManager)
        view_pager.adapter = adapter
        tab_layout.setupWithViewPager(view_pager)


    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = intent.getStringExtra("className")
    }
}



class TabbedAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> DiscussionFragment.newInstance()
        1 -> ClassWorkFragment.newInstance()
        2 -> MembersFragment.newInstance()
        else -> DiscussionFragment.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Discussion"
        1 -> "Class Work"
        2 -> "Members"
        else -> "Discussion"
    }

    override fun getCount(): Int = 3
}

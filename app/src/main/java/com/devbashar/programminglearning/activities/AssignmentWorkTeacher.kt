package com.devbashar.programminglearning.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.fragments.Instructions
import com.devbashar.programminglearning.fragments.StudentWork
import kotlinx.android.synthetic.main.activity_assignment_work_teacher.*

class AssignmentWorkTeacher : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme2)
        setContentView(R.layout.activity_assignment_work_teacher)

        initToolbar()
        val adapter = TabbedAdapter2(supportFragmentManager)

        view_pager.adapter = adapter
        tab_layout.setupWithViewPager(view_pager)

    }
    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }
}

class TabbedAdapter2(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> Instructions.newInstance()
        1 -> StudentWork.newInstance()
        else -> Instructions.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "INSTRUCTIONS"
        1 -> "STUDENT Work"
        else -> "INSTRUCTIONS"
    }

    override fun getCount(): Int = 2
}

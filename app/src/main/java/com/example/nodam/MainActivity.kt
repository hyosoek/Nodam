package com.example.nodam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = MainHomeFragment() // MyFragment는 사용자가 만든 프래그먼트 클래스입니다
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        swapFragmentEvent()

    }


    fun swapFragmentEvent(){
        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        val graphBtn = findViewById<ImageView>(R.id.graphBtn)
        val profileBtn = findViewById<ImageView>(R.id.profileBtn)

        homeBtn.setOnClickListener {
            val fragment = MainHomeFragment() // MyFragment는 사용자가 만든 프래그먼트 클래스입니다
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            setBtnColorGray()
            homeBtn.setImageResource(R.mipmap.home_white)
        }
        graphBtn.setOnClickListener {
            val fragment = MainGraphFragment() // MyFragment는 사용자가 만든 프래그먼트 클래스입니다
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            setBtnColorGray()
            graphBtn.setImageResource(R.mipmap.graph_white)
        }
        profileBtn.setOnClickListener {
            val fragment = MainProfileFragment() // MyFragment는 사용자가 만든 프래그먼트 클래스입니다
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            setBtnColorGray()
            profileBtn.setImageResource(R.mipmap.profile_white)
        }
    }
    fun setBtnColorGray(){
        setHomeBtnColorGray()
        setGraphBtnColorGray()
        setProfileBtnColorGray()
    }

    fun setHomeBtnColorGray(){
        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        homeBtn.setImageResource(R.mipmap.home)
    }

    fun setGraphBtnColorGray(){
        val graphBtn = findViewById<ImageView>(R.id.graphBtn)
        graphBtn.setImageResource(R.mipmap.graph)
    }

    fun setProfileBtnColorGray(){
        val profileBtn = findViewById<ImageView>(R.id.profileBtn)
        profileBtn.setImageResource(R.mipmap.profile)
    }


}
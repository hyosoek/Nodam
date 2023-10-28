package com.example.nodam
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class MainProfileFragment : Fragment() {

    // 프래그먼트가 화면에 표시될 때 호출되는 메소드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 레이아웃을 인플레이트하고 반환합니다.
        val view =  inflater.inflate(R.layout.fragment_main_profile, container, false)
        addProfileSettingView(view)
        return view
    }
    fun addProfileSettingView(view : View) {
        val linearLayoutInScroll = view.findViewById<LinearLayout>(R.id.linearLayoutInScroll)
        addDailySmokeMaxEditView(view,linearLayoutInScroll)
        makeLineView(view,linearLayoutInScroll)
        addVersionInformationView(view,linearLayoutInScroll)
    }

    fun makeLineView(view : View, linearLayoutInScroll: LinearLayout){
        val separator = View(requireContext())

        val separatorHeightInDp = 1
        val density = resources.displayMetrics.density
        val separatorHeightInPixels = (separatorHeightInDp * density + 0.5f).toInt()
        separator.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            separatorHeightInPixels
        )
        separator.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.silver))
        linearLayoutInScroll.addView(separator)
    }
    fun addDailySmokeMaxEditView(view : View, linearLayoutInScroll: LinearLayout){
        val content = layoutInflater.inflate(R.layout.view_profile_setting, linearLayoutInScroll, false)
        content.findViewById<TextView>(R.id.settingName).text = "일일 흡연 횟수 설정"
        linearLayoutInScroll.addView(content)
    }

    fun addVersionInformationView(view : View, linearLayoutInScroll: LinearLayout){
        val content = layoutInflater.inflate(R.layout.view_profile_setting, linearLayoutInScroll, false)
        content.findViewById<TextView>(R.id.settingName).text = "버전 정보"
        linearLayoutInScroll.addView(content)
    }

}

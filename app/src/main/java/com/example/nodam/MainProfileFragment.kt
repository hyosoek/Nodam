package com.example.nodam
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
    fun addProfileSettingView(view : View) {
        val linearLayoutInScroll = view.findViewById<LinearLayout>(R.id.linearLayoutInScroll)
        setProfileName(view)
        editProfileNameBtnEvent(view)

        addDeviceConnectEditView(view,linearLayoutInScroll)
        makeLineView(view,linearLayoutInScroll)
        addDailySmokeMaxEditView(view,linearLayoutInScroll)
        makeLineView(view,linearLayoutInScroll)
        addLocationTermsOfUseView(view,linearLayoutInScroll)
        makeLineView(view,linearLayoutInScroll)
        addVersionInformationView(view,linearLayoutInScroll)
    }
    fun setProfileName(view:View){
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("name", "사용자 이름")
        view.findViewById<TextView>(R.id.userName).text = username
    }
    fun addDeviceConnectEditView(view : View, linearLayoutInScroll: LinearLayout){
        val content = layoutInflater.inflate(R.layout.view_profile_setting, linearLayoutInScroll, false)
        content.findViewById<TextView>(R.id.settingName).text = "기기 연결 설정"
        linearLayoutInScroll.addView(content)
        //다른 프래그먼트 생성해야할 듯
    }
    fun addDailySmokeMaxEditView(view : View, linearLayoutInScroll: LinearLayout){
        val content = layoutInflater.inflate(R.layout.view_profile_setting, linearLayoutInScroll, false)
        content.findViewById<TextView>(R.id.settingName).text = "일일 흡연 횟수 설정"
        linearLayoutInScroll.addView(content)
        content.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("흡연 횟수 설정")

// LinearLayout을 생성하고 설정
            val linearLayout = LinearLayout(requireContext())
            linearLayout.orientation = LinearLayout.VERTICAL

            val inputEditText = EditText(requireContext())
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val marginInDp = 20 // 20dp의 margin을 설정
            val scale = resources.displayMetrics.density
            val marginInPixels = (marginInDp * scale + 0.5f).toInt()
            layoutParams.setMargins(marginInPixels, 0, marginInPixels, 0)
            inputEditText.layoutParams = layoutParams

            val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
            val username = sharedPreferences.getInt("smokeMaxCount", -1)
            inputEditText.hint = username.toString()

            linearLayout.addView(inputEditText)
            builder.setView(linearLayout)
            builder.setPositiveButton("저장") { dialog, which ->

                try {
                    val editor = sharedPreferences.edit()
                    editor.putInt("smokeMaxCount", inputEditText.text.toString().toInt())
                    editor.apply()
                    setProfileName(view)
                } catch (error : NumberFormatException) {
                }
            }

            builder.setNegativeButton("취소") { dialog, which -> }

            builder.show()
        }
    }
    fun addLocationTermsOfUseView(view : View, linearLayoutInScroll: LinearLayout){
        val content = layoutInflater.inflate(R.layout.view_profile_setting, linearLayoutInScroll, false)
        content.findViewById<TextView>(R.id.settingName).text = "위치 정보 이용약관"
        linearLayoutInScroll.addView(content)
        content.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("준비 중입니다.")
                .setMessage("이용약관이 어쩌고...")
            builder.show()
        }
    }
    fun addVersionInformationView(view : View, linearLayoutInScroll: LinearLayout){
        val content = layoutInflater.inflate(R.layout.view_profile_setting, linearLayoutInScroll, false)
        val leftText = content.findViewById<TextView>(R.id.settingName)
        leftText.text = "버전 정보"
        linearLayoutInScroll.addView(content)

        val rightText = TextView(requireContext())
        rightText.text = "v1.0.0"
        val silverColor = ContextCompat.getColor(requireContext(), R.color.white)
        rightText.setTextColor(silverColor)

// TextView를 content 레이아웃에 추가
        val contentLayout = content as ConstraintLayout // 혹은 content 레이아웃의 실제 타입에 따라 적절히 캐스트하세요.
        contentLayout.addView(rightText)

// TextView의 제약 조건 설정 (예: 부모의 상단에 연결)
        val layoutParams = rightText.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToTop = ConstraintSet.PARENT_ID // 위 부분을 부모에 연결
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID
        layoutParams.endToEnd = ConstraintSet.PARENT_ID
        rightText.layoutParams = layoutParams

// 레이아웃 갱신
        contentLayout.requestLayout()
    }

    fun editProfileNameBtnEvent(view:View){
        val content = view.findViewById<ImageButton>(R.id.profileEditBtn)
        content.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("이름 설정")

// LinearLayout을 생성하고 설정
            val linearLayout = LinearLayout(requireContext())
            linearLayout.orientation = LinearLayout.VERTICAL

            val inputEditText = EditText(requireContext())
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val marginInDp = 20 // 20dp의 margin을 설정
            val scale = resources.displayMetrics.density
            val marginInPixels = (marginInDp * scale + 0.5f).toInt()
            layoutParams.setMargins(marginInPixels, 0, marginInPixels, 0)
            inputEditText.layoutParams = layoutParams

            val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
            val username = sharedPreferences.getString("name", "사용자 이름")
            inputEditText.hint = username

            linearLayout.addView(inputEditText)

            builder.setView(linearLayout)

            builder.setPositiveButton("저장") { dialog, which ->
                val editor = sharedPreferences.edit()
                editor.putString("name", inputEditText.text.toString())
                editor.apply()
                setProfileName(view)
            }

            builder.setNegativeButton("취소") { dialog, which -> }

            builder.show()
        }
    }
}

package com.example.nodam
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class MainGraphFragment : Fragment() {

    data class CommitData(val date: String, val commitNum: Int)

    // 프래그먼트가 화면에 표시될 때 호출되는 메소드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 레이아웃을 인플레이트하고 반환합니다.
        val view =  inflater.inflate(R.layout.fragment_main_graph, container, false)

        setWeeklyGraphData(view)
        setGraphBtnEvent(view)
        return view
    }

    fun setWeeklyGraphData(view : View){
        defaultBtnColorSet(view)

        val btn = view.findViewById<Button>(R.id.weeklyBtn)
        btn.setBackgroundResource(R.color.white)
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()

        //오늘 데이터 추가하기
        val currentDate = calendar.time
        dateFormat.format(currentDate)

        val dataList = mutableListOf<CommitData>()
        // 최근 7일 동안의 날짜
        for (i in 1 until 8) {
            val recentDate = calendar.time
            val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
            val smokeTime = sharedPreferences.getInt(dateFormat.format(recentDate), 0) //오늘의 최대값을 가져오기
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dayNames = arrayOf("일", "월", "화", "수", "목", "금", "토")
            val koreanDayOfWeek = dayNames[dayOfWeek - 1]
            dataList.add(CommitData(koreanDayOfWeek,smokeTime))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        setGraphData(view, dataList.reversed())
    }
    fun setMonthlyGraphData(view : View){
        defaultBtnColorSet(view)

        val btn = view.findViewById<Button>(R.id.monthlyBtn)
        btn.setBackgroundResource(R.color.white)
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()

        //오늘 데이터 추가하기
        val currentDate = calendar.time
        dateFormat.format(currentDate)

        val dataList = mutableListOf<CommitData>()
        // 최근 7일 동안의 날짜
        for(j in 1 until 6){ //최근 5주간의 데이터를 파싱
            var stack = 0
            var start = ""
            for (i in 1 until 8) {
                val recentDate = calendar.time
                val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                val smokeTime = sharedPreferences.getInt(dateFormat.format(recentDate), 0) //오늘의 최대값을 가져오기
                stack += smokeTime
                if(i == 1){
                    start = dateFormat.format(recentDate).substring(5, 10)
                }
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
            dataList.add(CommitData("~"+start,stack))
        }

        setGraphData(view, dataList.reversed())
    }
    fun setQuarterGraphData(view : View){
        defaultBtnColorSet(view)

        val btn = view.findViewById<Button>(R.id.quaterBtn)
        btn.setBackgroundResource(R.color.white)
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()

        //오늘 데이터 추가하기
        val currentDate = calendar.time
        dateFormat.format(currentDate)

        val dataList = mutableListOf<CommitData>()
        // 최근 7일 동안의 날짜
        for(j in 1 until 4) {
            for (j in 1 until 6) { //최근 5주간의 데이터를 파싱
                var stack = 0
                for (i in 1 until 8) {
                    val recentDate = calendar.time
                    val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                    val smokeTime = sharedPreferences.getInt(dateFormat.format(recentDate), 0) //오늘의 최대값을 가져오기
                    stack += smokeTime
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                }
                val recentDate = calendar.time //이렇게 하면 가장 먼 날이 되는데....
                dataList.add(CommitData(dateFormat.format(recentDate).toString().substring(8,10), stack))

            }
        }

        setGraphData(view, dataList.reversed())
    }
    fun setAnnuallyGraphData(view : View){ //  달 별로 parsing하는게 좋을 듯
        defaultBtnColorSet(view)

        val btn = view.findViewById<Button>(R.id.annuallyBtn)
        btn.setBackgroundResource(R.color.white)
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()

        //오늘 데이터 추가하기
        val currentDate = calendar.time
        dateFormat.format(currentDate)

        val dataList = mutableListOf<CommitData>()
        // 최근 7일 동안의 날짜
        val recentDate = calendar.time
        var month = dateFormat.format(recentDate).substring(5,7) //현재 달을 출력
        var stack = 0
        for(i in 0 until 365 ){ //최근 5주간의 데이터를 파싱
            val recentDate = calendar.time
            if(dateFormat.format(recentDate).substring(5,7) != month){
                Log.d("Test_code","${recentDate}")
                dataList.add(CommitData(month.toString(),stack))
                stack = 0
                month = dateFormat.format(recentDate).substring(5,7)
            }
            val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
            val smokeTime = sharedPreferences.getInt(dateFormat.format(recentDate), 0) //오늘의 최대값을 가져오기
            stack += smokeTime
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        setGraphData(view, dataList.reversed())
    }

    fun setGraphData(view : View, dataList: List<CommitData>){
        try{
            val linechart = view.findViewById<LineChart>(R.id.line_chart)
            val xAxis = linechart.xAxis

            //데이터 가공
            //y축
            val entries: MutableList<Entry> = mutableListOf()
            for (i in dataList.indices){
                entries.add(Entry(i.toFloat(), dataList[i].commitNum.toFloat()))
            }
            val lineDataSet = LineDataSet(entries,"entries")

            lineDataSet.apply {
                color = resources.getColor(R.color.black, null)
                circleRadius = 5f
                lineWidth = 3f
                setCircleColor(resources.getColor(R.color.black, null))
                circleHoleColor = resources.getColor(R.color.black, null)
                setDrawHighlightIndicators(false)
                setDrawValues(true) // 숫자표시
                valueTextColor = resources.getColor(R.color.black, null)
                valueFormatter = DefaultValueFormatter(0)  // 소숫점 자릿수 설정
                valueTextSize = 20f
            }

            //차트 전체 설정
            linechart.apply {
                axisRight.isEnabled = false   //y축 사용여부
                axisLeft.isEnabled = false
                legend.isEnabled = false    //legend 사용여부
                description.isEnabled = false //주석
                isDragXEnabled = true   // x 축 드래그 여부
                isScaleYEnabled = false //y축 줌 사용여부
                isScaleXEnabled = false//x축 줌 사용여부
            }
            //X축 설정


            xAxis.apply {
                setDrawGridLines(false)
                setDrawAxisLine(true)
                setDrawLabels(true)
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = XAxisCustomFormatter(changeDateText(dataList))
                textColor = resources.getColor(R.color.black, null)
                textSize = 10f
                labelRotationAngle = 0f

                var listSize = 0
                GlobalScope.launch(Dispatchers.Main) {
                    listSize = dataList.size
                    setLabelCount(listSize, true)
                    linechart.apply {
                        invalidate() // view갱신
                    }
                }
                //setLabelCount(listSize, true)
            }

            val horizontalScrollView =
                view.findViewById<HorizontalScrollView>(R.id.horizontal_scroll_view)
            horizontalScrollView.post {
                horizontalScrollView.scrollTo(
                    linechart.width,
                    0
                )
            }

            linechart.apply {
                data = LineData(lineDataSet)
                notifyDataSetChanged() //데이터 갱신
                invalidate() // view갱신
            }
        }catch (err : IndexOutOfBoundsException){
            Log.d("error_on_graph_logic","IndexOutOfBoundsException")
            setGraphData(view,dataList)
        }

    }
    fun changeDateText(dataList: List<CommitData>): List<String> {
        val dataTextList = ArrayList<String>()
        for (i in dataList.indices) {
            val dateText = dataList[i].date
            dataTextList.add(dateText)
        }
        return dataTextList
    }
    class XAxisCustomFormatter(val xAxisData: List<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return xAxisData[(value).toInt()]
        }

    }

    fun defaultBtnColorSet(view:View){
        val weeklyBtn = view.findViewById<Button>(R.id.weeklyBtn)
        val monthlyBtn = view.findViewById<Button>(R.id.monthlyBtn)
        val quarterBtn = view.findViewById<Button>(R.id.quaterBtn)
        val annuallyBtn = view.findViewById<Button>(R.id.annuallyBtn)
        weeklyBtn.setBackgroundResource(R.color.gray)
        monthlyBtn.setBackgroundResource(R.color.gray)
        quarterBtn.setBackgroundResource(R.color.gray)
        annuallyBtn.setBackgroundResource(R.color.gray)
        weeklyBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        monthlyBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        quarterBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        annuallyBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }
    fun setGraphBtnEvent(view:View){
        val weeklyBtn = view.findViewById<Button>(R.id.weeklyBtn)
        val monthlyBtn = view.findViewById<Button>(R.id.monthlyBtn)
        val quarterBtn = view.findViewById<Button>(R.id.quaterBtn)
        val annuallyBtn = view.findViewById<Button>(R.id.annuallyBtn)

        weeklyBtn.setOnClickListener{
            setWeeklyGraphData(view)
        }
        monthlyBtn.setOnClickListener{
            setMonthlyGraphData(view)
        }
        quarterBtn.setOnClickListener {
            setQuarterGraphData(view)
        }
        annuallyBtn.setOnClickListener {
            setAnnuallyGraphData(view)
        }
    }

}





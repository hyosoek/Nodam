package com.example.nodam
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class MainGraphFragment : Fragment() {

    data class CommitData(val date: String, val commitNum: Int)

    // 프래그먼트가 화면에 표시될 때 호출되는 메소드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 레이아웃을 인플레이트하고 반환합니다.
        val view =  inflater.inflate(R.layout.fragment_main_graph, container, false)

        setGraphData(view)
        return view
    }


    fun setGraphData(view : View){
        val dataList: List<CommitData> = listOf(
            CommitData("08-28",3),
            CommitData("08-29",2),
            CommitData("08-30",5),
            CommitData("08-31",2),
            CommitData("09-01",3),
            CommitData("09-02",6),
            CommitData("09-03",7),
            CommitData("09-04",1),
            CommitData("09-05",3),
            CommitData("09-06",2)
        )

        val linechart = view.findViewById<LineChart>(R.id.line_chart)
        val xAxis = linechart.xAxis

        //데이터 가공
        //y축
        val entries: MutableList<Entry> = mutableListOf()
        for (i in dataList.indices){
            entries.add(Entry(i.toFloat(), dataList[i].commitNum.toFloat()))
        }
        val lineDataSet =LineDataSet(entries,"entries")

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
            valueTextSize = 10f
        }

        //차트 전체 설정
        linechart.apply {
            axisRight.isEnabled = false   //y축 사용여부
            axisLeft.isEnabled = false
            legend.isEnabled = false    //legend 사용여부
            description.isEnabled = false //주석
            isDragXEnabled = true   // x 축 드래그 여부
            isScaleYEnabled = false //y축 줌 사용여부
            isScaleXEnabled = false //x축 줌 사용여부
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
            setLabelCount(10, true)
        }

        val horizontalScrollView = view.findViewById<HorizontalScrollView>(R.id.horizontal_scroll_view)
        horizontalScrollView.post{
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


    }
    fun changeDateText(dataList: List<CommitData>): List<String> {
        val dataTextList = ArrayList<String>()
        for (i in dataList.indices) {
            val textSize = dataList[i].date.length
            val dateText = dataList[i].date.substring(textSize - 2, textSize)
            if (dateText == "01") {
                dataTextList.add(dataList[i].date)
            } else {
                dataTextList.add(dateText)
            }
        }
        return dataTextList
    }

    class XAxisCustomFormatter(val xAxisData: List<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return xAxisData[(value).toInt()]
        }

    }
}





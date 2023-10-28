package com.example.nodam

import java.text.SimpleDateFormat
import java.util.Date

class DateParse {
    fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd") // 날짜 형식을 원하는 대로 지정할 수 있습니다.
        return dateFormat.format(currentDate)
    }
}
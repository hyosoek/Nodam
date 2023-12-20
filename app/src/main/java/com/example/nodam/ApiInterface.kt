package com.example.nodam

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("nodam")
    fun postData(@Body requestBody: ApiRequestData):Call<ApiResponseData>
}
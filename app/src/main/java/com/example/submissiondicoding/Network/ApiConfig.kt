package com.example.submissiondicoding.Network

import com.example.submissiondicoding.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object{
        fun getApiService(): ApiService {
           val client by lazy {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val secretKey = BuildConfig.KEY
                        val requestBuilder = original.newBuilder()
                            .header("Authorization","token $secretKey")
                        val request = requestBuilder.build()
                        chain.proceed(request)
                    }
                    .build()
            }

             val retrofitBuilder: Retrofit.Builder by lazy {
                Retrofit.Builder()
                    .baseUrl("https://api.github.com")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
            }

            return retrofitBuilder.build().create(ApiService::class.java)
        }
    }

}
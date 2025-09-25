package com.cforce.reminder.data

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
	private const val BASE_URL = "https://codeforces.com/"

	private val logging: HttpLoggingInterceptor by lazy {
		HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
	}

	private val okHttp: OkHttpClient by lazy {
		OkHttpClient.Builder()
			.addInterceptor(logging)
			.build()
	}

	private val moshi: Moshi by lazy {
		Moshi.Builder()
			// No need for KotlinJsonAdapterFactory when using codegen
			.build()
	}

	private val retrofit: Retrofit by lazy {
		Retrofit.Builder()
			.baseUrl(BASE_URL)
			.client(okHttp)
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.build()
	}

	val service: CodeforcesService by lazy { retrofit.create(CodeforcesService::class.java) }
}

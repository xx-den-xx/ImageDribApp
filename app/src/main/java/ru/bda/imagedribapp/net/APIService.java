package ru.bda.imagedribapp.net;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.bda.imagedribapp.constants.APIData;
import ru.bda.imagedribapp.net.interfaces.API;

/**
 * Created by User on 09.02.2017.
 */

public class APIService {

    public static API getDribbbleShotService(final String authToken) {
        API api = getRestAdapter(authToken).create(API.class);
        return api;
    }

    private static Retrofit getRestAdapter(final String authToken) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request authRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer " + authToken).build();
                Response response = chain.proceed(authRequest);
                return response;
            }
        }).build();

        return new Retrofit.Builder()
                .baseUrl(APIData.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

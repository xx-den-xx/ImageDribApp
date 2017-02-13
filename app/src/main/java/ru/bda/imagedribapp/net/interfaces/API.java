package ru.bda.imagedribapp.net.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.bda.imagedribapp.entity.Shot;

/**
 * Created by User on 09.02.2017.
 */

public interface API {
    @GET("shots")
    Call<List<Shot>> getShots(@Query("access_token") String accessToken, @Query("per_page") int perPage);

    @GET("shots")
    Call<List<Shot>> getShots(@Query("per_page") int perPage, @Query("animate") boolean animate,
                              @Query("images") String images);
}

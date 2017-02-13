package ru.bda.imagedribapp.net.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.bda.imagedribapp.entity.Shot;

public interface API {

    @GET("shots")
    Call<List<Shot>> getShots(@Query("per_page") int perPage, @Query("animate") boolean animate,
                              @Query("images") String images);
}

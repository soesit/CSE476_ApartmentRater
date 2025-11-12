package com.example.apartmentrater;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("apartments/{placeId}/reviews/")
    Call<Void> postReview(
            @Path("placeId") String placeId,
            @Header("Authorization") String token,
            @Body Map<String, String> body
    );
}

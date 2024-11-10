package agh.edu.pl.healthmonitoringsystemai.client;

import agh.edu.pl.healthmonitoringsystemai.util.LocalDateTimeAdapter;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class RetrofitClient {

    private final String SERVER_BASE_URL;

    public RetrofitClient(@Value("${hms.api.base-url}") String hmsApiBaseUrl) {
        this.SERVER_BASE_URL = hmsApiBaseUrl;
    }

    public Retrofit getRetrofitClient() {
        OkHttpClient httpClient = new OkHttpClient();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return new Retrofit.Builder()
                .baseUrl(SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }
}

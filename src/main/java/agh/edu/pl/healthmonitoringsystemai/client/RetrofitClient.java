package agh.edu.pl.healthmonitoringsystemai.client;

import agh.edu.pl.healthmonitoringsystemai.exception.ApiException;
import agh.edu.pl.healthmonitoringsystemai.exception.response.ErrorResponse;
import agh.edu.pl.healthmonitoringsystemai.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RetrofitClient {

    private final String SERVER_BASE_URL;

    public RetrofitClient(@Value("${hms.api.base-url}") String hmsApiBaseUrl) {
        this.SERVER_BASE_URL = hmsApiBaseUrl;
    }

    public Retrofit getRetrofitClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Response response = chain.proceed(chain.request());

                    if (!response.isSuccessful()) {
                        handleErrorResponse(response);
                    }
                    return response;
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        return new Retrofit.Builder()
                .baseUrl(SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }


    private void handleErrorResponse(okhttp3.Response response) throws ApiException {
        int statusCode = response.code();
        String errorMessage = "Unknown error occurred.";
        String errorBody = null;

        try {
            ResponseBody body = response.peekBody(Long.MAX_VALUE);
            if (body != null) {
                errorBody = body.string();
                ErrorResponse apiError = new Gson().fromJson(errorBody, ErrorResponse.class);

                if (apiError != null && apiError.getMessage() != null) {
                    errorMessage = apiError.getMessage();
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse error response: {}", e.getMessage(), e);
        }

        log.error("API returned an error. Status code: {}, Error body: {}", statusCode, errorBody);
        throw new ApiException(errorMessage, statusCode);
    }
}
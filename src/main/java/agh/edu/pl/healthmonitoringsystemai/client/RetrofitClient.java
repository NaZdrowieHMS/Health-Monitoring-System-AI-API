//package agh.edu.pl.healthmonitoringsystemai.client;
//
//import agh.edu.pl.healthmonitoringsystemai.exception.ApiException;
//import agh.edu.pl.healthmonitoringsystemai.exception.response.ErrorResponse;
//import agh.edu.pl.healthmonitoringsystemai.util.LocalDateTimeAdapter;
//import okhttp3.Interceptor;
//import okhttp3.OkHttpClient;
//import okhttp3.Response;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//@Component
//public class RetrofitClient {
//
//    private final String SERVER_BASE_URL;
//
//    public RetrofitClient(@Value("${hms.api.base-url}") String hmsApiBaseUrl) {
//        this.SERVER_BASE_URL = hmsApiBaseUrl;
//    }
//
//    public Retrofit getRetrofitClient() {
//        OkHttpClient httpClient = new OkHttpClient.Builder()
//                .addInterceptor(new ErrorHandlingInterceptor())
//                .build();
//
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
//                .create();
//
//        return new Retrofit.Builder()
//                .baseUrl(SERVER_BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(httpClient)
//                .build();
//    }
//
//    private static class ErrorHandlingInterceptor implements Interceptor {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Response response = chain.proceed(chain.request());
//            if (!response.isSuccessful()) {
//                ErrorResponse apiError = parseError(response);
//                if (apiError == null) {
//                    throw new ApiException("API Error: Unknown error occurred.");
//                } else {
//                    throw new ApiException("API Error: " + apiError.getMessage() + " (code: " + apiError.getStatusCode() + ")");
//                }
//            }
//            return response;
//        }
//
//        private ErrorResponse parseError(Response response) {
//            try {
//                if (response.body() != null) {
//                    return new Gson().fromJson(response.body().charStream(), ErrorResponse.class);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
//}
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
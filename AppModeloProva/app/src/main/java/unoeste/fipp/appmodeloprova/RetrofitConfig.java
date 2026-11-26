package unoeste.fipp.appmodeloprova;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {
    // Para usar em um aparelho, informe o IPv4 do computador que roda o Spring.
    private static final String URL = "http://10.0.2.2:8080/apis/";
    private final Retrofit retrofit;

    public RetrofitConfig() {
        retrofit = new Retrofit.Builder().baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public MovieService getMovieService() {
        return retrofit.create(MovieService.class);
    }
}

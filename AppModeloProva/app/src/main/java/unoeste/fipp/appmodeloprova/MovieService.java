package unoeste.fipp.appmodeloprova;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MovieService {
    @GET("get-by-year/{year}")
    Call<List<Movie>> buscarPorAno(@Path("year") String ano);

    @POST("add-movie")
    Call<Movie> cadastrar(@Body Movie filme);
}

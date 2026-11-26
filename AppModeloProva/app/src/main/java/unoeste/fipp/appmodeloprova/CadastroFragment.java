package unoeste.fipp.appmodeloprova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroFragment extends Fragment {
    private Button btCadastrar;
    private TextView tvMensagem;
    private Call<Movie> cadastro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro, container, false);
        btCadastrar = view.findViewById(R.id.btCadastrar);
        tvMensagem = view.findViewById(R.id.tvMensagemCadastro);
        btCadastrar.setOnClickListener(v -> cadastrarFilme(view));
        return view;
    }

    private void cadastrarFilme(View view) {
        EditText txTitulo = view.findViewById(R.id.txTitulo);
        EditText txAno = view.findViewById(R.id.txAnoCadastro);
        EditText txImdb = view.findViewById(R.id.txImdb);

        Movie filme = new Movie();
        filme.title = txTitulo.getText().toString().trim();
        filme.year = txAno.getText().toString().trim();
        filme.imdbId = txImdb.getText().toString().trim();

        if (filme.title.isEmpty()) {
            txTitulo.setError("Informe o título");
            return;
        }
        if (!filme.year.matches("[0-9]{4}")) {
            txAno.setError("Digite um ano com quatro dígitos");
            return;
        }
        if (filme.imdbId.isEmpty()) {
            txImdb.setError("Informe o IMDb");
            return;
        }

        filme.shortSummary = lerCampo(view, R.id.txSinopse);
        filme.genres = lerCampo(view, R.id.txGeneros);
        filme.runtime = lerCampo(view, R.id.txDuracao);
        filme.youTubeTrailer = lerCampo(view, R.id.txTrailer);
        filme.rating = lerCampo(view, R.id.txNota);
        filme.moviePoster = lerCampo(view, R.id.txPoster);
        filme.director = lerCampo(view, R.id.txDirecao);
        filme.writers = lerCampo(view, R.id.txRoteiristas);
        filme.cast = lerCampo(view, R.id.txElenco);

        btCadastrar.setEnabled(false);
        tvMensagem.setText("Cadastrando...");
        cadastro = new RetrofitConfig().getMovieService().cadastrar(filme);
        cadastro.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (call != cadastro || call.isCanceled())
                    return;

                btCadastrar.setEnabled(true);
                if (response.isSuccessful())
                    tvMensagem.setText("Filme cadastrado");
                else if (response.code() == 400)
                    tvMensagem.setText("IMDb já cadastrado");
                else
                    tvMensagem.setText("Erro ao cadastrar o filme");
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                if (call == cadastro && !call.isCanceled()) {
                    btCadastrar.setEnabled(true);
                    tvMensagem.setText("Erro ao acessar o servidor");
                }
            }
        });
    }

    private String lerCampo(View view, int id) {
        EditText campo = view.findViewById(id);
        return campo.getText().toString().trim();
    }

    @Override
    public void onDestroyView() {
        if (cadastro != null)
            cadastro.cancel();
        cadastro = null;
        super.onDestroyView();
    }
}

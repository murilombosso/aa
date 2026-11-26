package unoeste.fipp.appmodeloprova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesquisaFragment extends Fragment {
    private TextView tvMensagem;
    private FilmeAdapter adapter;
    private Call<List<Movie>> consulta;
    private String anoPesquisado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);
        EditText txAno = view.findViewById(R.id.txAno);
        Button btPesquisar = view.findViewById(R.id.btPesquisar);
        tvMensagem = view.findViewById(R.id.tvMensagem);
        ListView lvFilmes = view.findViewById(R.id.lvFilmes);

        adapter = new FilmeAdapter(requireContext());
        lvFilmes.setAdapter(adapter);
        lvFilmes.setOnItemClickListener((parent, item, position, id) -> {
            Movie filme = adapter.getItem(position);
            ((MainActivity) requireActivity()).abrirDetalhes(filme);
        });

        btPesquisar.setOnClickListener(v -> {
            String ano = txAno.getText().toString().trim();
            if (!ano.matches("[0-9]{4}")) {
                txAno.setError("Digite um ano com quatro dígitos");
                return;
            }
            buscarFilmes(ano);
        });

        if (savedInstanceState != null)
            anoPesquisado = savedInstanceState.getString("anoPesquisado");
        if (anoPesquisado != null)
            buscarFilmes(anoPesquisado);

        return view;
    }

    private void buscarFilmes(String ano) {
        if (consulta != null)
            consulta.cancel();

        anoPesquisado = ano;
        adapter.clear();
        tvMensagem.setText("Pesquisando...");
        consulta = new RetrofitConfig().getMovieService().buscarPorAno(ano);
        consulta.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (call != consulta || call.isCanceled())
                    return;

                List<Movie> filmes = response.body();
                if (response.isSuccessful() && filmes != null && !filmes.isEmpty()) {
                    adapter.addAll(filmes);
                    tvMensagem.setText("");
                } else if (response.code() == 400 || response.isSuccessful()) {
                    tvMensagem.setText("Nenhum filme encontrado para esse ano");
                } else {
                    tvMensagem.setText("Erro ao consultar os filmes");
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                if (call == consulta && !call.isCanceled())
                    tvMensagem.setText("Erro ao acessar o servidor");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("anoPesquisado", anoPesquisado);
    }

    @Override
    public void onDestroyView() {
        if (consulta != null)
            consulta.cancel();
        consulta = null;
        super.onDestroyView();
    }
}

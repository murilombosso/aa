package unoeste.fipp.appmodeloprova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ResultadoFragment extends Fragment {
    private TextView tvDetalhes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resultado, container, false);
        tvDetalhes = view.findViewById(R.id.tvDetalhes);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Movie filme = ((MainActivity) requireActivity()).getFilmeSelecionado();

        if (filme == null) {
            tvDetalhes.setText("Selecione um filme na pesquisa.");
            return;
        }

        tvDetalhes.setText("Título: " + texto(filme.title) +
                "\nAno: " + texto(filme.year) +
                "\nSinopse: " + texto(filme.shortSummary) +
                "\nGêneros: " + texto(filme.genres) +
                "\nIMDb: " + texto(filme.imdbId) +
                "\nDuração: " + texto(filme.runtime) +
                "\nTrailer: " + texto(filme.youTubeTrailer) +
                "\nNota: " + texto(filme.rating) +
                "\nPôster: " + texto(filme.moviePoster) +
                "\nDireção: " + texto(filme.director) +
                "\nRoteiristas: " + texto(filme.writers) +
                "\nElenco: " + texto(filme.cast) +
                "\nID do banco: " + texto(filme.id));
    }

    private String texto(String valor) {
        return valor == null || valor.trim().isEmpty() ? "—" : valor;
    }
}

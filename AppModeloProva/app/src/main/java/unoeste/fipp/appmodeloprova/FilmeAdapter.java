package unoeste.fipp.appmodeloprova;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class FilmeAdapter extends ArrayAdapter<Movie> {
    public FilmeAdapter(Context context) {
        super(context, R.layout.item_filme, new ArrayList<>());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_filme, parent, false);

        Movie filme = getItem(position);
        TextView tvTitulo = convertView.findViewById(R.id.tvTitulo);
        TextView tvAnoGenero = convertView.findViewById(R.id.tvAnoGenero);
        tvTitulo.setText(filme.title);
        String anoGenero = filme.year;
        if (filme.genres != null && !filme.genres.isEmpty())
            anoGenero += " | " + filme.genres;
        tvAnoGenero.setText(anoGenero);
        return convertView;
    }
}

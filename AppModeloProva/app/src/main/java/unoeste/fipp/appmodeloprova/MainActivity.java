package unoeste.fipp.appmodeloprova;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.os.BundleCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private Movie filmeSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState != null)
            filmeSelecionado = BundleCompat.getSerializable(savedInstanceState, "filmeSelecionado", Movie.class);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        viewPager.setAdapter(new ViewPagerAdapter(this));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pesquisar)
                viewPager.setCurrentItem(0, false);
            else if (id == R.id.nav_resultado)
                viewPager.setCurrentItem(1, false);
            else if (id == R.id.nav_cadastro)
                viewPager.setCurrentItem(2, false);
            else
                return false;
            return true;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int id = R.id.nav_pesquisar;
                if (position == 1)
                    id = R.id.nav_resultado;
                else if (position == 2)
                    id = R.id.nav_cadastro;

                if (bottomNavigationView.getSelectedItemId() != id)
                    bottomNavigationView.setSelectedItemId(id);
            }
        });
    }

    public void abrirDetalhes(Movie filme) {
        filmeSelecionado = filme;
        viewPager.setCurrentItem(1, false);
    }

    public Movie getFilmeSelecionado() {
        return filmeSelecionado;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("filmeSelecionado", filmeSelecionado);
    }
}

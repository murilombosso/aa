# Prova F4 2026 — passo a passo

Use o `AppModeloProva` do pacote `ProvaF42026.zip`. Ele já tem Retrofit, Gson, `ViewPager2`, `BottomNavigationView` e dois fragments. A adaptação fica no próprio app, com Java, XML e os componentes do modelo.

## 1. Rode o backend e confira o MongoDB

1. Extraia o ZIP. Abra a pasta `MoviesFinder` no IntelliJ como projeto Maven. Selecione o **JDK 25**, que é a versão do `pom.xml`.
2. Confira se o MongoDB está rodando. No MongoDB Compass, conecte em `mongodb://localhost:27017`.
3. Execute `MoviesFinderApplication` no IntelliJ. O Spring sobe na porta `8080`. Na primeira execução, o inicializador tenta carregar os filmes quando o banco ainda não existe.
4. No Compass, abra o banco `movies_collection` e a coleção `movies`. Teste o filtro `{ "year": "2000" }`. O ano é texto no banco. No navegador da máquina que roda o Spring, abra `http://localhost:8080/apis/get-by-year/2000` e confira o JSON.
5. Compare o campo `IMDB_ID` no Compass com `imdbId` no JSON da API. No código fornecido, o inicializador lê o arquivo usando Gson e o mapeamento de `IMDB_ID` é do Spring Data. Por isso os identificadores dos filmes importados automaticamente podem vir vazios. Se acontecer, o caminho pelo Compass é criar um banco de teste, por exemplo `movies_collection_teste`, com uma coleção `movies`, e importar o `MoviesFinder/filmes.json` original em **Add Data → Import JSON or CSV file**. No IntelliJ, pare o Spring, configure `SPRING_MONGODB_DATABASE=movies_collection_teste` nas variáveis da configuração de execução e rode de novo. O inicializador pula a importação quando esse banco já existe. Depois, confira se `GET /apis/get-by-imdb/tt7026230` encontra um filme.

### Rotas disponíveis

| Método e rota | Uso |
| --- | --- |
| `GET /apis/info` | Conferir a conexão do backend com o MongoDB |
| `GET /apis/get-first` | Ver a estrutura de um filme |
| `GET /apis/get-by-year/{year}` | Buscar filmes de um ano |
| `GET /apis/get-by-imdb/{imdb}` | Consultar um filme pelo identificador do IMDb |
| `POST /apis/add-movie` | Cadastrar um filme recebendo JSON |

## 2. Configure o Retrofit no app

1. Abra `AppModeloProva` no Android Studio. As classes do app ficam em `app/src/main/java/unoeste/fipp/appmodeloprova`. O `AndroidManifest.xml` do modelo já tem a permissão `INTERNET` e permite HTTP local.
2. Crie `Movie.java` no mesmo pacote de `Cep.java`. Use campos `String`, como no backend. Os nomes que o Gson deve ler e enviar são `id`, `title`, `year`, `shortSummary`, `genres`, `imdbId`, `runtime`, `youTubeTrailer`, `rating`, `moviePoster`, `director`, `writers` e `cast`. Pode usar campos públicos, como no modelo `Cep`.
3. Crie `MovieService.java` no molde de `EnderecoService.java`.

```java
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

    @GET("get-by-imdb/{imdb}")
    Call<Movie> buscarPorImdb(@Path("imdb") String imdb);

    @POST("add-movie")
    Call<Movie> cadastrar(@Body Movie filme);
}
```

4. Em `RetrofitConfig.java`, troque a URL do ViaCEP por `http://IP_DO_COMPUTADOR:8080/apis/`, incluindo a barra final. Rode `ipconfig` no Windows e substitua `IP_DO_COMPUTADOR` pelo IPv4 da máquina do Spring na conexão usada pelo aparelho. No Android, use esse IP conforme o enunciado. Mantenha `GsonConverterFactory.create()` e crie `getMovieService()` retornando `retrofit.create(MovieService.class)`. Em aparelho físico, confirme que ele alcança o PC pela rede.

## 3. Ligue os três fragments no ViewPager2

1. Reaproveite `PesquisaFragment` e `ResultadoFragment`. Crie `CadastroFragment` e seu XML `fragment_cadastro.xml`.
2. Em `ViewPagerAdapter.java`, mapeie as posições `0 = PesquisaFragment`, `1 = ResultadoFragment`, `2 = CadastroFragment` e retorne `3` em `getItemCount()`.
3. Em `res/menu/bottom_nav_menu.xml`, mantenha `nav_pesquisar` e `nav_resultado`, com os títulos **Pesquisar** e **Detalhes**. Adicione `nav_cadastro` com o título **Cadastro**.
4. Em `MainActivity.java`, atualize os dois listeners que já existem. A opção do menu muda o pager para a posição correspondente. Ao deslizar, `onPageSelected` marca a opção correspondente do menu. Trate as três posições nos dois listeners.
5. Na `MainActivity`, guarde um campo `private Movie filmeSelecionado`. Crie `getFilmeSelecionado()` e `abrirDetalhes(Movie filme)`. Em `abrirDetalhes`, atribua o filme ao campo e chame `viewPager.setCurrentItem(1, false)`.

## 4. Item A — pesquisa por ano com ListView personalizado

1. Em `fragment_pesquisa.xml`, troque o campo de CEP por `txAno`, com teclado numérico. Adicione o botão `btPesquisar`, um `TextView` `tvMensagem` e um `ListView` `lvFilmes`. Num `LinearLayout` vertical, use altura `0dp` e peso `1` no `ListView` para ele ocupar o espaço restante.
2. Crie `res/layout/item_filme.xml` com dois `TextView`, por exemplo `tvTitulo` e `tvAnoGenero`. Em `FilmeAdapter.java`, estenda `ArrayAdapter<Movie>` e sobrescreva `getView()`. Infle `item_filme` quando `convertView` for nulo, pegue o item com `getItem(position)` e preencha os campos com título, ano e gênero. Esse XML próprio é o que personaliza o `ListView`.
3. No `onCreateView` de `PesquisaFragment`, faça os `findViewById`, associe o adapter ao `ListView` e use `setOnClickListener` no botão. Leia o ano com `trim()`, valide quatro dígitos e chame `new RetrofitConfig().getMovieService().buscarPorAno(ano).enqueue(...)`.
4. No `Callback<List<Movie>>`, se `response.isSuccessful()` e `response.body() != null`, limpe e carregue os novos filmes no adapter com `clear()`, `addAll(...)` e `notifyDataSetChanged()`. Se vier HTTP 400, limpe a lista e mostre **Ano inexistente**. Em `onFailure`, mostre **Erro ao acessar o servidor**.
5. Use `lvFilmes.setOnItemClickListener(...)`. Dentro do listener, pegue `adapter.getItem(position)` e chame `((MainActivity) requireActivity()).abrirDetalhes(filme)`.

## 5. Item B — detalhes do filme selecionado

1. Em `fragment_resultado.xml`, coloque um `TextView` dentro de um `ScrollView` para caber a sinopse e o elenco.
2. Em `ResultadoFragment.java`, ligue o `TextView` em `onCreateView`. Em `onResume`, leia `((MainActivity) requireActivity()).getFilmeSelecionado()`. Se ainda não houver seleção, mostre **Selecione um filme na pesquisa**.
3. Monte o texto com título, ano, sinopse, gêneros, IMDb, duração, trailer, nota, pôster, direção, roteiristas e elenco. Inclua o ID do banco se estiver presente. Para campos vazios, mostre `—`. O endpoint de pesquisa já devolve todos esses dados, então você pode preencher a tela com o próprio objeto escolhido. Para conferir a rota por IMDb, use `buscarPorImdb(imdbId)` quando o identificador estiver disponível.

## 6. Item C — cadastro de filme

1. Use um `ScrollView` com um `LinearLayout` vertical em `fragment_cadastro.xml`. Faça um campo para cada dado do filme, incluindo IMDb, e um botão **Cadastrar** no fim. O ID interno do MongoDB é gerado na gravação.
2. Em `CadastroFragment.java`, ligue os campos com `findViewById`. No clique, crie um `Movie`, preencha os campos com os textos digitados e deixe `id` nulo. Valide título, ano de quatro dígitos e IMDb preenchido antes de enviar. No corpo JSON a chave é `imdbId`.
3. Envie com `new RetrofitConfig().getMovieService().cadastrar(filme).enqueue(...)`, com um `Callback<Movie>`. Se a resposta for bem-sucedida, mostre **Filme cadastrado**. Para HTTP 400, com IMDb preenchido, mostre **IMDb já cadastrado**. Em `onFailure`, mostre **Erro ao acessar o servidor**.

## 7. Teste e prepare o ZIP

- Pesquise `2000` e confira se cada linha mostra título, ano e gênero. Toque em dois filmes diferentes e confira se os dados mudam. Pesquise `2099` para testar a mensagem de ano inexistente.
- Cadastre um filme de teste do ano `2000` com um IMDb ainda livre, como `tt99999999`. Pesquise `2000` novamente e confira o novo filme. Tente cadastrar o mesmo IMDb outra vez para conferir a mensagem de duplicidade.
- Compacte a pasta **AppModeloProva** com o projeto Android. Inclua `app`, os arquivos Gradle e o wrapper. Pode deixar fora `.gradle`, `.idea`, pastas `build` e `local.properties`. O envio pedido na folha é do ZIP do projeto pelo **Aprender**. Siga a orientação do professor para a conexão à rede.

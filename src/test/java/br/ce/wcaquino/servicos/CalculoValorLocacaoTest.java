package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import br.ce.waquino.exceptions.FilmeSemEstoqueException;
import br.ce.waquino.exceptions.LocadoraException;
import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	private LocacaoService service = new LocacaoService();
	private LocacaoDAO dao;
	private SPCService spc;
	
	@Parameter
	public List<Filme> filmes;
	@Parameter(value = 1)
	public Double valorLocacao;
	@Parameter(value = 2)
	public String cenario;

	@Before
	public void setup() {
		service = new LocacaoService();
		dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
		spc = Mockito.mock(SPCService.class);
		service.setSPCService(spc);
	}

	private static Filme filme1 = FilmeBuilder.umFilme().agora();
	private static Filme filme2 = FilmeBuilder.umFilme().agora();
	private static Filme filme3 = FilmeBuilder.umFilme().agora();
	private static Filme filme4 = FilmeBuilder.umFilme().agora();
	private static Filme filme5 = FilmeBuilder.umFilme().agora();
	private static Filme filme6 = FilmeBuilder.umFilme().agora();
	private static Filme filme7 = FilmeBuilder.umFilme().agora();
	
	@Parameters(name = "{2}")
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object[][]{
			{Arrays.asList(filme1,filme2), 8.0 , "2 Filmes: Sem Desconto"},
			{Arrays.asList(filme1,filme2,filme3), 11.0 , "3 Filmes: 25%"},
			{Arrays.asList(filme1,filme2,filme3,filme4), 13.0, "4 Filmes: 50%"},
			{Arrays.asList(filme1,filme2,filme3,filme4,filme5), 14.0, "5 Filmes: 75%"},
			{Arrays.asList(filme1,filme2,filme3,filme4,filme5,filme6), 14.0, "6 Filmes: 100%"},
			{Arrays.asList(filme1,filme2,filme3,filme4,filme5,filme6,filme7), 18.0, "7 Filmes: Sem Desconto"}
		});
	}

	@SuppressWarnings("deprecation")
	// Teste desconto no aluguel do quinto filme(75%)
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
		// Cenario
		Usuario usuario = new Usuario("Usuario 1");

		// Acao
		Locacao resultado = service.alugarFilme(usuario, filmes);

		// Verificacao
		// 4+4+3+2+1 = 14
		assertThat(resultado.getValor(), CoreMatchers.is(valorLocacao));
	}
}

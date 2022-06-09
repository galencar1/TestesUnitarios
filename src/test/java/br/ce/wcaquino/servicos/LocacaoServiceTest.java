package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDeDias;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.waquino.exceptions.FilmeSemEstoqueException;
import br.ce.waquino.exceptions.LocadoraException;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;
	
/************************************************************************************************************/
	// Utilizando Rules
	@Rule
	// Error's Collector's
	public ErrorCollector error = new ErrorCollector();
/*************************************************************************************************************/
	@SuppressWarnings("deprecation")
	@Rule
	public ExpectedException exception = ExpectedException.none();
/*************************************************************************************************************/
	@Before
	public void setup() {
		service = new LocacaoService();
	}
/*************************************************************************************************************/
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		// Criando teste unitario
		// Cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));

		// Ação
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// Verificação
		error.checkThat(locacao.getValor(), CoreMatchers.is(5.0));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),
//				CoreMatchers.is(true));
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDeDias(1));
	}
/*************************************************************************************************************/
	// Teste filme sem estoque utilizando a forma elegante
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		// Cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 4.0));

		// Ação
		service.alugarFilme(usuario, filmes);
	}
/*************************************************************************************************************/
	// Teste Usuario Vazio utilizando forma robusta
	@SuppressWarnings("deprecation")
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));

		// ação
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio!"));
		}
	}
/*************************************************************************************************************/
	// Teste Filme vazio utilizando a forma nova
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		// Cenario
		Usuario usuario = new Usuario("Usuario 1");

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio!");

		// acao
		service.alugarFilme(usuario, null);
	}
/**************************************************************************************************************/
	@SuppressWarnings("deprecation")
	// Teste desconto no aluguel do terceiro filme (25%)
	@Test
	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
		//Cenario
			Usuario usuario = new Usuario("Usuario 1");
			List<Filme>filmes = Arrays.asList(
					new Filme("Filme 1", 2, 4.0),
					new Filme("Filme 2 ", 2, 4.0),
					new Filme("Filme 3", 2, 4.0));
		//Acao
			Locacao resultado =  service.alugarFilme(usuario, filmes);
		
		//Verificacao
		//4+4+3 = 11
			assertThat(resultado.getValor(), CoreMatchers.is(11.0));
			
		
	}
/**************************************************************************************************************/
	@SuppressWarnings("deprecation")
	// Teste desconto no aluguel do quarto filme(50%)
	@Test
	public void devePagar50PctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
		//Cenario
			Usuario usuario = new Usuario("Usuario 1");
			List<Filme>filmes = Arrays.asList(
					new Filme("Filme 1", 2, 4.0),
					new Filme("Filme 2 ", 2, 4.0),
					new Filme("Filme 3", 2, 4.0),
					new Filme("Filme 4", 2, 4.0));
		//Acao
			Locacao resultado =  service.alugarFilme(usuario, filmes);
		
		//Verificacao
		//4+4+3+2 = 13
			assertThat(resultado.getValor(), CoreMatchers.is(13.0));	
	}
/**************************************************************************************************************/
	@SuppressWarnings("deprecation")
	// Teste desconto no aluguel do quinto filme(75%)
	@Test
	public void devePagar25PctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
		//Cenario
			Usuario usuario = new Usuario("Usuario 1");
			List<Filme>filmes = Arrays.asList(
					new Filme("Filme 1", 2, 4.0),
					new Filme("Filme 2 ", 2, 4.0),
					new Filme("Filme 3", 2, 4.0),
					new Filme("Filme 4", 2, 4.0),
					new Filme("Filme 5", 2, 4.0));
		//Acao
			Locacao resultado =  service.alugarFilme(usuario, filmes);
		
		//Verificacao
		//4+4+3+2+1 = 14
			assertThat(resultado.getValor(), CoreMatchers.is(14.0));	
	}
/**************************************************************************************************************/
	@SuppressWarnings("deprecation")
	// Teste desconto no aluguel do sexto filme(100%)
	@Test
	public void devePagar0PctNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
		//Cenario
			Usuario usuario = new Usuario("Usuario 1");
			List<Filme>filmes = Arrays.asList(
					new Filme("Filme 1", 2, 4.0),
					new Filme("Filme 2 ", 2, 4.0),
					new Filme("Filme 3", 2, 4.0),
					new Filme("Filme 4", 2, 4.0),
					new Filme("Filme 5", 2, 4.0),
					new Filme("Filme 6", 2, 4.0));
		//Acao
			Locacao resultado =  service.alugarFilme(usuario, filmes);
		
		//Verificacao
		//4+4+3+2+1+0 = 14
			assertThat(resultado.getValor(), CoreMatchers.is(14.0));	
	}
/***************************************************************************************************************/
	@SuppressWarnings("deprecation")
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = new Usuario("usuario 1");
		List<Filme> filmes = Arrays.asList(
				new Filme("Filme 1", 2, 4.0));
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes); 
		//verificacao
//		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
//		Assert.assertTrue(ehSegunda);
//		assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		assertThat(retorno.getDataRetorno(), caiEm(Calendar.SUNDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
 	}
}

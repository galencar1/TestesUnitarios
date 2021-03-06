package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDeDias;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;

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
import org.mockito.Mockito;

import br.ce.waquino.exceptions.FilmeSemEstoqueException;
import br.ce.waquino.exceptions.LocadoraException;
import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;
	private LocacaoDAO dao;
	private SPCService spc;
	private EmailService email;
	
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
		
		dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao); 
		
		spc = Mockito.mock(SPCService.class);
		service.setSPCService(spc);
		
		email = Mockito.mock(EmailService.class);
		service.setEmailService(email);
	}
/*************************************************************************************************************/
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		// Criando teste unitario
		// Cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().comValor().agora());

		// A??o
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// Verifica??o
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
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

		// A??o
		service.alugarFilme(usuario, filmes);
	}
/*************************************************************************************************************/
	// Teste Usuario Vazio utilizando forma robusta
	@SuppressWarnings("deprecation")
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		// a??o
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
		Usuario usuario = UsuarioBuilder.umUsuario().agora();

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio!");

		// acao
		service.alugarFilme(usuario, null);
	}
/**************************************************************************************************************/
	
/**************************************************************************************************************/
	@SuppressWarnings("deprecation")
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes); 
		//verificacao
//		boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
//		Assert.assertTrue(ehSegunda);
//		assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		assertThat(retorno.getDataRetorno(), caiEm(Calendar.SUNDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
 	}
	
	@Test
	
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeSemEstoqueException {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true); 
		
		//acao
		try {
			service.alugarFilme(usuario, filmes);
			
		//verificacao
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario Negativado!"));
		}

		Mockito.verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		//Usuario usuario2 = UsuarioBuilder.umUsuario().agora();
		//Usuario usuario3 = UsuarioBuilder.umUsuario().agora();
		
		List<Locacao> locacoes = Arrays.asList(LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario).agora());
				//LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora());
				//LocacaoBuilder.umLocacao().comUsuario(usuario2).agora());
		
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes); 
		//acao 
		service.notificarAtrasos();
		//verificacao
		Mockito.verify(email).notificarAtraso(usuario);
		//Mockito.verify(email).notificarAtraso(usuario3);
		//Mockito.verify(email).notificarAtraso(usuario2);
		//Mockito.verifyNoMoreInteractions(email);
	}
}

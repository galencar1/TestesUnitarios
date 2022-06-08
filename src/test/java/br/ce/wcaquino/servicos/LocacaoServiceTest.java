package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertThat;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
	
	// Utilizando Rules
	@Rule
	// Error's Collector's
	public ErrorCollector error = new ErrorCollector();

	@SuppressWarnings("deprecation")
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		System.out.println("Before");
		service = new LocacaoService();
	}
	
	@After
	public void tearDown() {
		System.out.println("After");
	}

	@BeforeClass
	public static void setupClass() {
		System.out.println("Before Class");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println("After Class");
	}

	@Test
	public void testeLocacao() throws Exception {

		// Criando teste unitario

		// Cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		
		System.out.println("Teste!");
		
		// Ação
		Locacao locacao = service.alugarFilme(usuario, filme);

		// Verificação
		error.checkThat(locacao.getValor(), CoreMatchers.is(5.0));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),
				CoreMatchers.is(true));
	}

	// Teste filme sem estoque utilizando a forma elegante
	@Test(expected = FilmeSemEstoqueException.class)
	public void testeLocacao_FilmeSemEstoque() throws Exception {
		// Cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		// Ação
		service.alugarFilme(usuario, filme);
	}

	// Teste Usuario Vazio utilizando forma robusta
	@SuppressWarnings("deprecation")
	@Test
	public void testLocacao_usuarioVazio() throws FilmeSemEstoqueException {
		// cenario
		Filme filme = new Filme("Filme 2", 1, 5.0);

		// ação
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), CoreMatchers.is("Usuario vazio!"));
		}
	}

	// Teste Filme vazio utilizando a forma nova
	@Test
	public void testLocacao_FilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		// Cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio!");
		
		//acao
		service.alugarFilme(usuario, null);
	}
}

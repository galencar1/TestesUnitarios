package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertThat;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;



public class LocacaoServiceTest {
	
	//Utilizando Rules
	@Rule
	//Error's Collector's
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testeLocacao() throws Exception {
		
				
		//Criando teste unitario

		//Cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);

		//Ação
		Locacao locacao = service.alugarFilme(usuario, filme);

		//Verificação
		error.checkThat(locacao.getValor(), CoreMatchers.is(5.0));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),
				CoreMatchers.is(true  ));
	}
	
	//Teste forma elegante
	@Test(expected = Exception.class)
	public void testeLocacao_FilmeSemEstoque() throws Exception{
		//Cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		//Ação
		service.alugarFilme(usuario, filme);
	}
	
	//Teste forma Robusta
	@Test
	public void testeLocacao_FilmeSemEstoque2() {
		//Cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		//Ação
		try {
			service.alugarFilme(usuario, filme);
			Assert.fail("Deveria ser uma exceção");
		} catch (Exception e) {
			// TODO: handle exception
			assertThat(e.getMessage(), CoreMatchers.is("Filme sem estoque"));
		}
		
	}
	
	@Test
	public void testeLocacao_FilmeSemEstoque3() throws Exception{
		//Cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		exception.expect(Exception.class);
		exception.expectMessage("Filme sem estoque");
		
		//Ação
		service.alugarFilme(usuario, filme);
	}
}

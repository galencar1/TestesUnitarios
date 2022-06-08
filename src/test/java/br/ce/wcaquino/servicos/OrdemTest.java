package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

//Anota��o JUnit que indica a ordem que os testes ser�o executados
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class OrdemTest {
	
	public static int contador = 0;
	
	@Test
	public void inicio() {
		contador = 1;
	}
	
	@Test
	public void verifica() {
		Assert.assertEquals(1, contador);
	}
}

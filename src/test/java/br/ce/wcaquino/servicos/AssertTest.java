package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {
	@Test
	public void test() {
		/* Assert's true and false*/
		Assert.assertTrue(true); //Verifica se um valor é verdadeiro
		//Assert.assertTrue(false); //Inserindo 'false', o teste não passa. Nesse caso devemos negar a expressão.
		//Assert.assertTrue(!false); // Dessa maneira o teste funciona corretamente.Porém o correto é utilizar
		// o assertFalse.
		Assert.assertFalse(false);
		
		/*Assert Equals*/
		Assert.assertEquals("Erro de comparação",1, 1); // Compara um valor ao outro.
		Assert.assertEquals(0.51, 0.51, 0.01); // Comparando valores double, devemos colocar um delta de separação
		//utilizamos esse delta como margem de erro, dizimas periódicas que são infinitos precisam dessa margem de erro.
		// Exemplo PI.
		Assert.assertEquals(Math.PI, 3.14, 0.01);
		//Assert.assertEquals(Math.PI, 3.14, 0.001); //Adicionando um "0" a mais no delta o teste falha;
		
		int i = 5;
		Integer i2 = 5;
		//Assert.assertEquals(i, i2);// Nesse caso o java não compara por se tratar de uma variável tipo primitivo
		//e uma classe Wrapper (Objeto)
		
		//Podemos efetuar isso de duas maneiras.
		Assert.assertEquals(Integer.valueOf(i), i2); //Passando o tipo primitivo para objeto
		Assert.assertEquals(i, i2.intValue()); // Passando o objeto para tipo primitivo
		
		/*Assert Equals para string*/
		Assert.assertEquals("bola", "bola");
		//Assert.assertEquals("bola", "Bola"); //Nesse caso o teste falha devido a letra maiuscula.
		// Corrigimos utilizando o assert true
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("bo"));
		
		Usuario u1 = new Usuario("Usuario1");
		Usuario u2 = new Usuario("Usuario1");
		Usuario u3 = null;
		Usuario u4 = u2;
		Assert.assertEquals(u1, u2);// Assert não considera os objetos iguais, pois, quem define isso é o próprio objeto
		// PAra funcionar é necessario implementar o objeto Equals no próprio objeto usuario
		
		//Comparando se os objetos são da mesma instancia
		Assert.assertSame(u2, u4);
		//Verificando se um objeto é nulo.
		Assert.assertTrue(u3 ==null); // ou
		Assert.assertNull(u3);
		
	}
}

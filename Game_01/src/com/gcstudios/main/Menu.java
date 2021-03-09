package com.gcstudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.gcstudios.world.World;

public class Menu {

	public String[] options = {"novo jogo","carregar jogo","sair"};
	
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	
	public boolean up,down,enter;
	
	public boolean pause;
	
	public static boolean saveExists = false;
	public static boolean saveGame = false;
	
	
	public void tick() {
		File file = new File("save.txt");
		if (file.exists()) {
			saveExists = true;
			
		} else {
			saveExists = false;
		}
		
		if(up) {
			up = false;
			currentOption--;
			if(currentOption < 0)
				currentOption = maxOption;
		}
		if(down) {
			down = false;
			currentOption++;
			if(currentOption > maxOption)
				currentOption = 0;
		}
		if(enter) {
			enter = false;
			if(options[currentOption] == "novo jogo" || options[currentOption] == "continuar") {
				Game.gameState = "NORMAL";
				pause = false;
			}
			else if (options[currentOption] == "carregar jogo") {
				System.out.println("Carregando...");
				file = new File("save.txt");
				System.out.println("Criando arquivo...");
				if (file.exists()) {
					System.out.println("Aqruivo existe...");
					String saver = loadGame(10);
					System.out.println("Descriptografando...");
					aplicarSave(saver);
				}
				System.out.println("Carregou...");
			}
			else if(options[currentOption] == "sair") {
				System.exit(1);
			}
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		//g2.setColor(new Color(0,0,0,100));
		//g2.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial",Font.BOLD,36));
		
		g.drawString(">Danki.Code<", (Game.WIDTH*Game.SCALE) / 2 - 110, (Game.HEIGHT*Game.SCALE) / 2 - 160);
		
		//Opcoes de menu
		g.setColor(Color.white);
		g.setFont(new Font("arial",Font.BOLD,24));
		if(pause == false)
			g.drawString("Novo jogo", (Game.WIDTH*Game.SCALE) / 2 - 50, 160);
		else
			g.drawString("Resumir", (Game.WIDTH*Game.SCALE) / 2 - 40, 160);
		g.drawString("Carregar jogo", (Game.WIDTH*Game.SCALE) / 2 - 70, 200);
		g.drawString("Sair", (Game.WIDTH*Game.SCALE) / 2 - 10, 240);
		
		if(options[currentOption] == "novo jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE) / 2 - 90, 160);
		}else if(options[currentOption] == "carregar jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE) / 2 - 90, 200);
		}else if(options[currentOption] == "sair") {
			g.drawString(">", (Game.WIDTH*Game.SCALE) / 2 - 40, 240);
		}
	}
	
	////////// Método para carregar o game ///////////
	public String loadGame(int descriptografar) {
		String line = ""; // O valor inicial dessa variavel local é nulo
		File arquivo = new File("save.txt");
		if (arquivo.exists()) { // Se esse aquivo que passamos no parametro de File existir:
			/// Iremos ler o nosso arquivo
			try {
				String lerLinha = "";
				BufferedReader ler = new BufferedReader(new FileReader("save.txt"));
				/// Agora iremos ler de fato
				try {
					while ((lerLinha = ler.readLine()) != null) { // Enquanto tiver algo pra ler:
						String[] arrayString = lerLinha.split(":");
						char[] caracteres = arrayString[1].toCharArray();
						/* Exemplo de arrayString -> level: 2
						arrayString[0] é level
						arrayString[1] é 2
						*/
						arrayString[1] = "";
						/// Decodificando ///        
						for (int i = 0; i < caracteres.length; i++) {
							caracteres[i] -= descriptografar; // Voltando a ler tudo normal
							arrayString[1] += caracteres[i];
						}
						
						// Adicionando coisas na variavel que será o retorno do método //
						line += arrayString[0];
						line += ":";
						line += arrayString[1];
						line += "/"; // Para dar um split e "quebrar linha"
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {}
		}
		
		return line; // Se nao existir esse arquivo file, retorne essa linha vazia
	}
	
	////////// Método para salvar o game //////////
	public void save(String[] nome, int[] valor, int criptografia) {
		///// Criar um arquivo de save /////
		BufferedWriter arquivo = null;
		FileWriter escreve;
		try {
			escreve = new FileWriter("save.txt");
			arquivo = new BufferedWriter(escreve);
		} catch(IOException e) { 
			e.printStackTrace();
		}
		
		///// Criptografia /////
		for (int i = 0; i < nome.length; i++) { // Percorrer todas as strings pra construir baseado nisso o arquivo do save
			String atual = nome[i]; // Pegamos o nome atual 
			atual += ":";
			String valorEmString = Integer.toString(valor[i]); // Converter o valor numerico (inteiro) em um numero de texto
			char[] caracteres = valorEmString.toCharArray(); // Pegamos o valor atual do valor numerico ja convertido em string, mas agora pegando cada caractere
			// Loop da criptografia //
			for (int a = 0; a < caracteres.length; a++) {
				caracteres[a] += criptografia; // Isso vai alterar as palavras de nosso arquivo save, deixando-o crpitografado
				atual += caracteres[a];
			}
			// Agora vamos escrever no arquivo //
			try {
				arquivo.write(atual);
				if (i < nome.length - 1) // Se passar disso, terá um erro no array
					arquivo.newLine(); // Teremos uma nova linha 
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Segurança para que depois de escrever no aquivo o sistema feche //
			try {
				arquivo.flush();
				arquivo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	///////// Método para aplicar o save e o loadGame //////////
	public void aplicarSave(String str) {
		String[] arrayStr = str.split("/");
		System.out.println("Str: " + str);
		for (int i = 0; i < arrayStr.length; i++) {
			System.out.println("arrayStr: " + arrayStr[i]);
			String[] arrayStr2 = arrayStr[i].split(":");
			switch (arrayStr2[0]) {
				case "level":
					World.restartGame("level" + arrayStr2[1] + ".png");
					Game.gameState = "NORMAL";
					pause = false;
					break;
					
			}
		}
	}
	
}








package com.marcarini.sudoku.resolver;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class SudokuResolver {
	
	private HashMap<String, ArrayList<Integer>> matrizOpcoes = new HashMap<String, ArrayList<Integer>>();
	private HashMap<String, ArrayList<Integer>> qntOpcoesPorLinhaQuadrante = new HashMap<String, ArrayList<Integer>>();
	private HashMap<String, ArrayList<Integer>> qntOpcoesPorColunaQuadrante = new HashMap<String, ArrayList<Integer>>();
	
	public static void main(String[] args) {

		int[][] inputSudoku = {{0,0,5,0,7,0,0,0,8}, {0,0,1,0,0,0,3,4,0},{8,6,0,0,0,9,0,2,0}, {0,0,0,6,0,3,0,7,0},
				{2,0,0,0,0,1,4,0,0}, {0,0,7,8,9,0,0,3,0},{0,9,0,0,8,0,0,0,0}, {0,8,2,1,0,5,0,0,0},{4,0,0,0,0,0,0,0,0} };

		SudokuResolver sudoku = new SudokuResolver();
		sudoku.resolverMatrizSudoku(inputSudoku);
	}

	
	public void resolverMatrizSudoku(int[][] matriz) {
		int quantidadeResolver = 0;
		int qntResolverAnterior = 0;
		ArrayList<Integer> opcoesIniciais = null;
		imprimirSudoku(matriz);
		quantidadeResolver = getQuantidadeResolver(matriz);
		
		boolean removeuOportunidades = false;
		while ((quantidadeResolver > 0 && quantidadeResolver != qntResolverAnterior) || removeuOportunidades) {
			
			qntResolverAnterior = quantidadeResolver;
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (matriz[i][j] == 0) {
						opcoesIniciais = matrizOpcoes.get(this.getChaveOpcoes(i, j));
						if (opcoesIniciais == null || opcoesIniciais.isEmpty()) {
							opcoesIniciais = getOpcoesIniciais();
							matrizOpcoes.put(this.getChaveOpcoes(i, j), opcoesIniciais);
						}
						

						if (this.aplicarRegraLinha(opcoesIniciais, i, j, matriz) == 1) {
							this.setValorMatriz(i, j, matriz, opcoesIniciais.get(0).intValue());
						}

						if (this.aplicarRegraColuna(opcoesIniciais, i, j, matriz) == 1) {
							this.setValorMatriz(i, j, matriz, opcoesIniciais.get(0).intValue());
						}
						
						if (this.aplicarRegraQuadrante(opcoesIniciais, i, j, matriz) == 1) {
							this.setValorMatriz(i, j, matriz, opcoesIniciais.get(0).intValue());
						}
					}
				}
			}
			
			quantidadeResolver = getQuantidadeResolver(matriz);
			
			boolean encontrouValor = false;
			if (quantidadeResolver == qntResolverAnterior) {
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						opcoesIniciais = matrizOpcoes.get(this.getChaveOpcoes(i, j));
						if (opcoesIniciais != null && !opcoesIniciais.isEmpty()) {
							encontrouValor = this.aplicarRegraPossibilidadesQuadrante(opcoesIniciais, i, j, matriz);
							if (encontrouValor) {
								break;
							}
							
							encontrouValor = this.aplicarRegraPossibilidadesLinha(opcoesIniciais, i, j, matriz);
							if (encontrouValor) {
								break;
							}
							
							encontrouValor = this.aplicarRegraPossibilidadesColuna(opcoesIniciais, i, j, matriz);
							if (encontrouValor) {
								break;
							}
						}
					}
					if (encontrouValor) {
						break;
					}
				}
			}
			
			removeuOportunidades = this.aplicarRegraPossibilidadesLinhaColunaQuadrante(matriz);

			quantidadeResolver = getQuantidadeResolver(matriz);
			imprimirOportunidades(matriz);
			imprimirSudoku(matriz);
		}

	}
	
	private void setValorMatriz(int linha, int coluna, int[][] matriz, int valor) {
		for (int i = 0; i < 9; i++) {
			if (matriz[linha][i] == valor) {
				this.imprimirEstadoErro(linha, coluna, matriz, valor);
				throw new RuntimeException("Regra do sudoku quebrada. Valor já existente na linha.");
			}
			if (matriz[i][coluna] == valor) {
				this.imprimirEstadoErro(linha, coluna, matriz, valor);
				throw new RuntimeException("Regra do sudoku quebrada. Valor já existente na coluna.");
			}
		}
		
		int qLinha = getLinhaOuColunaInicialQuadrande(linha);
		int qColuna = getLinhaOuColunaInicialQuadrande(coluna);

		for (int i = qLinha; i < (qLinha + 3); i++) {
			for (int j = qColuna; j < (qColuna + 3); j++) {
				if (matriz[i][j] == valor) {
					this.imprimirEstadoErro(linha, coluna, matriz, valor);
					throw new RuntimeException("Regra do sudoku quebrada. Valor já existente no quadrante.");
				}
			}
		}
		
		ArrayList<Integer> mo = matrizOpcoes.get(this.getChaveOpcoes(linha, coluna));
		if (mo != null) {
			mo.clear();
		}
		matriz[linha][coluna] = valor;
	}
	
	private void imprimirEstadoErro(int linha, int coluna, int[][] matriz, int valor) {
		System.out.println("-------------------------------------");
		System.out.println("Erro:");
		this.imprimirSudoku(matriz);
		System.out.println("Linha: " + linha + "; Coluna: " + coluna + ", Valor: " + valor);
		System.out.println("-------------------------------------");
	}

	private String getChaveOpcoes(int linha, int coluna) {
		return linha + "X" + coluna;
	}
	
	public void imprimirSudoku(int[][] matriz) {

		for (int i = 0; i < 9; i++) {
			System.out.print("||");
			for (int j = 0; j < 9; j++) {
				if (j == 2 || j == 5) {
					System.out.print("  " + matriz[i][j] + " |");
				} else {
					System.out.print("  " + matriz[i][j] + " ");
				}
			}
			System.out.print("||");
			System.out.print("\n");
			if (i == 2 || i == 5) {
				System.out.print("------------------------------------------");
				System.out.print("\n");
			}
		}
		System.out.print("\n");
		System.out.println("Tantos a resolver: " + getQuantidadeResolver(matriz));
	}
	
	
	public void imprimirOportunidades(int[][] matriz) {

		System.out.println("----------- Oportunidades - Inicio -------------");
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.println(" ");
				System.out.print("Posição (" + i + "," + j + "): ");
				ArrayList<Integer> opcaoesCelula = matrizOpcoes.get(this.getChaveOpcoes(i, j));
				if (opcaoesCelula != null) {
					for (int x = 0 ; x < opcaoesCelula.size(); x++) {
						if (x > 0) {
							System.out.print(", ");
						}
						System.out.print(opcaoesCelula.get(x).intValue());
					}
				}
			}
			
		}
		System.out.println("\n----------- Oportunidades - Fim -------------");
	}


	public int getQuantidadeResolver(int[][] matriz) {
		int somaZeros = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (matriz[i][j] == 0) {
					somaZeros += 1;
				}
			}
		}
		return somaZeros;
	}

	public int aplicarRegraLinha(ArrayList<Integer> opcoes, int linha, int coluna, int[][] matriz) {
		for (int i = 0; i < 9; i++) {
			if (matriz[linha][i] != 0) {
				opcoes.remove(new Integer(matriz[linha][i]));
			}

		}

		return opcoes.size();
	}

	public int aplicarRegraColuna(ArrayList<Integer> opcoes, int linha, int coluna, int[][] matriz) {
		for (int i = 0; i < 9; i++) {
			if (matriz[i][coluna] != 0) {
				opcoes.remove(new Integer(matriz[i][coluna]));
			}

		}

		return opcoes.size();
	}

	public int aplicarRegraQuadrante(ArrayList<Integer> opcoes, int linha, int coluna, int[][] matriz) {
		int qLinha = getLinhaOuColunaInicialQuadrande(linha);
		int qColuna = getLinhaOuColunaInicialQuadrande(coluna);

		for (int i = qLinha; i < (qLinha + 3); i++) {
			for (int j = qColuna; j < (qColuna + 3); j++) {
				if (matriz[i][j] != 0) {
					opcoes.remove(new Integer(matriz[i][j]));
				}
			}
		}
		return opcoes.size();
	}
	
	
	public boolean aplicarRegraPossibilidadesQuadrante(ArrayList<Integer> opcoes, int linha, int coluna, int[][] matriz) {
		boolean encontrouValor = false;
		ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(linha, coluna));
		if (opcoesCelulaAtual != null) {
			for (int x = 0; x < opcoesCelulaAtual.size(); x++) {
				Integer op = opcoesCelulaAtual.get(x);
				encontrouValor = !verificaPossibilidadeOcorrenciaNoQuadrante(op, linha, coluna);
				if (encontrouValor) {
					this.imprimirEstadoErro(linha, coluna, matriz, op.intValue());
					this.setValorMatriz(linha, coluna, matriz, op.intValue());
					break;
				}
			}
		}
		
		return encontrouValor;
	}
	
	
	private boolean verificaPossibilidadeOcorrenciaNoQuadrante(Integer op, int linha, int coluna) {
		int qLinha = getLinhaOuColunaInicialQuadrande(linha);
		int qColuna = getLinhaOuColunaInicialQuadrande(coluna);

		for (int i = qLinha; i < (qLinha + 3); i++) {
			for (int j = qColuna; j < (qColuna + 3); j++) {
				if (i != linha || j != coluna) {
					ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(i, j));
					if (opcoesCelulaAtual != null && opcoesCelulaAtual.contains(op)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	
	public boolean aplicarRegraPossibilidadesLinhaColunaQuadrante(int[][] matriz) {
		boolean removeuOportunidade = false;
		//Para cada quadrante irá verificar todas as oportunidades.
		for (int i = 0; i < 9; i = i + 3) {
			for (int j = 0; j < 9; j = j + 3) {
				for (int x = 1; x < 10; x++) {
					if (verificaPossibilidadeOcorrenciaPorLinhaColunaNoQuadrante(x, i, j, matriz)) {
						removeuOportunidade = true;
					}
				}
			}
		}
		
		return removeuOportunidade;
		
	}
	
	private boolean verificaPossibilidadeOcorrenciaPorLinhaColunaNoQuadrante(Integer op, int linha, int coluna, int[][] matriz) {
		int qLinha = getLinhaOuColunaInicialQuadrande(linha);
		int qColuna = getLinhaOuColunaInicialQuadrande(coluna);

		ArrayList<Integer> opcaoLinha = qntOpcoesPorLinhaQuadrante.get(this.getChaveOportunidadePorLinhaQuadrante(op.intValue(), linha, coluna));
		ArrayList<Integer> opcaoColuna = qntOpcoesPorColunaQuadrante.get(this.getChaveOportunidadePorColunaQuadrante(op.intValue(), linha, coluna));
		
		if (opcaoLinha == null) {
			opcaoLinha = new ArrayList<Integer>();
			qntOpcoesPorLinhaQuadrante.put(this.getChaveOportunidadePorLinhaQuadrante(op.intValue(), linha, coluna), opcaoLinha);
		}
		
		if (opcaoColuna == null) {
			opcaoColuna = new ArrayList<Integer>();
			qntOpcoesPorColunaQuadrante.put(this.getChaveOportunidadePorColunaQuadrante(op.intValue(), linha, coluna), opcaoLinha);
		}
		//Analisar se existem ocorrencia de oportunidade somente em uma linha ou só em uma coluna do quadrante.
		for (int i = qLinha; i < (qLinha + 3); i++) {
			for (int j = qColuna; j < (qColuna + 3); j++) {
				ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(i, j));
				if (opcoesCelulaAtual != null && opcoesCelulaAtual.contains(op)) {

					if (!opcaoLinha.contains(new Integer(i))) {
						opcaoLinha.add(new Integer(i));
					}
								
					if (!opcaoLinha.contains(new Integer(j))) {
						opcaoLinha.add(new Integer(j));
					}
				}
			}
		}
		
		boolean removeuOportunidade = false;
		//Essa opção só pode estar nessa linha do quadrante, então não pode ser opção nessa mesma linha dos demais quadrantes.
		if (opcaoLinha.size() == 1) {
			int i = opcaoLinha.get(0).intValue();
			for (int j = 0; j < 9; j++) {
				if (j < qColuna && j >= (qColuna + 3)) {
					ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(i, j));
					opcoesCelulaAtual.remove(op);
					removeuOportunidade = true;
				}
			}
		}
		
		//Essa opção só pode estar nessa coluna do quadrante, então não pode ser opção nessa mesma linha dos demais quadrantes.
		if (opcaoColuna.size() == 1) {
			int j = opcaoColuna.get(0).intValue();
			for (int i = 0; i < 9; i++) {
				if (i < qLinha && i >= (qLinha + 3)) {
					ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(i, j));
					opcoesCelulaAtual.remove(op);
					removeuOportunidade = true;
				}
			}
		}
		
		if (removeuOportunidade) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(i, j));
					if (opcoesCelulaAtual.size() == 1) {
						setValorMatriz(i, j, matriz, opcoesCelulaAtual.get(0).intValue());
					}
				}
			}
		}
		
		return removeuOportunidade;
	}
	
	private String getChaveOportunidadePorLinhaQuadrante(int oportunidade, int linha, int coluna) {
		int qLinha = getLinhaOuColunaInicialQuadrande(linha);
		int qColuna = getLinhaOuColunaInicialQuadrande(coluna);
		
		return oportunidade + "_linha_q" + qLinha + "x" + qColuna;
	}
	
	private String getChaveOportunidadePorColunaQuadrante(int oportunidade, int linha, int coluna) {
		int qLinha = getLinhaOuColunaInicialQuadrande(linha);
		int qColuna = getLinhaOuColunaInicialQuadrande(coluna);
		
		return oportunidade + "_coluna_q" + qLinha + "x" + qColuna;
	}
	
	public boolean aplicarRegraPossibilidadesLinha(ArrayList<Integer> opcoes, int linha, int coluna, int[][] matriz) {
		boolean encontrouValor = false;
		ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(linha, coluna));
		if (opcoesCelulaAtual != null) {
			for (int x = 0; x < opcoesCelulaAtual.size(); x++) {
				Integer op = opcoesCelulaAtual.get(x);
				encontrouValor = !verificaPossibilidadeOcorrenciaNaLinha(op, linha, coluna);
				if (encontrouValor) {
					this.imprimirEstadoErro(linha, coluna, matriz, op.intValue());
					this.setValorMatriz(linha, coluna, matriz, op.intValue());
					break;
				}
			}
		}
		
		return encontrouValor;
	}
	
	private boolean verificaPossibilidadeOcorrenciaNaLinha(Integer op, int linha, int coluna) {

		for (int j = 0; j < 9; j++) {
			if (j != coluna) {
				ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(linha, j));
				if (opcoesCelulaAtual != null && opcoesCelulaAtual.contains(op)) {
					return true;
				}
			}
		}
		
		
		return false;
	}
	
	
	public boolean aplicarRegraPossibilidadesColuna(ArrayList<Integer> opcoes, int linha, int coluna, int[][] matriz) {
		boolean encontrouValor = false;
		ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(linha, coluna));
		if (opcoesCelulaAtual != null) {
			for (int x = 0; x < opcoesCelulaAtual.size(); x++) {
				Integer op = opcoesCelulaAtual.get(x);
				encontrouValor = !verificaPossibilidadeOcorrenciaNaColuna(op, linha, coluna);
				if (encontrouValor) {
					this.imprimirEstadoErro(linha, coluna, matriz, op.intValue());
					this.setValorMatriz(linha, coluna, matriz, op.intValue());
					break;
				}
			}
		}
		
		return encontrouValor;
	}
	
	private boolean verificaPossibilidadeOcorrenciaNaColuna(Integer op, int linha, int coluna) {

		for (int i = 0; i < 9; i++) {
			if (i != coluna) {
				ArrayList<Integer> opcoesCelulaAtual = matrizOpcoes.get(this.getChaveOpcoes(i, coluna));
				if (opcoesCelulaAtual != null && opcoesCelulaAtual.contains(op)) {
					return true;
				}
			}
		}
		
		
		return false;
	}
	
	private int getLinhaOuColunaInicialQuadrande(int linha) {
		int qLinha = 0;
		
		if (((linha + 1f) / 3f) <= 1f) {
			qLinha = 0;
		} else if (((linha + 1f) / 3f) <= 2f) {
			qLinha = 3;
		} else {
			qLinha = 6;
		}
		
		return qLinha; 
	}

	private ArrayList<Integer> getOpcoesIniciais() {
		ArrayList<Integer> opcoesIniciais = new ArrayList<Integer>();
		for (int i = 1; i < 10; i++) {
			opcoesIniciais.add(new Integer(i));
		}
		return opcoesIniciais;
	}

}

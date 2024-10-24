/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.stockreserve.dal;

import java.time.LocalDate;

/**
 *
 * @author leog4
 */
public class Produto {
    private String id;
	private String nome;
	private double preco;
	private int quantidade;
	private LocalDate vencimento;

	public Produto(String id, String nome, double preco, int quantidade, LocalDate vencimento) {
		this.id = id;
		this.nome = nome;
		this.preco = preco;
		this.quantidade = quantidade;
		this.vencimento = vencimento;
	}

	public String getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public double getPreco() {
		return preco;
	}

	public int getQuantidade() {
		return quantidade;
	}
	public int setQuantidade(int quantidade) {
		return this.quantidade = quantidade;
	}
	
	public LocalDate getVencimento() {
		return vencimento;
	}

	public void setVencimento(LocalDate vencimento) {
		this.vencimento = vencimento;
	}
}

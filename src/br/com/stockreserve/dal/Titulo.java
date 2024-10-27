/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.stockreserve.dal;

import java.util.List;

/**
 *
 * @author leog4
 */
public class Titulo {
    private String id;
	private double preco;
	private boolean pago;
	//A venda agora vai ter uma lista de produtos em vez de ser um produto e uma quantidade de produtos.
	public List<Produto> produtosCarrinho;
	

	public Titulo(String id, double preco, boolean paga, List<Produto> produtosCarrinho) {
		this.id = id;
		this.preco = preco;
		this.pago = pago;
		this.produtosCarrinho = produtosCarrinho;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public double getPreco() {
		return preco;
	}
	
	public void setPreco(double preco) {
		this.preco = preco;
	}

	public boolean isPago() {
		return pago;
	}

	public void setPago(boolean pago) {
		this.pago = pago;
	}
	public List<Produto> getProdutosCarrinho() {
        return produtosCarrinho;
    }
	public void setProdutosCarrinho(List<Produto> produtosCarrinho) {
		this.produtosCarrinho = produtosCarrinho;
	}
}

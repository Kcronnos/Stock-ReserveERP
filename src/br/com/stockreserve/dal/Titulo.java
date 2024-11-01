/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.stockreserve.dal;

import java.util.List;

/**
 * Representa um título de venda, que contém informações sobre o preço, estado
 * de pagamento e uma lista de produtos associados a essa venda.
 *
 * A classe armazena o identificador do título, o preço total e uma lista de
 * produtos que estão incluídos na venda.
 *
 * @author leog4
 * @version 2.0
 */
public class Titulo {

    private String id;
    private double preco;
    private boolean pago;
    public List<Produto> produtosCarrinho;

    /**
     * Construtor para criar um objeto Titulo.
     *
     * @param id Identificador único do título.
     * @param preco Preço total do título.
     * @param pago Indica se o título foi pago.
     * @param produtosCarrinho Lista de produtos associados a este título.
     */
    public Titulo(String id, double preco, boolean pago, List<Produto> produtosCarrinho) {
        this.id = id;
        this.preco = preco;
        this.pago = pago;
        this.produtosCarrinho = produtosCarrinho;
    }

    /**
     * Retorna o identificador do título.
     *
     * @return O identificador do título.
     */
    public String getId() {
        return id;
    }

    /**
     * Define um novo identificador para o título.
     *
     * @param id O novo identificador do título.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retorna o preço total do título.
     *
     * @return O preço do título.
     */
    public double getPreco() {
        return preco;
    }

    /**
     * Define um novo preço para o título.
     *
     * @param preco O novo preço do título.
     */
    public void setPreco(double preco) {
        this.preco = preco;
    }

    /**
     * Verifica se o título foi pago.
     *
     * @return true se o título foi pago, false caso contrário.
     */
    public boolean isPago() {
        return pago;
    }

    /**
     * Define o estado de pagamento do título.
     *
     * @param pago O novo estado de pagamento do título.
     */
    public void setPago(boolean pago) {
        this.pago = pago;
    }

    /**
     * Retorna a lista de produtos associados a este título.
     *
     * @return A lista de produtos do carrinho.
     */
    public List<Produto> getProdutosCarrinho() {
        return produtosCarrinho;
    }

    /**
     * Define a lista de produtos associados a este título.
     *
     * @param produtosCarrinho A nova lista de produtos do carrinho.
     */
    public void setProdutosCarrinho(List<Produto> produtosCarrinho) {
        this.produtosCarrinho = produtosCarrinho;
    }
}

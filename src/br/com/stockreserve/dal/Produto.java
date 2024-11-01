/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.stockreserve.dal;
import java.time.LocalDate;

/**
 * Representa um produto disponível no sistema, contendo informações
 * sobre seu identificador, nome, preço, quantidade disponível e data
 * de vencimento.
 * 
 * A classe armazena os detalhes essenciais de um produto, que podem ser
 * utilizados em operações de venda, gerenciamento de estoque e controle
 * de validade.
 * 
 * @author leog4
 * @version 2.0
 */
public class Produto {
    private int id;
    private String nome;
    private double preco;
    private int quantidade;
    private LocalDate vencimento;
    
    /**
     * Construtor para criar um objeto Produto.
     *
     * @param id Identificador único do produto.
     * @param nome Nome do produto.
     * @param preco Preço do produto.
     * @param quantidade Quantidade disponível do produto.
     * @param vencimento Data de vencimento do produto.
     */

    public Produto(int id, String nome, double preco, int quantidade, LocalDate vencimento) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.vencimento = vencimento;
    }

    /**
     * Retorna o identificador do produto.
     *
     * @return O identificador do produto.
     */
    public int getId() { 
        return id; 
    }
    
    /**
     * Retorna o nome do produto.
     *
     * @return O nome do produto.
     */
    public String getNome() { 
        return nome; 
    }
    
    /**
     * Retorna o preço do produto.
     *
     * @return O preço do produto.
     */
    public double getPreco() { 
        return preco; 
    }
    
    /**
     * Retorna a quantidade disponível do produto.
     *
     * @return A quantidade do produto.
     */
    public int getQuantidade() { 
        return quantidade; 
    }
    
    /**
     * Retorna a data de vencimento do produto.
     *
     * @return A data de vencimento do produto.
     */
    public LocalDate getVencimento() { 
        return vencimento; 
    }
    
    /**
     * Define uma nova quantidade disponível do produto.
     *
     * @param quantidade A nova quantidade do produto.
     */
    public void setQuantidade(int quantidade) { 
        this.quantidade = quantidade; 
    }
}

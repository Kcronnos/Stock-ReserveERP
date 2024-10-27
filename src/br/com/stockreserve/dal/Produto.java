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
    private int id;
    private String nome;
    private double preco;
    private int quantidade;
    private LocalDate vencimento;

    public Produto(int id, String nome, double preco, int quantidade, LocalDate vencimento) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.vencimento = vencimento;
    }

    // Getters e setters
    public int getId() { 
        return id; }
    
    public String getNome() { 
        return nome; }
    
    public double getPreco() { 
        return preco; }
    
    public int getQuantidade() { 
        return quantidade; }
    
    public LocalDate getVencimento() { 
        return vencimento; }
    
    public void setQuantidade(int quantidade) { 
        this.quantidade = quantidade; }
}

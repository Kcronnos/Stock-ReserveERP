/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.stockreserve.dal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author leog4
 */
public class Criptografia {

    /**
     * Criptografa uma senha utilizando o algoritmo SHA-256.
     *
     * @param senha A senha em formato de String que será criptografada.
     * @return Uma representação hexadecimal da senha criptografada.
     * @throws RuntimeException Se ocorrer um erro ao obter a instância do
     * algoritmo de hash.
     * 
     * @author leog4
     * @version 2.0
     */
    public static String criptografar(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

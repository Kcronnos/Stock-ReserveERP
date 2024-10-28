/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.stockreserve.dal;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author leog4
 */
public class JsonUtil {

    public static String produtosParaJson(List<Produto> produtos) {
        JSONArray jsonArray = new JSONArray();
        for (Produto produto : produtos) {
            JSONObject jsonProduto = new JSONObject();
            jsonProduto.put("id", produto.getId());
            jsonProduto.put("nome", produto.getNome());
            jsonProduto.put("preco", produto.getPreco());
            jsonProduto.put("quantidade", produto.getQuantidade());
            if (produto.getVencimento() != null) {
               jsonProduto.put("vencimento", produto.getVencimento().toString());
            } else {
               jsonProduto.put("vencimento", null);
            }

            jsonArray.add(jsonProduto);
        }
        return jsonArray.toJSONString();
    }

    public static List<Produto> jsonParaProdutos(String jsonString) throws ParseException {
        List<Produto> produtos = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(jsonString);

        for (Object obj : jsonArray) {
            JSONObject jsonProduto = (JSONObject) obj;
            Long idLong = (Long) jsonProduto.get("id");
            int id = idLong != null ? idLong.intValue() : 0;
            String nome = (String) jsonProduto.get("nome");
            double preco = (double) jsonProduto.get("preco");
            int quantidade = ((Long) jsonProduto.get("quantidade")).intValue();
            LocalDate vencimento = null;
            if(jsonProduto.get("vencimento") != null){
              vencimento = LocalDate.parse((String) jsonProduto.get("vencimento"));   
            }
            produtos.add(new Produto(id, nome, preco, quantidade, vencimento));
        }
        return produtos;
    }
}
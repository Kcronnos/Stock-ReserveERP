package br.com.stockreserve.dal;

import java.sql.*;
/**
 *
 * @author Felipe
 */
public class ModuloConexao {
    //Método responsavel por estabeler conexão com o banco
    public static Connection conector () { 
        Connection conexao = null;
        
        //A linha abaixo chama o driver
        String driver = "com.mysql.cj.jdbc.Driver";
        
        //Armazenando informações referente ao banco
        String url = "jdbc:mysql://localhost:3306/dberp";
        String user = "elinaldo";
        String password = "snow2501";
        
        //Estabelecendo a conexao com o banco
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            //A linha abaixo serve de apoio para esclarecer o erro
            System.out.println(e);
            return null;
        }
        
    }
}

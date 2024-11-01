package br.com.stockreserve.dal;

import java.sql.*;

/**
 * A classe ModuloConexao é responsável por gerenciar a conexão com o banco de
 * dados. Esta classe fornece um método estático para estabelecer uma conexão
 * com um banco de dados MySQL, facilitando a interação com o sistema de
 * gerenciamento de banco de dados.
 *
 * @author Felipe
 * @version 2.0
 */
public class ModuloConexao {

    /**
     * Estabelece uma conexão com o banco de dados.
     *
     * Este método tenta conectar ao banco de dados utilizando as informações de
     * conexão pré-definidas. Em caso de sucesso, retorna a conexão. Caso
     * contrário, retorna null e imprime o erro no console.
     *
     * @return Connection A conexão com o banco de dados se for bem-sucedida, ou
     * null se a conexão falhar.
     */
    public static Connection conector() {
        Connection conexao = null;

        //A linha abaixo chama o driver
        String driver = "com.mysql.cj.jdbc.Driver";

        //Armazenando informações referente ao banco
        String url = "jdbc:mysql://localhost:3306/dberp";
        String user = "root";
        String password = "";

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

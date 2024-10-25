package br.com.stockreserve.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class IALimiteDeEstoque {

    private Connection conector;

    // Construtor que recebe o objeto de conexão
    public IALimiteDeEstoque(Connection conector) {
        this.conector = conector;
    }

    // Método para verificar o estoque e emitir alertas quando necessário
    public void verificarEstoque() {
        String query = "SELECT idproduto, nomeproduto, preco, quantidade, vencimento, limite_minimo "
                     + "FROM produtos WHERE quantidade < limite_minimo";
        try (PreparedStatement stmt = conector.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            boolean alertaEmitido = false;
            //Quando ele entrar nesse while ele vai entrar apenas com as linhas de produtos que estão abaixo do permitido, depois disso ele emite o alerta de cada produto.
            while (rs.next()) {
                int idProduto = rs.getInt("idproduto");
                String nomeProduto = rs.getString("nomeproduto");
                double preco = rs.getDouble("preco");
                int quantidade = rs.getInt("quantidade");
                double peso = rs.getDouble("peso");
                double pesoTotalEstoque = rs.getDouble("pesototalestoque");
                Date vencimento = rs.getDate("vencimento");
                int limiteMinimo = rs.getInt("limite_minimo");

                // Emite alerta se o estoque estiver abaixo do permitido
                System.out.println("⚠️ Alerta: O estoque do produto '" + nomeProduto + "' (ID: " + idProduto + ") está abaixo do limite permitido.");
                System.out.println("Preço: R$" + preco + " | Quantidade atual: " + quantidade + " | Limite mínimo: " + limiteMinimo);
                System.out.println("Peso unitário: " + peso + "kg | Peso total em estoque: " + pesoTotalEstoque + "kg");
                //Aqui é só pra verificar se a data ta no formato certo de ser impresso.
                if (vencimento != null) {
                    System.out.println("Data de vencimento: " + vencimento);
                } else {
                    System.out.println("Data de vencimento: Não aplicável");
                }
                
                alertaEmitido = true;
            }
            //Se tiver tudo ok, ele printa essa mensagem
            if (!alertaEmitido) {
                System.out.println("✅ Todos os produtos estão dentro do limite permitido.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao verificar o estoque: " + e.getMessage());
        }
    }
}

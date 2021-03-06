package br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique;

import br.com.zup.edu.ligaqualidade.desafiobiblioteca.DadosDevolucao;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.DadosEmprestimo;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.EmprestimoConcedido;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.repository.EmprestimoConcedidoRepository;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.repository.ExemplarRepository;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.repository.UsuarioRepository;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.service.DevolverEmprestimoService;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.service.RegistrarEmprestimoService;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosExemplar;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosLivro;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosUsuario;

import java.time.LocalDate;
import java.util.Set;

public class Solucao {

    /**
     * Você precisa implementar o código para executar o fluxo
     * o completo de empréstimo e devoluções a partir dos dados
     * que chegam como argumento.
     *
     * Caso você queira pode adicionar coisas nas classes que já existem,
     * mas não pode alterar nada.
     */

    /**
     * @param livros                            dados necessários dos livros
     * @param exemplares                        tipos de exemplares para cada livro
     * @param usuarios                          tipos de usuarios
     * @param emprestimos                       informações de pedidos de empréstimos
     * @param devolucoes                        informações de devoluções, caso exista.
     * @param dataParaSerConsideradaNaExpiracao aqui é a data que deve ser utilizada para verificar expiração
     * @return
     */
    public static Set<EmprestimoConcedido> executa(Set<DadosLivro> livros,
                                                   Set<DadosExemplar> exemplares,
                                                   Set<DadosUsuario> usuarios,
                                                   Set<DadosEmprestimo> emprestimos,
                                                   Set<DadosDevolucao> devolucoes,
                                                   LocalDate dataParaSerConsideradaNaExpiracao) {

        UsuarioRepository usuarioRepository = new UsuarioRepository(usuarios);
        ExemplarRepository exemplarRepository = new ExemplarRepository(exemplares);
        EmprestimoConcedidoRepository emprestimoConcedidoRepository = new EmprestimoConcedidoRepository();

        RegistrarEmprestimoService registrarEmprestimoService = new RegistrarEmprestimoService(usuarioRepository, exemplarRepository, emprestimoConcedidoRepository);
        DevolverEmprestimoService devolverEmprestimoService = new DevolverEmprestimoService(emprestimoConcedidoRepository);

        registrarEmprestimoService.registrar(emprestimos, dataParaSerConsideradaNaExpiracao);
        devolverEmprestimoService.devolver(devolucoes);

        return emprestimoConcedidoRepository.get();
    }

}

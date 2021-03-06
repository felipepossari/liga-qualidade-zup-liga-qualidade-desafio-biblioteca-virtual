package br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.service;

import br.com.zup.edu.ligaqualidade.desafiobiblioteca.DadosEmprestimo;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.EmprestimoConcedido;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.repository.EmprestimoConcedidoRepository;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.repository.ExemplarRepository;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.modifique.repository.UsuarioRepository;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosExemplar;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.DadosUsuario;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.TipoExemplar;
import br.com.zup.edu.ligaqualidade.desafiobiblioteca.pronto.TipoUsuario;

import java.time.LocalDate;
import java.util.Set;

public class RegistrarEmprestimoService {

    public static final int TEMPO_EMPRESTIMO_MAX = 60;
    UsuarioRepository usuarioRepository;
    EmprestimoConcedidoRepository emprestimoConcedidoRepository;
    ExemplarDisponivelService exemplarDisponivelService;

    public RegistrarEmprestimoService(UsuarioRepository usuarioRepository,
                                      ExemplarRepository exemplarRepository,
                                      EmprestimoConcedidoRepository emprestimoConcedidoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.emprestimoConcedidoRepository = emprestimoConcedidoRepository;
        this.exemplarDisponivelService = new ExemplarDisponivelService(exemplarRepository, emprestimoConcedidoRepository);
    }

    public void registrar(Set<DadosEmprestimo> emprestimos, LocalDate dataParaSerConsideradaNaExpiracao) {

        for (DadosEmprestimo emprestimo : emprestimos) {

            if (isTempoEmprestimoInvalid(emprestimo)) {
                continue;
            }

            DadosUsuario dadosUsuario = usuarioRepository.get(emprestimo.idUsuario);
            if (isUsuarioPadrao(dadosUsuario) && isExemplarRestrito(emprestimo)) {
                continue;
            }

            EmprestimoConcedido emprestimoConcedido = buildExemplarConcedido(emprestimo);

            if(hasEmprestimoVencido(dadosUsuario.idUsuario, dataParaSerConsideradaNaExpiracao)){
                continue;
            }

            emprestimoConcedidoRepository.regitrar(emprestimoConcedido);
        }
    }

    private EmprestimoConcedido buildExemplarConcedido(DadosEmprestimo emprestimo) {
        Integer idExemplar = exemplarDisponivelService.getId(emprestimo.idLivro, emprestimo.tipoExemplar);

        EmprestimoConcedido emprestimoConcedido = new EmprestimoConcedido(emprestimo.idPedido, emprestimo.idUsuario,
                idExemplar,
                LocalDate.now().plusDays(emprestimo.tempo));
        return emprestimoConcedido;
    }

    private boolean hasEmprestimoVencido(int idUsuario, LocalDate dataParaSerConsideradaNaExpiracao){
        var emprestimosEmAberto = emprestimoConcedidoRepository.getEmprestimosEmAberto(idUsuario);
        if(!emprestimosEmAberto.isEmpty() &&
                isDataPrevistaDevolucaoVencida(dataParaSerConsideradaNaExpiracao, emprestimosEmAberto)){
            return true;
        }
        return false;
    }

    private boolean isDataPrevistaDevolucaoVencida(LocalDate dataParaSerConsideradaNaExpiracao, Set<EmprestimoConcedido> emprestimosEmAberto) {
        return emprestimosEmAberto.stream()
                .anyMatch(it -> it.dataPrevistaDevolucao.isBefore(dataParaSerConsideradaNaExpiracao));
    }

    private boolean isExemplarRestrito(DadosEmprestimo emprestimo) {
        return TipoExemplar.RESTRITO.equals(emprestimo.tipoExemplar);
    }

    private boolean isUsuarioPadrao(DadosUsuario dadosUsuario) {
        return TipoUsuario.PADRAO.equals(dadosUsuario.padrao);
    }

    private boolean isTempoEmprestimoInvalid(DadosEmprestimo emprestimo) {
        return emprestimo.tempo > TEMPO_EMPRESTIMO_MAX;
    }
}

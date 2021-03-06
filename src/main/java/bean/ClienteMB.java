package bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.primefaces.event.FlowEvent;

import dao.AnimalService;
import dao.ClienteService;
import dao.EnderecoService;
import dao.exceptions.NonexistentEntityException;
import dao.util.JPAUtil;
import dao.util.Validation;
import models.Animal;
import models.Cliente;
import models.Endereco;


@ManagedBean
@RequestScoped
@ViewScoped
public class ClienteMB {

    
    ClienteService dao = new ClienteService(JPAUtil.EMF);
    EnderecoService daoEndereco = new EnderecoService(JPAUtil.EMF);
    AnimalService daoAnimal = new AnimalService(JPAUtil.EMF);
    private Endereco endereco = new Endereco();
    private String mensagem = "";
    private List<Cliente> clientes = new ArrayList<Cliente>();
    private Cliente cliente = new Cliente();
    private String clientePesquisado;
    private Animal animal = new Animal();
    private List<Animal> animais = new ArrayList<Animal>();
   

    public ClienteMB() {
        pesquisar();
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagemExclusao() {
        return mensagem;
    }

    public void setMensagemExclusao(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagemAlteracao() {
        return mensagem;
    }

    public void setMensagemAlteracao(String mensagem) {
        this.mensagem = mensagem;
    }
    
    //metodo usado para setar o cliente logado e add o animal
    public void salvarAnimal(Cliente cliente) throws NonexistentEntityException, Exception{
    	Validation validacao = new Validation();
    	this.setCliente(cliente);
    	cliente.addAnimal(animal);
    	daoAnimal.createAnimal(animal);
    	dao.edit(cliente);
    	this.setMensagem(" cadastrado(a) com sucesso! ");
        validacao.mensagemConfirmarCadastro(mensagem);
    }

    //metodo de inserção no banco de dados
    public void inserir() {
    	Validation validacao = new Validation();
        try {
        	Cliente aux = dao.findClienteEmail(cliente.getEmail()); //usado para verificar se o email ja eh cadastrado
        	
        	if(aux == null){
	        	daoEndereco.createEndereco(endereco);
	        	cliente.setEndereco(endereco);
	            dao.create(cliente);
	            this.setMensagem(this.cliente.getNome() + " cadastrado(a) com sucesso! ");
	            validacao.mensagemConfirmarCadastro(mensagem);
	            cliente = new Cliente();
	            endereco = new Endereco();
        	}else{
        		validacao.mensagemConfirmarCadastro("O Email " + cliente.getEmail() + " ja é cadastrado");
        		
        	}
        } catch (Exception ex) {
            setMensagem(this.cliente.getNome() + "já existe no sistema, cadastro não realizado!");
            Logger.getLogger(ClienteMB.class.getName()).log(Level.SEVERE, null, ex);
        }

        pesquisar();
    }

    public void excluir(long idCliente, long idEndereco) {
        try {
            dao.destroy(idCliente);
            daoEndereco.destroy(idEndereco);
            setMensagemExclusao(this.cliente.getNome() + "  foi excluído(a) com sucesso!");
            cliente = new Cliente();
            endereco = new Endereco();
        } catch (NonexistentEntityException ex) {
            this.setMensagemExclusao("id não existe");
            Logger.getLogger(ClienteMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        pesquisar();
    }

    public void alterar() throws Exception {
        try {
            dao.edit(cliente);
            setMensagemAlteracao(this.cliente.getNome() + " foi alterado(a) com sucesso!");
            cliente = new Cliente();
        } catch (NonexistentEntityException ex) {
            this.setMensagemAlteracao("id não existe");
            Logger.getLogger(ClienteMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        pesquisar();
    }

    public void carregar(Long id) {
        Cliente a = dao.findCliente(id);
        cliente.setNome(a.getNome());
        cliente.setCPF(a.getCPF());
        cliente.setSenha(a.getSenha());
        cliente.setEmail(a.getEmail());
        cliente.setId(a.getId());

        if (cliente == null) {
            cliente = new Cliente();
        }
    }
    
    public void carregar1(Cliente cliente){
    	this.setCliente(cliente);
    }
    
    public void carregarAnimal(Animal animal){
    	this.setAnimal(animal);
    }

    public int pesquisar() {
        clientes = dao.findClienteEntities();
        return clientes.size();
    }

    public void pesquisarClientes() {
        clientes = new ArrayList<Cliente>();
        for (Cliente a : dao.findClienteEntities()) {
            if ((a.getEmail().toLowerCase().contains(clientePesquisado) || (a.getNome().toLowerCase().contains(clientePesquisado)))) {
                clientes.add(a);

            }
        }
        setClientePesquisado("");
       
    }
    
    public int pesquisarAnimalTamanho() {
        animais = cliente.getAnimais();
        return animais.size();

    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Cliente> getClienteList() {
        return clientes;
    }

    public void setClienteList(ArrayList<Cliente> clientes) {
        this.clientes = clientes;
    }

    public List<Cliente> pesquisarCliente() {
        return dao.findClienteEntities();
    }

    public String getClientePesquisado() {
        return clientePesquisado;
    }

    public void setClientePesquisado(String clientePesquisado) {
        this.clientePesquisado = clientePesquisado;
    }

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Animal getAnimal() {
		return animal;
	}

	public void setAnimal(Animal animal) {
		this.animal = animal;
	}

	
}
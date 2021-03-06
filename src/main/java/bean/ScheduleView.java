package bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;	// dropdown servicos
import java.util.Map;	// dropdown servicos
import java.util.List;	// dropdown servicos
import java.util.ArrayList;	// dropdown servicos

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
 







import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import dao.AnimalService;
import dao.FuncionarioService;
import dao.ServicoService;	// dropdown servicos
import dao.util.JPAUtil;	// dropdown servicos
import models.Agenda;
import models.Animal;
import models.Cliente;
import models.Funcionario;
import models.Servico;	// dropdown servicos
 
@ManagedBean
@ViewScoped
public class ScheduleView implements Serializable {
 
    private ScheduleModel eventModel;
    private ScheduleModel lazyEventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    
    private Map<String, Map<String, String>> data = new HashMap<String, Map<String,String>>();
    
    private Agenda angenda = new Agenda();
    private AgendaMB agendaMB = new AgendaMB();
    
    private String hora;
    private Map<String, String> horario = new HashMap<String, String>();
    
    private ServicoService servicoService = new ServicoService(JPAUtil.EMF);	// dropdown servicos
    private Map<String, String> mapaServicos;	// dropdown servicos
    private List<Servico> servicos = new ArrayList<Servico>();	// dropdown servicos
    private String servico;	// dropdown servicos
    
    private FuncionarioService funcionarioService = new FuncionarioService(JPAUtil.EMF);
    private Map<String, String> mapaFuncionarios;
    private List<Funcionario> funcionarios = new ArrayList<Funcionario>();
    private String funcionario;
    
    private AnimalService daoAnimal = new AnimalService(JPAUtil.EMF);
    private List<Animal> animais;
    private Map<String, String> mapaAnimais = new HashMap<String, String>();
    private String animal;
    
    private Cliente cliente = new Cliente();
    
    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        lazyEventModel = new LazyScheduleModel() {
             
            @Override
            public void loadEvents(Date start, Date end) {
                Date random = getRandomDate(start);
                addEvent(new DefaultScheduleEvent("Lazy Event 1", random, random));
                 
                random = getRandomDate(start);
                addEvent(new DefaultScheduleEvent("Lazy Event 2", random, random));
            }   
        };
        
        servicos = servicoService.findServicoEntities();  // dropdown servicos
        funcionarios = funcionarioService.findFuncionarioEntities();
        mapaServicos = new HashMap<String, String>();	// dropdown servicos
        Map<String, String> map = new HashMap<String, String>();
        for(Servico s: servicos) {	// dropdown servicos
        	mapaServicos.put(s.getTipo(), s.getTipo());	// dropdown servicos
        	map = new HashMap<String, String>();
        	for(Funcionario f: funcionarios) {
        		map.put(f.getNome(), f.getNome());
        	}
        	data.put(s.getTipo(), map);
        }	// dropdown servicos
        
        for(int i = 7; i <= 17; i++) {
        	if(i != 11 || i != 12) {
        		horario.put(""+i, ""+i);
        	}
        }
        //for(Animal a: cliente.getAnimais()){
        	//mapaAnimais.put(a.getNome(), a.getNome());
        //}
    }
    
     
    public Date getRandomDate(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.add(Calendar.DATE, ((int) (Math.random()*30)) + 1);    //set random day of month
         
        return date.getTime();
    }
     
    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);
         
        return calendar.getTime();
    }
     
    public ScheduleModel getEventModel() {
        return eventModel;
    }
     
    public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }
 
    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
 
        return calendar;
    }
     
    public ScheduleEvent getEvent() {
        return event;
    }
 
    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }
     
    @SuppressWarnings("deprecation")
	public void addEvent(ActionEvent actionEvent) {
        if(event.getId() == null)
            eventModel.addEvent(event);
        else
            eventModel.updateEvent(event);
         
        event.getStartDate().setHours(Integer.parseInt(hora));
        
        event = new DefaultScheduleEvent();
    }
     
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }
     
    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }
     
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
     
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
    
    public void onServicoChange() {
        if(servico !=null && !servico.equals(""))
            mapaFuncionarios = data.get(servico);
        else
        	mapaFuncionarios = new HashMap<String, String>();
    }
     
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    //  dropdown servicos
    //---------------------------
    public Map<String, String> getMapaServicos() {
    	return this.mapaServicos;
    }
    
    public void setServico(String servico) {
    	this.servico = servico;
    }
    
    public String getServico() {
    	return this.servico;
    }
    //--------------------------

	public Map<String, String> getMapaFuncionarios() {
		return mapaFuncionarios;
	}

	public void setMapaFuncionarios(Map<String, String> mapaFuncionarios) {
		this.mapaFuncionarios = mapaFuncionarios;
	}

	public List<Funcionario> getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(List<Funcionario> funcionarios) {
		this.funcionarios = funcionarios;
	}

	public String getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(String funcionario) {
		this.funcionario = funcionario;
	}

	public Map<String, String> getHorario() {
		return horario;
	}

	public void setHorario(Map<String, String> horario) {
		this.horario = horario;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public Map<String, String> getMapaAnimais() {
		return mapaAnimais;
	}

	public void setMapaAnimais(Map<String, String> mapaAnimais) {
		this.mapaAnimais = mapaAnimais;
	}

	public String getAnimal() {
		return animal;
	}

	public void setAnimal(String animal) {
		this.animal = animal;
	}

	public List<Animal> getAnimais() {
		return animais;
	}

	public void setAnimais(List<Animal> animais) {
		this.animais = animais;
	}

	public AnimalService getDaoAnimal() {
		return daoAnimal;
	}

	public void setDaoAnimal(AnimalService daoAnimal) {
		this.daoAnimal = daoAnimal;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}


	public Agenda getAngenda() {
		return angenda;
	}


	public void setAngenda(Agenda angenda) {
		this.angenda = angenda;
	}


	public AgendaMB getAgendaMB() {
		return agendaMB;
	}


	public void setAgendaMB(AgendaMB agendaMB) {
		this.agendaMB = agendaMB;
	}
}
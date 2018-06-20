package it.polito.tdp.flightdelays.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import it.polito.tdp.flightdelays.db.FlightDelaysDAO;

public class Simulatore {

	//PARAMETRI (variabili per modificare la simulazione)
	private int numPas;
	private int numVoli;
	private Airline linea;
	private List<Airport> airports;
	
	private FlightDelaysDAO dao;
	Random random;
	
	//MODELLO DEL MONDO (fotografia del sistema)
	private List<Passeggero> passeggeri;
	
	//CODA DEGLI EVENTI
	LinkedList<Event> queue;
	
	//VALORI OUTPUT
	//è la mia stessa lista di passeggeri, dove vado a memorizzare i ritardi, voglio quelli
	
	
	public void init(int numPas, int numVoli, List<Airport> listaAereoporti, Airline linea, FlightDelaysDAO dao) {
		this.numPas=numPas;
		this.numVoli=numVoli;
		this.airports=listaAereoporti;
		this.linea=linea;
		this.dao = dao;
		 
		 queue = new LinkedList();
		 
		 random = new Random();
		 
		 
		 //creo numPas passeggeri e li assegno ogni volta ad un aereoporto casuale
		 passeggeri = new ArrayList<>(); //li salvo per poi stamparne il ritardo
		 for(int i=0; i<numPas; i++) {
				
			 //posizionare numPas passeggeri in modo casuale tra gli aeroporti disponibili
				int x = random.nextInt(airports.size()); // nextInit da un numero da 0 al numero inserito tra () escluso
				Airport casualAirport = airports.get(x); //prendo aereoporto casuale
				
				Passeggero p = new Passeggero(i, numVoli);
				passeggeri.add(p);
			    
				//prendo primo volo di quel aereoporto verso un aereoporto della mia linea
				//e ci metto su il passeggero corrente, devo cercare il primo e creo evento
				                                                                            //01/01/2015 0h 0min 0sec
				Flight volo = dao.findFirstFlight(linea, casualAirport.getId(), LocalDateTime.of(2015, 1, 1, 0, 0, 0));
				p.accumuloRitardo(volo.getArrivalDelay()); //accumulo ritardo
				p.voloEffettuato();
			
				if(volo != null) {
				    Event e = new Event(p, casualAirport, volo.getScheduledDepartureDate());
				    queue.add(e);
				}
			}
	}

	public void run() {
		
		Event e;
		while((e = this.queue.poll()) != null) {
			processEvent(e);
		}
		
	}
	
	private void processEvent(Event e) {

		//ARRIVO PASSEGGERO==PARTENZA STESSO PASSEGGERO
		Passeggero passeggero = e.getPasseggero();
		
		//se ancora può fare voli lo faccio imbarcare
		if(passeggero.getVoli() < numVoli) {
			
			
			//trova primo volo dopo data passata, da questo aereoporto, verso aereoporto della linea
			Flight volo = dao.findFirstFlight(linea, e.getAirport().getId(), e.getData());
			
			if(volo != null) {
			    Event e2 = new Event(e.getPasseggero(), e.getAirport(), volo.getScheduledDepartureDate());
				passeggero.accumuloRitardo(volo.getArrivalDelay()); //accumulo ritardo
				passeggero.voloEffettuato();//diminuisco il numero di voli
			    queue.add(e2);
			}
				
		}
	}


	public List<Passeggero> getResultati() {
		return passeggeri;
	}


}

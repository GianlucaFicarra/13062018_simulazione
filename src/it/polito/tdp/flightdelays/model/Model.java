package it.polito.tdp.flightdelays.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flightdelays.db.FlightDelaysDAO;


public class Model {

	private List<Airline> listaLinee; //per rendere la linea desiderata
	
	private List<Airport> listaAereoporti; //i miei nodi
	
	private List<Tratta> tratte; //archi
	
	private FlightDelaysDAO dao;
	private Simulatore sim;

	/*
	 Nel grafo vOGLIO AEREPORTI DI ARRIVO E DESTINAZIONE, MA LA CLASSE AIRPORT
	  HA SOLO GLI ID, CREO UNA CLASSE IDMAP per convertire id in oggetti
	*/
	SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	AirportIdMap airportIdMap;
	
	
	public Model() {
		dao= new FlightDelaysDAO();
		sim= new Simulatore();
		

		this.listaLinee =  dao.loadAllAirlines(); //carico tutte le linee disponibili
		
		this.listaAereoporti= new LinkedList<Airport>(); //vertici
		this.airportIdMap = new AirportIdMap();
		this.tratte = new LinkedList<>();
	
	}

	
	public List<Airline> getLinee() { //popolo la tendina con le linee consentite
		return listaLinee;
	}


	public void createGraph(Airline linea) {
		
		        //creo grafo.... dichiarazione standard
				grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
				
				//carico vertici (uso mappa per avere oggetti aereoporti)
				listaAereoporti = dao.getAllAirportsFromAirline(airportIdMap, linea);
				   
				//creato grafo e vertici li aggiungo
				Graphs.addAllVertices(grafo, this.listaAereoporti);
				
				// caricamento tratte passanti per la mia linea
				tratte= dao.getTratte(airportIdMap, linea);
				
				//calcolo il peso della tratta==Archi
				//tramite la media dei ritardi e le distanze in longitudine e latitudine:
				 for(Tratta t: tratte) {
					
					/*aereoporto sorgente e destinazione 
					 * voglio gli oggetti non gli id quindi uso idmap*/
					Airport sourceAirport = t.getOrigine();
					Airport destinationAirport = t.getDestination();
					
					//se sono diversi
					if (sourceAirport != null && destinationAirport != null && !sourceAirport.equals(destinationAirport)) {
		
						//1-distanze
						double distanza = LatLngTool.distance(new LatLng(sourceAirport.getLatitude(), sourceAirport.getLongitude()),
								                            new LatLng(destinationAirport.getLatitude(), destinationAirport.getLongitude()),
								                            LengthUnit.KILOMETER);
						
					    //avendo settato e calcolato il valore 
						double peso = (double)t.getAvg() / (double)distanza;
						t.setPeso(peso);
						
						//vado ad inserire l'arco ed il peso appena calcolato
						Graphs.addEdge(grafo, sourceAirport, destinationAirport, peso);
					}
				}
				
				//stampo di default vertici e archi
				System.out.println("\n Numero di vertici per il grafo: "+grafo.vertexSet().size());
				System.out.println("\n Numero di archi per il grafo: "+grafo.edgeSet().size());
	}



	//dovo aver creato il grafo e popolato le tratte prendo le pù grosse
	public List<Tratta> getTrattePeggiori() {
		Collections.sort(tratte);  //ordine di peso crescente
		return tratte.subList(0, 10); //voglio solo le 10 tratte di peso max
	}


	public List<Passeggero>  simula(int numPas, int numVoli, Airline linea) {
		sim.init(numPas, numVoli, listaAereoporti, linea, dao); 
		sim.run();
		
		return sim.getResultati();
	}





}
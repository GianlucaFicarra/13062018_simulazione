package it.polito.tdp.flightdelays.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flightdelays.db.FlightDelaysDAO;
import it.polito.tdp.flightdelays.db.Tratta;

public class Model {

	private List<Airline> listaLinee;
	private List<Airport> listaAereoporti;
	private List<Flight> listaVoli;
	private List<Tratta> tratte;
	private FlightDelaysDAO dao;
	
	SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> grafo;

	/*
	 Nel grafo vOGLIO AEREPORTI DI ARRIVO E DESTINAZIONE, MA LA CLASSE AIRPORT
	  HA SOLO GLI ID, CREO UNA CLASSE IDMAP
	*/
		AirportIdMap airportIdMap;
	
	public Model() {
		dao= new FlightDelaysDAO();

		this.listaLinee =  dao.loadAllAirlines();
		this.listaAereoporti=  dao.loadAllAirports();
		this.listaVoli= new LinkedList();
		
	
		   airportIdMap = new AirportIdMap();
		   listaAereoporti = dao.getAllAirports(airportIdMap);
	}



	public List<Airline> getLinee() {
		return listaLinee;
	}


	public void createGraph(Airline linea) {
		
		//creo grafo.... dichiarazione standard
				grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
				
				//creato grafo aggiungo i vertici dalla lista di aereoporti
				Graphs.addAllVertices(grafo, this.listaAereoporti);
				
				listaVoli= dao.getFlightByAirline(linea, airportIdMap);
				
				tratte= dao.getAvg(airportIdMap, linea);
				
				//aggiungo i collegameti cioè gli archi ed itero sulle linee
				
				for (Flight r : listaVoli) {
					
					/*aereoporto sorgente e destinazione 
					 * voglio gli oggetti non gli id quindi uso idmap*/
					
					Airport sourceAirport = r.getOrigin();
					Airport destinationAirport = r.getDestination();
					
					//se sono diversi
					if (!sourceAirport.equals(destinationAirport)) {
					
						//calcolo il peso tramite la media dei ritardi e le distanze in longitudine e latitudine:
						double distanza = LatLngTool.distance(new LatLng(sourceAirport.getLatitude(), sourceAirport.getLongitude()),
								                            new LatLng(destinationAirport.getLatitude(), destinationAirport.getLongitude()),
								                            LengthUnit.KILOMETER);
						
						double media=0.0;
					    for(Tratta t: tratte) {
						
						if(sourceAirport.equals(t.getOrigine()) && destinationAirport.equals(t.getDestination())) {
								media=t.getAvg();
								t.setDistanza(distanza);
								t.setPeso();
						}
						
						
						
					}
						
						double peso= media/distanza;
						
						//vado ad inserire l'arco ed il peso appena calcolato
						Graphs.addEdge(grafo, sourceAirport, destinationAirport, peso);
					}
				}
				
				//stampo di default vertici e archi
				System.out.println("\n Numero di vertici per il grafo: "+grafo.vertexSet().size());
				System.out.println("\n Numero di archi per il grafo: "+grafo.edgeSet().size());
	}



	public List<Tratta> getTrattePeggiori() {
		Collections.sort(tratte);
		return tratte.subList(0, 10);
	}




}

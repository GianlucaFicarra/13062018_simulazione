package it.polito.tdp.flightdelays.db;

import java.util.HashMap;
import java.util.Map;

import it.polito.tdp.flightdelays.model.Airline;

public class AirlineIdMap {
	
	//schema valido per tutte e tre le mappe
	
	private Map<String, Airline> map;
	
	public AirlineIdMap() {
		map = new HashMap<>();
	}
	
	public Airline get(String airlineId) {//dato id torna oggetto della mappa corrispondente
		return map.get(airlineId);
	}
	
	public Airline get(Airline airline) { //passo oggetto e mappa controlla di averlo
		Airline old = map.get(airline.getId());
		if (old == null) {
			map.put(airline.getId(), airline);
			return airline; //se nuovo lo aggiunge e lo restituisce
		}
		return old; //se gia ce l'ha restituisce il vecchio
	}
	
	public void put(Airline airline, String airlineId) {//per inserire nuovo aereoporto
		map.put(airlineId, airline);
	}
}

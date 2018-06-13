package it.polito.tdp.flightdelays.db;

import java.util.Comparator;

import it.polito.tdp.flightdelays.model.Airport;

public class Tratta implements Comparable<Tratta> {

	private double avg;
	private Airport origine;
	private Airport destination;
	
	private double distanza;
	private double peso;
	
	
	
	
	public Tratta(double avg, Airport origine, Airport destination) {
		super();
		this.avg = avg;
		this.origine = origine;
		this.destination = destination;
	}
	
	
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	public Airport getOrigine() {
		return origine;
	}
	public void setOrigine(Airport origine) {
		this.origine = origine;
	}
	public Airport getDestination() {
		return destination;
	}
	public void setDestination(Airport destination) {
		this.destination = destination;
	}


	public double getDistanza() {
		return distanza;
	}


	public void setDistanza(double distanza) {
		this.distanza = distanza;
	}


	public double getPeso() {
		return peso;
	}


	public void setPeso() {
		this.peso = (double)avg / (double)distanza;
	}

	
	@Override
	public int compareTo(Tratta t) {
		
		return Double.compare(t.getPeso(), this.peso);
	}


	@Override
	public String toString() {
		return "Tratta [avg=" + avg + ", origine=" + origine + ", destination=" + destination + ", distanza=" + distanza
				+ ", peso=" + peso + "]"+"\n";
	}
	
	
	
	
	
	
}

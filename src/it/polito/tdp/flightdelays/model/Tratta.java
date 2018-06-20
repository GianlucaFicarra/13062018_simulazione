package it.polito.tdp.flightdelays.model;

import java.util.Comparator;

import it.polito.tdp.flightdelays.model.Airport;

public class Tratta implements Comparable<Tratta> {

	private double peso;
	private Airport origine;
	private Airport destination;
	
	//valori per calcolare peso: media/distanza
	private double distanza;
	private double avg;

	
	public Tratta(double peso, Airport origine, Airport destination) {
		super();
		this.peso = peso;
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
	public void setPeso(double peso) {
		this.peso = peso;
	}


	@Override
	public int compareTo(Tratta t) {  //ordine peso decrescente, prima le peggiori
	
		return Double.compare(t.getPeso(), this.peso);
	}


	@Override
	public String toString() {
		return "Origine=" + origine + "- Destination=" + destination;
	}
	
	
	
	
	
	
}

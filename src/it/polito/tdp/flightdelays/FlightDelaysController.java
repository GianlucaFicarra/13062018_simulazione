package it.polito.tdp.flightdelays;

import java.util.ArrayList;
import java.util.List;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.flightdelays.model.Airline;
import it.polito.tdp.flightdelays.model.Model;
import it.polito.tdp.flightdelays.model.Passeggero;
import it.polito.tdp.flightdelays.model.Tratta;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FlightDelaysController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea txtResult;

    @FXML
    private ComboBox<Airline> cmbBoxLineaAerea;

    @FXML
    private Button caricaVoliBtn;

    @FXML
    private TextField numeroPasseggeriTxtInput;

    @FXML
    private TextField numeroVoliTxtInput;

    @FXML
    private Button btnSimula;


	private Model model;
	
    public void setModel(Model model) {
    	this.model = model;
    			
    			//setto i valori dell tendina
    	       cmbBoxLineaAerea.getItems().addAll(model.getLinee());
    	       btnSimula.setDisable(true); //lo disattivo
    	}
    
    @FXML
    void doCaricaVoli(ActionEvent event) {
    		
    		//salvo linea passato dall'utente
    		Airline linea = cmbBoxLineaAerea.getValue();

    		//verifico se ogetto linea non sia nulla
    		if (linea == null) {
    			txtResult.appendText("Scegliere una linea\n");
    			return;
    		}
    		
    		//creo un grafo per calcolare i confini
    		try {
    			model.createGraph(linea);
    		    btnSimula.setDisable(false); //lo attivo
	    			
	    			
	    		List<Tratta> list = new ArrayList<>();
	    		list= model.getTrattePeggiori();
	    		
	    		if(list == null) {
	        		txtResult.setText("Nessuna rotta trovata.");
	        		return;
	        	}
	    		
	    		txtResult.appendText("\nStampo le tratte peggiori: \n");
	    		int cont=1;
	    		for(Tratta t: list) {
	    			this.txtResult.appendText(String.format("%d %s\n", cont, t.toString()));
	    			cont++;
	    		}
	    		this.txtResult.appendText("\n\n");
	    			
    			
    		} catch (RuntimeException e) {
    			e.printStackTrace();
    			txtResult.appendText("Errore\n");
    			return;
    		}
    		
    		
  
    }

    
    @FXML
    void doSimula(ActionEvent event) {
    		
    	this.txtResult.clear();
    	
    		
    		String pas = numeroPasseggeriTxtInput.getText();
    		String voli = numeroVoliTxtInput.getText();
    		
    	    try {
    			int numPas = Integer.parseInt(pas);
        		int numVoli = Integer.parseInt(voli);
    			
        		//i futuri voli devono stare nella mia linea
        		Airline linea = cmbBoxLineaAerea.getValue();
        	
        		//creo il numero di passeggeri voluto:
        		List <Passeggero> passeggeri = new ArrayList<>();
        	
            	passeggeri= model.simula(numPas, numVoli, linea);  //setto il ritardo del passeggero come risultato
            	
            	txtResult.appendText("\n\nStampo il ritardo complessivo accumulato da ciascun passeggero: \n");
            	for(Passeggero p: passeggeri) {
            		txtResult.appendText(p.toString()+"\n");
            	}
        		
    		}catch (NumberFormatException e) {
        		this.txtResult.appendText("ERRORE: inserire valori numerici!!!\n");
        	}
    		
    		
    }

    @FXML
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert cmbBoxLineaAerea != null : "fx:id=\"cmbBoxLineaAerea\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert caricaVoliBtn != null : "fx:id=\"caricaVoliBtn\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert numeroPasseggeriTxtInput != null : "fx:id=\"numeroPasseggeriTxtInput\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert numeroVoliTxtInput != null : "fx:id=\"numeroVoliTxtInput\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'FlightDelays.fxml'.";

    }
    
	
}
